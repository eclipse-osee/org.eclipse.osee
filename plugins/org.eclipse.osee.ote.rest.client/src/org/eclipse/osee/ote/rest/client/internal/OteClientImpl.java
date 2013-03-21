/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.rest.client.internal;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.ote.rest.client.ConfigurationProgress;
import org.eclipse.osee.ote.rest.client.ConfigurationStatusCallback;
import org.eclipse.osee.ote.rest.client.GetFileProgress;
import org.eclipse.osee.ote.rest.client.OteClient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteClientImpl implements OteClient, WebResourceFactory {

//   private URIProvider uriProvider;
//   private WebClientProvider clientProvider;
//
//   public void setWebClientProvider(WebClientProvider clientProvider) {
//      this.clientProvider = clientProvider;
//   }
//
//   public void setUriProvider(URIProvider uriProvider) {
//      this.uriProvider = uriProvider;
//   }
   
   private ExecutorService executor;
   
   public OteClientImpl(){
      executor = Executors.newCachedThreadPool(new ThreadFactory(){
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setName("OTE Client " + th.getId());
            th.setDaemon(true);
            return th;
         }
      });
   }

   public void start() {

   }

   public void stop() {
   }

   public Job configureServerEnvironment(URI uri, List<File> jars, final ConfigurationStatusCallback callback) {
      IOperation operation = new ConfigureOperation(this, uri, jars);
      Job job = Operations.executeAsJob(operation, true, Job.LONG, new JobChangeAdapter() {
         @Override
         public void aboutToRun(IJobChangeEvent event) {
            super.aboutToRun(event);
         }

         @Override
         public void running(IJobChangeEvent event) {
            super.running(event);
         }

         @Override
         public void done(IJobChangeEvent event) {
            super.done(event);
            IStatus status = event.getResult();
            if (status.isOK()) {
               callback.success();
            } else {
               if(status.getException() != null){
                  callback.fail(status.getException());
               }
               if(status.getMessage() != null) {
                  callback.fail(status.getMessage());
               }
            }
         }

      });
      return job;
   }

//   @Override
//   public void configureServer(URI uri, List<File> jars, ConfigurationStatusCallback callback) {
//      OteConfiguration configuration = new OteConfiguration();
//      OteConfigurationIdentity identity = new OteConfigurationIdentity();
//      identity.setName("test");
//      configuration.setIdentity(identity);
//      try {
//         HeadlessClassServer classServer = new
//               HeadlessClassServer(PortUtil.getInstance().getValidPort(),
//                     InetAddress.getLocalHost(), jars);
//         try {
//            for (BundleInfo bundleInfo : classServer.getBundles()) {
//               OteConfigurationItem item = new OteConfigurationItem();
//               item.setBundleName(bundleInfo.getSymbolicName());
//               item.setBundleVersion(bundleInfo.getVersion());
//               item.setLocationUrl(bundleInfo.getSystemLocation().toString());
//               item.setMd5Digest(bundleInfo.getMd5Digest());
//               configuration.addItem(item);
//            }
//
//            WebResource baseService = getWebResource("");
//            OteJobStatus response =
//                  baseService.path("ote").path("config").accept(MediaType.APPLICATION_XML).post(OteJobStatus.class,
//                        configuration);
//            if (response != null) {
//
//               WebResource service =
//                     getWebResource(UriBuilder.fromUri(response.getUpdatedJobStatus().toURI()).build());
//
//               OteJobStatus innerResponse = response;
//               callback.setUnitsOfWork(response.getTotalUnitsOfWork());
//               while (innerResponse != null && !innerResponse.isJobComplete()) {
//                  Thread.sleep(100);
//                  innerResponse =
//                        service.accept(MediaType.APPLICATION_XML).get(OteJobStatus.class);
//                  if (innerResponse != null) {
//                     callback.setUnitsOfWork(innerResponse.getTotalUnitsOfWork());
//                     callback.setUnitsWorked(innerResponse.getUnitsWorked());
//                  }
//               }
//               if (innerResponse != null) {
//                  callback.setUnitsWorked(innerResponse.getUnitsWorked());
//                  if (innerResponse.isSuccess()) {
//                     callback.success();
//                  } else {
//                     callback.fail(innerResponse.getLog());
//                  }
//               } else {
//                  callback.fail("Unable to determine job status.");
//               }
//            } else {
//               callback.fail("Unable to determine job status.");
//            }
//         } finally {
//            classServer.stop();
//         }
//      } catch (Throwable th) {
//         callback.fail(th);
//      }
//   }

   // private WebResource getWebResource() {
   // ClientConfig config = new DefaultClientConfig();
   // Client client = Client.create(config);
   // WebResource service =
   // client.resource(UriBuilder.fromUri("http://localhost:8089").build());
   // // uriProvider.getApplicationServerURI();
   // return service;
   // }
   //
   // private WebResource getWebResource(URI uri) {
   // ClientConfig config = new DefaultClientConfig();
   // Client client = Client.create(config);
   // WebResource service = client.resource(uri);
   // return service;
   // }

   @Override
   public WebResource createResource(URI uri) throws Exception {
      ClientConfig config = new DefaultClientConfig();
      Client client = Client.create(config);
      WebResource service = client.resource(uri);
      return service;
   }

   @Override
   public Future<GetFileProgress> getFile(URI uri, File destination, String filePath, final GetFileProgress progress){
      return executor.submit(new GetOteServerFile(uri, destination, filePath, progress, this));
   }
   
   @Override
   public Future<ConfigurationProgress> configureServerEnvironment(URI uri, List<File> jars, final ConfigurationProgress progress){
      return executor.submit(new ConfigureOteServerFile(uri, jars, progress, this));
   }

//   @Override
//   public void configureServer(URI server, List<File> jarsToLoad, ConfigurationStatusCallback callback) {
//      // TODO Auto-generated method stub
//      
//   }

}
