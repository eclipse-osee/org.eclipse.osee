package org.eclipse.osee.framework.db.connection.internal;


class ConnectionReaper extends Thread {

   private OseeConnectionPool pool;
   private final long delay = 300000;

   ConnectionReaper(OseeConnectionPool pool) {
      this.pool = pool;
   }

   public void run() {
      while (true) {
         try {
            sleep(delay);
         } catch (InterruptedException e) {
         }
         pool.reapConnections();
      }
   }
}