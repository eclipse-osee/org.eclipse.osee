package org.eclipse.osee.framework.resource.common.osgi;

class OseeServerThread extends Thread {

   protected OseeServerThread(String name) {
      super(name);

   }

   protected OseeServerThread(Runnable arg0, String name) {
      super(arg0, name);
   }
}
