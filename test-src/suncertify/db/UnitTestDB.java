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
public class UnitTestDB {

    private boolean doLock;
    private boolean doUnLock;

    public UnitTestDB(boolean doLock, boolean doUnLock) {
        this.doLock = doLock;
        this.doUnLock = doUnLock;
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
            if (doLock) {
                cookie = data.lock(recNo);
            }
            dump(data.read(recNo));

        } catch (RecordNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (cookie != -1) {
                try {
                    if (doUnLock) {
                        data.unlock(recNo, cookie);
                    }
                } catch (RecordNotFoundException e) {
                    System.out.println("Exception " + e.toString());
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

            if (doLock) {
                cookie = data.lock(recNo);
            }
            data.update(recNo, d, cookie);

        } catch (RecordNotFoundException e) {
            System.out.println("Exception " + e.toString());
        } catch (SecurityException e) {
            System.out.println("Exception " + e.toString());
        } catch (IOException e) {
            System.out.println("Exception " + e.toString());
        } finally {
            if (cookie != -1) {
                try {
                    if (doUnLock) {
                        data.unlock(recNo, cookie);
                    }
                } catch (RecordNotFoundException e) {
                    System.out.println("Exception " + e.toString());
                } catch (SecurityException e) {
                    System.out.println("Exception " + e.toString());
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
            if (doLock) {
                cookie = data.lock(recNo);
            }
            data.delete(recNo, cookie);

        } catch (RecordNotFoundException e) {
            System.out.println("Exception " + e.toString());
        } catch (SecurityException e) {
            System.out.println("Exception " + e.toString());
        } catch (IOException e) {
            System.out.println("Exception " + e.toString());
        } finally {
            if (cookie != -1) {
                try {
                    if (doUnLock) {
                        data.unlock(recNo, cookie);
                    }
                } catch (RecordNotFoundException e) {
                    System.out.println("Exception " + e.toString());
                } catch (SecurityException e) {
                    System.out.println("Exception " + e.toString());
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

            UnitTestDB test1 = new UnitTestDB(true, true);
            test1.addTest(data);
            test1.readTest(data, 28);
            test1.matchTest(data);
            test1.addTest(data);
            test1.readTest(data, 28);
            test1.matchTest(data);
            test1.updateTest(data, 28);
            test1.readTest(data, 28);
            test1.matchTest(data);
            test1.deleteTest(data, 28);
            test1.readTest(data, 28);
            test1.matchTest(data);

            UnitTestDB test2 = new UnitTestDB(false, false);
            test2.updateTest(data, 28);
            test2.deleteTest(data, 28);

            UnitTestDB test3 = new UnitTestDB(false, true);
            test3.updateTest(data, 28);
            test3.deleteTest(data, 28);

        } catch (Exception e) {
            System.out.println("Exception " + e.toString());
        }
    }

}
