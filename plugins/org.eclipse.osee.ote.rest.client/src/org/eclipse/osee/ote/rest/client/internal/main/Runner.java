package org.eclipse.osee.ote.rest.client.internal.main;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.ote.rest.client.GetFileProgress;
import org.eclipse.osee.ote.rest.client.internal.OteClientImpl;

public class Runner {

   public static void main(String[] args) throws IOException, OseeCoreException, InterruptedException, ExecutionException {

      GetFileProgress callback = new GetFileProgress() {

         private int total;

         @Override
         public void fail(String string) {
            System.out.println("fail: " + string);

         }

         @Override
         public void fail(Throwable th) {
            System.out.println(th.getMessage());
            th.printStackTrace();
         }

         @Override
         public void setUnitsOfWork(int totalUnitsOfWork) {
            total = totalUnitsOfWork;
         }

         @Override
         public void setUnitsWorked(int unitsWorked) {
            System.out.println(unitsWorked + " of " + total);
         }

         @Override
         public void success() {
            System.out.println("success");
         }
      };

      OteClientImpl imp = new OteClientImpl();
      // imp.setUriProvider(new URIProviderImpl());
//      List<File> folders = new ArrayList<File>();
//      folders.add(new File("C:\\UserData\\CAx\\gitrepos\\ote\\lba\\usg\\no_rmi\\ote.lba.usg\\plugins\\ote.lba.usg.v4.p2\\target\\precompiled\\commonBundles"));
//      folders.add(new File("C:\\UserData\\CAx\\gitrepos\\ote\\lba\\usg\\no_rmi\\ote.lba.usg\\plugins\\ote.lba.usg.v4.p2\\target\\precompiled\\externalServerDependencies"));
//      folders.add(new File("C:\\UserData\\CAx\\gitrepos\\ote\\lba\\usg\\no_rmi\\ote.lba.usg\\plugins\\ote.lba.usg.v4.p2\\target\\precompiled\\runtimeLibs"));

      URI uri = UriBuilder.fromPath("http://localhost:8089").build();

      Future<GetFileProgress> job = imp.getFile(uri, new File("test1"), "C:\\UserData\\AH-6\\AH6_X86_BUILDS\\fcc_main_x86_ote.exe1", callback);
      Future<GetFileProgress> job1 = imp.getFile(uri, new File("test2"), "C:\\UserData\\AH-6\\AH6_X86_BUILDS\\fcc_main_x86_ote.exe", callback);
      Future<GetFileProgress> job2 = imp.getFile(uri, new File("test3"), "C:\\UserData\\AH-6\\AH6_X86_BUILDS\\fcc_main_x86_ote.exe", callback);
      Future<GetFileProgress> job3 = imp.getFile(uri, new File("test4"), "C:\\UserData\\AH-6\\AH6_X86_BUILDS\\fcc_main_x86_ote.exe", callback);
     job.get();
     job1.get();
     job2.get();
     job3.get();
   }

}
