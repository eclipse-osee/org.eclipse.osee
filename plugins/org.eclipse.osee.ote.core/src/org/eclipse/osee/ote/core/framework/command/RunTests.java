package org.eclipse.osee.ote.core.framework.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.Configuration;
import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.OTEServerFolder;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.status.CommandEndedStatusEnum;
import org.eclipse.osee.ote.core.environment.status.OTEStatusBoard;
import org.eclipse.osee.ote.core.framework.IMethodResult;
import org.eclipse.osee.ote.core.framework.ReturnCode;
import org.eclipse.osee.ote.core.framework.saxparse.ProcessOutfileOverview;
import org.eclipse.osee.ote.message.IMessageTestContext;

public class RunTests implements ITestServerCommand, Serializable {

   private static final long serialVersionUID = 7408758537342855854L;
   private static final String DEVELOPMENT = "Development";
   
   private final IPropertyStore global;
   private final List<IPropertyStore> scripts;
   private volatile boolean cancelAll = false;
   private volatile boolean isRunning = false;
   private final UUID sessionKey;
   private final String guid;
   private TestEnvironment environment;
   private final Configuration configuration;

   public RunTests(String guid, UUID uuid, Configuration configuration, IPropertyStore global, List<IPropertyStore> scripts) {
      this.global = global;
      this.scripts = scripts;
      this.sessionKey = uuid;
      this.guid = guid;
      this.configuration = configuration;
   }

   public UUID getSessionKey() {
      return sessionKey;
   }

   @Override
   public ICommandHandle createCommandHandle(Future<ITestCommandResult> result, ITestContext context) throws ExportException {
      return new RunTestsHandle(result, context, this);
   }

   @Override
   public ITestCommandResult execute(TestEnvironment environment, OTEStatusBoard statusBoard) throws Exception {
      ITestCommandResult retVal = TestCommandResult.SUCCESS;
      isRunning = true;
      IMessageTestContext msgContext = (IMessageTestContext) environment;
      this.environment = environment;
      
      OTEApi ote = ServiceUtility.getService(OTEApi.class);
      OTEServerFolder serverFolder = ote.getServerFolder();
      
      String testType = getTestType();
      File batchFolder = serverFolder.getNewBatchFolder(testType);
      batchFolder.mkdirs();
      if(!isFolderToKeep(testType)){
         serverFolder.markFolderForDelete(batchFolder);
      }
      BatchLog batchLog = new BatchLog(serverFolder.getBatchLogFile(batchFolder));
      batchLog.open();
      
      File batchStatusFile = serverFolder.getBatchStatusFile(batchFolder);
      File batchRunList = serverFolder.getBatchRunList(batchFolder);
      
      setBatchStatus(batchStatusFile, "running");
      setBatchRunList(batchRunList, scripts);
      
      environment.setupOutfileDir(batchFolder.getAbsolutePath());
      msgContext.resetScriptLoader(configuration, global.getArray(RunTestsKeys.classpath.name()));
      
      //Override the command line option only if batch abort has been selected in TestManager
      boolean batchAbortFailMode = global.getBoolean(RunTestsKeys.batchFailAbortMode.name());
      if(batchAbortFailMode){
        System.setProperty("ote.abort.on.fail", "true");
      }  
      else {
         System.clearProperty("ote.abort.on.fail"); 
      }
      
      //Override the command line option only if batch pause has been selected in TestManager
      boolean batchPauseFailMode = global.getBoolean(RunTestsKeys.batchFailPauseMode.name());
      if(batchPauseFailMode){
         System.setProperty("ote.pause.on.fail", "true");
      }   
      else {
         System.clearProperty("ote.pause.on.fail");   
      }

      //Override the command line option only if batch pause has been selected in TestManager
      boolean printOnFailMode = global.getBoolean(RunTestsKeys.printFailToConsoleMode.name());
      if(printOnFailMode){
         System.setProperty("ote.print.fail.to.console", "true");
      }   
      else {
         System.clearProperty("ote.print.fail.to.console");   
      }
      
      for (IPropertyStore store : scripts) {
         if (cancelAll) {
            statusBoard.onTestComplete(store.get(RunTestsKeys.testClass.name()),
               store.get(RunTestsKeys.serverOutfilePath.name()),
               store.get(RunTestsKeys.clientOutfilePath.name()), CommandEndedStatusEnum.ABORTED,
               new ArrayList<IHealthStatus>());
            retVal = TestCommandResult.CANCEL;
            continue;

         }
         statusBoard.onTestStart(store.get(RunTestsKeys.testClass.name()), store.get(RunTestsKeys.serverOutfilePath.name()), store.get(RunTestsKeys.clientOutfilePath.name()));
         IMethodResult runResults = environment.getRunManager().run(environment, store);

         CommandEndedStatusEnum status = CommandEndedStatusEnum.RAN_TO_COMPLETION;
         if (runResults.getReturnCode() == ReturnCode.ABORTED) {
            status = CommandEndedStatusEnum.ABORTED;
         }
         if (runResults.getReturnCode() == ReturnCode.ERROR) {
            status = CommandEndedStatusEnum.EXCEPTION;
         }

         statusBoard.onTestComplete(store.get(RunTestsKeys.testClass.name()),
            store.get(RunTestsKeys.serverOutfilePath.name()), store.get(RunTestsKeys.clientOutfilePath.name()),
            status, runResults.getStatus());
         batchLog.flush();
         
         File outfile = new File(store.get(RunTestsKeys.serverOutfilePath.name()));
         generateOutfileSummary(outfile, serverFolder.getResultsFile(outfile));
      }
      setBatchStatus(batchStatusFile, "complete");
      isRunning = false;
      batchLog.close();
      return retVal;
   }
   
