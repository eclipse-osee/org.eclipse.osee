/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.rest.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.Configuration;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.core.framework.command.RunTests;
import org.eclipse.osee.ote.rest.model.KeyValue;
import org.eclipse.osee.ote.rest.model.OTEJobStatus;
import org.eclipse.osee.ote.rest.model.OTETestRun;
import org.eclipse.osee.ote.rest.model.Properties;

/**
 * @author Andrew M. Finkbeiner
 */
@Path("run")
public class OteRunTestResource {

   // Allows to insert contextual objects into the class, 
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   @GET
   @Produces({MediaType.TEXT_HTML})
   public String getCurrentRunStatus() throws MalformedURLException, IllegalArgumentException, UriBuilderException, InterruptedException, ExecutionException {
      return "hello";//getDataStore().getConfiguration(uriInfo);
   }
   
   @POST
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public OTEJobStatus run(OTETestRun tests) throws IOException, InterruptedException, ExecutionException {
      TestEnvironmentInterface env = ServiceUtility.getService(TestEnvironmentInterface.class);
      OTEJobStatus status = new OTEJobStatus();
      if(env == null){
         status.setSuccess(false);
         status.setJobComplete(true);
         status.setErrorLog("Failed to aquire TestEnvironmentInterface.");
      } else {
         UUID id = UUID.randomUUID();
         Configuration configuration = TranslateUtil.translateToOtherConfig(tests.getJarConfiguration());
         RunTests envTestRun = new RunTests(id.toString(), id, configuration, propertyStoreConversion(tests.getGlobalProperties()), propertyStoreConversion(tests.getTests()));
         OteRunTestCommands commands = ServiceUtility.getService(OteRunTestCommands.class);
         if(commands != null){
            commands.putCommand(id.toString(), envTestRun);
         }
         env.addCommand(envTestRun);
         status.setJobId(id.toString());
      }
      return status;
   }
   
   @Path("{uuid}")
   public OteRunTestResourceItem getConfiguration(@PathParam("uuid") String id) {
      return new OteRunTestResourceItem(uriInfo, request, id);
   }

   public IPropertyStore propertyStoreConversion(Properties properties){
      IPropertyStore store = new PropertyStore();
      for(KeyValue pair:properties.getPairs()){
         if(pair.getValue() != null){
            store.put(pair.getKey(), pair.getValue());
         } else if(pair.getValues() != null){
            store.put(pair.getKey(), pair.getValues().toArray(new String[0]));
         } else {
            OseeLog.log(OteRunTestResource.class, Level.SEVERE, "For PROPERTY:" + pair.getKey() + ", the corresponding VALUE is NULL!!");
         }
      }
      return store;
   }
   
   public List<IPropertyStore> propertyStoreConversion(List<Properties> properties){
      List<IPropertyStore> stores = new ArrayList<>();
      for(Properties prop:properties){
         stores.add(propertyStoreConversion(prop));
      }
      return stores;
   }
   
}
