package suncertify.db;

import java.util.Random;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rabbuhl
 * Date: Apr 2, 2005
 * Time: 9:50:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestDB {

    private static final int maxRecords = 25;

    static class testDataThread implements Runnable {

        private DB data;
        private Random random = new Random();

        public testDataThread(DB data) {
            this.data = data;
        }

        private synchronized void dump(String[] result) {
            if (result != null) {
                for (int i = 0; i < result.length; i++) {
                    System.out.print(result[i].trim());
                    if (i + 1 < result.length) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
        }

        private void readTest(DB data, int recNo) {
            long cookie = -1;
            try {
                cookie = data.lock(recNo);
                dump(data.read(recNo));

            } catch (RecordNotFoundException e) {
            } catch (IOException e) {
            } finally {
                if (cookie != -1) {
                    try {
                        data.unlock(recNo, cookie);
                    } catch (RecordNotFoundException f) {
                    } catch (IOException e) {
                        System.out.println("Exception " + e.toString());
                    }
                }
            }
        }

        private void updateTest(DB data, int recNo) {
            long cookie = -1;
            try {
                String [] d = new String[6];
                d[5] = "Rick";

                cookie = data.lock(recNo);
                data.update(recNo, d, cookie);

            } catch (RecordNotFoundException e) {
            } catch (IOException e) {
                System.out.println("Exception " + e.toString());
            } finally {
                if (cookie != -1) {
                    try {
                        data.unlock(recNo, cookie);
                    } catch (RecordNotFoundException f) {
                    } catch (IOException e) {
                        System.out.println("Exception " + e.toString());
                    }
                }
            }
        }

        private void addTest(DB data) {
            String [] d = new String[6];
            try {

                d[0] = "A" + Thread.currentThread().getName();
                d[1] = "B";
                d[2] = "C";
                d[3] = "D";
                d[4] = "E";
                d[5] = null;

                int recNo = data.create(d);

            } catch (DuplicateKeyException e) {
                System.out.println("Exception " + e.toString() + " " + d[0]);
            } catch (IOException e) {
                System.out.println("Exception " + e.toString());
            }
        }

        private void deleteTest(DB data, int recNo) {
            long cookie = -1;
            try {
                cookie = data.lock(recNo);
                data.delete(recNo, cookie);

            } catch (RecordNotFoundException e) {
            } catch (IOException e) {
                System.out.println("Exception " + e.toString());
            } finally {
                if (cookie != -1) {
                    try {
                        data.unlock(recNo, cookie);
                    } catch (RecordNotFoundException e) {
                    } catch (IOException e) {
                        System.out.println("Exception " + e.toString());
                    }
                }
            }
        }

        private void matchTest(DB data) {
            try {
                String [] d = new String[6];

                d[0] = null;
                d[1] = null;
                d[2] = null;
                d[3] = null;
                d[4] = null;
                d[5] = "Rick";
                int[] matches = data.find(d);
                if (matches != null) {
                    for (int i = 0; i < matches.length; i++) {
                        readTest(data, matches[i]);
                    }
                }

            } catch (Exception e) {
                System.out.println("Exception " + e.toString());
            }
        }

        public void run() {
            for (int i = 0; i < maxRecords; i++) {
                int recNo = random.nextInt(maxRecords);
                readTest(data, recNo);
            }

            for (int i = 0; i < maxRecords; i++) {
                addTest(data);
            }

            for (int i = 0; i < 1; i++) {
                int recNo = random.nextInt(maxRecords);
                deleteTest(data, recNo);
            }

            for (int i = 0; i < maxRecords; i++) {
                int recNo = random.nextInt(maxRecords);
                updateTest(data, recNo);
            }

            matchTest(data);
        }
    }

    public static void main(String args[]) {
        try {
            DB data = null;
            if (args.length > 0) {
                System.out.println("Locating RMI registry on remote host \"" + args[0] + "\"." );
                Registry remoteRegistry = LocateRegistry.getRegistry(args[0]);
                System.out.println( "RemoteMapClient looking up service \"" + DB.SERVICENAME + "\"." );
                data = (DB)remoteRegistry.lookup(DB.SERVICENAME);
            } else {
                data = new Data("db-2x2.db");
            }

            for (int i = 0; i < 10; i++) {
                Thread foo = new Thread(new TestDB.testDataThread(data));
                foo.start();
            }

        } catch (Exception e) {
            System.out.println("Exception " + e.toString());
        }
    }

}
