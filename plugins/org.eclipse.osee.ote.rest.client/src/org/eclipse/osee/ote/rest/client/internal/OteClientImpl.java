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

import org.eclipse.osee.ote.rest.client.OTECacheItem;
import org.eclipse.osee.ote.rest.client.OteClient;
import org.eclipse.osee.ote.rest.client.Progress;
import org.eclipse.osee.ote.rest.client.ProgressWithCancel;
import org.eclipse.osee.ote.rest.model.OTEConfiguration;
import org.eclipse.osee.ote.rest.model.OTETestRun;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteClientImpl implements OteClient, WebResourceFactory {

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

   @Override
   public WebResource createResource(URI uri) throws Exception {
      ClientConfig config = new DefaultClientConfig();
      Client client = Client.create(config);
      WebResource service = client.resource(uri);
      return service;
   }

   @Override
   public Future<Progress> getFile(URI uri, File destination, String filePath, final Progress progress){
      return executor.submit(new GetOteServerFile(uri, destination, filePath, progress, this));
   }
   
   @Override
   public Future<Progress> configureServerEnvironment(URI uri, List<File> jars, final Progress progress){
      return executor.submit(new ConfigureOteServer(uri, jars, progress, this));
   }

   @Override
   public Future<Progress> updateServerJarCache(URI uri, String baseJarURL, List<OTECacheItem> jars, Progress progress) {
      return executor.submit(new PrepareOteServerFile(uri, baseJarURL, jars, progress, this));
   }

   @Override
   public Future<ProgressWithCancel> runTest(URI uri, OTETestRun tests, Progress progress) {
      return executor.submit(new RunTests(uri, tests, progress, this));
   }

   @Override
   public Future<Progress> configureServerEnvironment(URI uri, OTEConfiguration configuration, Progress progress) {
      return executor.submit(new ConfigureOteServer(uri, configuration, progress, this));
   }

}
