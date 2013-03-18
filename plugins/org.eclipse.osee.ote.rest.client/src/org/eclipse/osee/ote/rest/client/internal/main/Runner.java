package org.eclipse.osee.ote.rest.client.internal.main;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.ote.rest.client.ConfigurationStatusCallback;
import org.eclipse.osee.ote.rest.client.internal.OteClientImpl;

public class Runner {

   public static void main(String[] args) throws IOException, OseeCoreException {

      ConfigurationStatusCallback callback = new ConfigurationStatusCallback() {

         private int total;

         @Override
         public void fail(String string) {
            System.out.println("fail: " + string);

         }

         @Override
         public void fail(Throwable th) {
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
      List<File> folders = new ArrayList<File>();
      folders.add(new File("C:\\UserData\\CAx\\gitrepos\\ote\\lba\\usg\\no_rmi\\ote.lba.usg\\plugins\\ote.lba.usg.v4.p2\\target\\precompiled\\commonBundles"));
      folders.add(new File("C:\\UserData\\CAx\\gitrepos\\ote\\lba\\usg\\no_rmi\\ote.lba.usg\\plugins\\ote.lba.usg.v4.p2\\target\\precompiled\\externalServerDependencies"));
      folders.add(new File("C:\\UserData\\CAx\\gitrepos\\ote\\lba\\usg\\no_rmi\\ote.lba.usg\\plugins\\ote.lba.usg.v4.p2\\target\\precompiled\\runtimeLibs"));

      URI uri = UriBuilder.fromPath("http://localhost:8089").build();

      Job job = imp.configureServerEnvironment(uri, folders, callback);
      try {
         job.join();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      Operations.checkForErrorStatus(job.getResult());
   }

}