   private void setBatchRunList(File batchRunList, List<IPropertyStore> scripts2) {
      StringBuilder sb = new StringBuilder();
      for (IPropertyStore store : scripts) {
         sb.append(store.get(RunTestsKeys.testClass.name()));
         sb.append("\n");
      }
      try {
         Lib.writeStringToFile(sb.toString(), batchRunList);
      } catch (IOException ex) {
         OseeLog.log(getClass(), Level.SEVERE, "Failed to write batch run list", ex);
      }
   }

   private void setBatchStatus(File batchStatusFile, String string) {
      try {
         Lib.writeStringToFile(string, batchStatusFile);
      } catch (IOException ex) {
         OseeLog.log(getClass(), Level.SEVERE, "Failed to write batch status", ex);
      }
   }

   private void generateOutfileSummary(File outfile, File resultsFile){
      String summary = getResults(outfile);
      writeSummaryToFile(resultsFile, summary);
   }
   
   private void writeSummaryToFile(File file, String summary) {
      try {
         Lib.writeStringToFile(summary, file);
      } catch (IOException ex) {
         OseeLog.log(getClass(), Level.SEVERE, "Failed to write outfile summary", ex);
      }
   }

   private String getResults(File outfile){
      ProcessOutfileOverview overview = new ProcessOutfileOverview();
      FileInputStream fis = null;
      try{
         fis = new FileInputStream(outfile);
         overview.run(fis);
      } catch (Exception ex) {
         OseeLog.log(getClass(), Level.SEVERE, "Failed to write outfile summary", ex);
      } finally {
         if(fis != null){
            try {
               fis.close();
            } catch (IOException e) {
               OseeLog.log(getClass(), Level.SEVERE, "Failed to close outfile", e);
            }
         }
      }
      return String.format("%s,%s,%s", overview.getScriptName(), overview.getResults(), overview.getElapsedTime());
   }

   private String getTestType(){
      IPropertyStore props = scripts.get(0);
      return props.get("FormalTestType");
   }
   
   private boolean isFolderToKeep(String testType) {
      if(testType != null && testType.equalsIgnoreCase(DEVELOPMENT)){
         return false;
      } else {
         return true;
      }
   }

   public boolean cancel() {
      cancelAll = true;
      return environment.getRunManager().abort();
   }
   
   public boolean cancelSingle() {
      return environment.getRunManager().abort();
   }

   @Override
   public String getGUID() {
      return guid;
   }

   @Override
   public UUID getUserSessionKey() {
      return sessionKey;
   }

   boolean isRunning() {
      return isRunning;
   }
   
}
