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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.core.ServiceUtility;

/**
 * @author Andrew M. Finkbeiner
 */
@Path("batches")
public class OteBatchResource {

   // Allows to insert contextual objects into the class, 
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   UriInfo uriInfo;
   @Context
   Request request;
   private final String path;

   public OteBatchResource(UriInfo uriInfo, Request request, String path) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.path = path;
   }

   public OteBatchResource() {
      this.path = "";
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getBatches() throws OseeCoreException {
      OTEApi ote = ServiceUtility.getService(OTEApi.class);
      File rootBatches = ote.getServerFolder().getBatchesFolder();
      File myBatchFolder = new File(rootBatches, path);
      File logFile = ote.getServerFolder().getBatchLogFile(myBatchFolder);
      
      HTMLBuilder builder = new HTMLBuilder();
      
      builder.open("OTE Batch Status");
      /**
       * Disaled till we figure out security 
      if(uriInfo != null){
         String url = uriInfo.getAbsolutePath().toASCIIString();
         builder.addLink(url, "content.zip", "Get Folder Contents");
      }
      */
      builder.commonHeader(myBatchFolder);

      
      generateStatusSection(builder, ote, myBatchFolder);
      
      generateResultsSection(builder, ote, myBatchFolder);
      
      builder.h2("Output");
      builder.pre(getFileContents(logFile));
      builder.close();
	   return builder.get(); 
   }
   
   private void generateResultsSection(HTMLBuilder builder, OTEApi ote, File myBatchFolder) {
      File runList = ote.getServerFolder().getBatchRunList(myBatchFolder);
      String runListContents = "";
      if(runList.exists()){
         runListContents = getFileContents(runList);
      }
      if(runListContents.length() > 0){
         File[] resultFiles = myBatchFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File arg0) {
               return arg0.getName().endsWith(".result");
            }
         });
         String[] runListArray = runListContents.split("\n");
         List<TestResultSummary> results = getTestResults(resultFiles, runListArray);
         builder.h2("Tests");
         builder.tableStart();
         builder.trStart();
         builder.td("Test");
         builder.td("Result");
         builder.td("Time");
         builder.trEnd();
         
         for(TestResultSummary result:results){
            builder.trStart();
            builder.td(result.getName());
            builder.td(result.getResult());
            builder.td(result.getTime());
            builder.trEnd();
         }
         
         builder.tableEnd();
         
      } else {
         builder.h2("Files");
         builder.ulStart();
         File[] batches = myBatchFolder.listFiles();
         Arrays.sort(batches, new TimeSort());
         for(File file:batches){
            builder.li(file.getName());
         }
         builder.ulStop();
      }
   }
   
   

   private List<TestResultSummary> getTestResults(File[] resultFiles, String[] runListArray) {
      List<TestResultSummary> results = new ArrayList<TestResultSummary>();
      Map<String, TestResultSummary> resultsTemp = new HashMap<String, TestResultSummary>();
      for(File file:resultFiles){
         String content = getFileContents(file);
         if(content.length() > 0){
            String[] data = content.split(",");
            if(data.length == 3){
               resultsTemp.put(data[0], new TestResultSummary(data[0], data[1], data[2]));
            }
         }
      }
      for(String name:runListArray){
         TestResultSummary result = resultsTemp.get(name);
         if(result == null){
            result = new TestResultSummary(name);
         }
         results.add(result);
      }
      return results;
   }

   private void generateStatusSection(HTMLBuilder builder, OTEApi ote, File myBatchFolder){
      builder.h2("Status");
      String status = getFileContents(ote.getServerFolder().getBatchStatusFile(myBatchFolder));
      if(status.length() == 0){
         status = "unknown";
      }
      builder.p(status);
   }
   
   private String getFileContents(File statusFile) {
      String status = "";
      try {
         if(statusFile.exists()){
            status = Lib.fileToString(statusFile);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return status;
   }
   
   @Path("content.zip")
   @GET
   @Produces({"application/zip"})
   public StreamingOutput getZip() throws Exception {
      return new StreamingOutput() {
         @Override
         public void write(OutputStream output) throws IOException, WebApplicationException {
            try {
               OTEApi ote = ServiceUtility.getService(OTEApi.class);
               File rootBatches = ote.getServerFolder().getBatchesFolder();
               File myBatchFolder = new File(rootBatches, path);
               ZIPGenerator generator = new ZIPGenerator(myBatchFolder);
               generator.generateZip(output);
            } catch (Exception e) {
               throw new WebApplicationException(e);
            }
         }
      };
   }
   
}
