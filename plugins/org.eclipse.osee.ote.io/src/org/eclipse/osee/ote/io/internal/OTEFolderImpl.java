package org.eclipse.osee.ote.io.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.io.OTEServerFolder;
import org.eclipse.osee.ote.properties.OtePropertiesCore;

public class OTEFolderImpl implements OTEServerFolder{
   
   private static int DAYS;
   
   static {
      String strDays = OtePropertiesCore.batchFolderDays.getValue();
      if(strDays!=null){
         try{
         DAYS = Integer.parseInt(strDays);
         } catch (NumberFormatException ex){
            DAYS = 7;
         }
      } else {
         DAYS = 7;
      }
   }
   
   private static final String DELETE_MARKER = ".delete";
   private static final String LOG_FILE = ".log";
   private static final String STATUS_FILE = ".status";
   private static final String RUNLIST_FILE = ".runlist";
   private static final String RESULTS_FILE = ".result";
   private static final String RUNNING_MARKER = ".running";
   
   private static File OTESERVER = determineOteServerFolder();
   private static File BATCHES = new File(OTESERVER, "batches");
   private static File JARCACHE = new File(OTESERVER, "runtimeCache");
   private static File SERVERS = new File(OTESERVER, "servers");
   private static SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd___kk_mm_ss");
   
   private File currentServerFolder; 
   
   private static File determineOteServerFolder() {
      File oteStationParent = new File(OtePropertiesCore.oteServerFolder.getValue("undefined"));
      if (!oteStationParent.exists() || !oteStationParent.isDirectory()) {
         OseeLog.log(OTEFolderImpl.class, Level.WARNING, "ote server folder parent does not exist folder -" + oteStationParent.getAbsolutePath());
         oteStationParent = new File(OtePropertiesCore.userHome.getValue(), "stations");
         return new File(oteStationParent, OtePropertiesCore.oteStationName.getValue("unknown-station"));
      }
      File stationsFolder = new File(oteStationParent, "stations");
      File stationFolder = new File(stationsFolder, OtePropertiesCore.oteStationName.getValue("unknown-station"));
      return new File(stationFolder, OtePropertiesCore.userName.getValue());
   }
   
   public OTEFolderImpl(){
      currentServerFolder = getNewServerFolder();
      String serverIOPath = OtePropertiesCore.ioRedirectPath.getValue(currentServerFolder.getAbsolutePath());
      currentServerFolder = new File(serverIOPath);
   }
   
   /**
    * ds component method
    */
   public void start(){
      cleanOldBatchFolders();
      cleanOldServerFolders();
   }
   
   /**
    * ds component method
    */
   public void stop(){
      
   }
   
   /**
    * This will not clean sub-folders
    */
   @Override
   public void cleanOldBatchFolders(){
      Date dateBefore = new Date(new Date().getTime() -(DAYS * 24 * 3600 * 1000));
      if(BATCHES.exists()){
         File[] files = BATCHES.listFiles();
         if(files != null){
            for(File file:files){
               if(file.isDirectory()){
                  boolean olderFileOrUnknown = true;
                  try {
                     Date fileDate = format.parse(file.getName());
                     if(fileDate == null){
                        OseeLog.log(getClass(), Level.WARNING, "Failed to parse a date from the Dir name "+ file.getAbsolutePath());
                     } else {
                        olderFileOrUnknown = fileDate.before(dateBefore);
                     }
                  } catch (ParseException e) {
                     int index = file.getName().lastIndexOf("__");
                     if(index > 1){
                        try{
                           Date fileDate = format.parse(file.getName().substring(0, index-1));
                           if(fileDate == null){
                              OseeLog.log(getClass(), Level.WARNING, "Failed to parse a date from the Dir name "+ file.getAbsolutePath());
                           } else {
                              olderFileOrUnknown = fileDate.before(dateBefore);
                           }
                        } catch (ParseException e2){
                           OseeLog.log(getClass(), Level.SEVERE, e2);
                        }
                     } else {
                        OseeLog.log(getClass(), Level.SEVERE, e);
                     }
                  }
                  File delete = new File(file, DELETE_MARKER);
                  boolean deleteFolder = delete.exists();
                  if(deleteFolder && olderFileOrUnknown){
                     File[] toDelete = file.listFiles();
                     for(File f:toDelete){
                        f.delete();
                     }
                     file.delete();
                  }
               }
            }
         }
      }
   }
   
   public void cleanOldServerFolders(){
      Date dateBefore = new Date(new Date().getTime() -(DAYS * 24 * 3600 * 1000));
      Date dateNoMatterwhat = new Date(new Date().getTime() -(28L * 24L * 3600L * 1000L));//4 weeks
      if(SERVERS.exists()){
         File[] files = SERVERS.listFiles();
         if(files != null){
            for(File file:files){
               if(file.isDirectory()){
                  boolean olderFileOrUnknown = true;
                  boolean removeNoMatterWhat = false;
                  try {
                     Date fileDate = format.parse(file.getName());
                     if(fileDate == null){
                        OseeLog.log(getClass(), Level.WARNING, "Failed to parse a date from the Dir name "+ file.getAbsolutePath());
                     } else {
                        olderFileOrUnknown = fileDate.before(dateBefore);
                        removeNoMatterWhat = fileDate.before(dateNoMatterwhat);
                     }
                  } catch (ParseException e) {
                     int index = file.getName().lastIndexOf("__");
                     if(index > 1){
                        try{
                           Date fileDate = format.parse(file.getName().substring(0, index-1));
                           if(fileDate == null){
                              OseeLog.log(getClass(), Level.WARNING, "Failed to parse a date from the Dir name "+ file.getAbsolutePath());
                           } else {
                              olderFileOrUnknown = fileDate.before(dateBefore);
                           }
                        } catch (ParseException e2){
                           OseeLog.log(getClass(), Level.SEVERE, e2);
                        }
                     } else {
                        OseeLog.log(getClass(), Level.SEVERE, e);
                     }
                  }
                  File running = new File(file, RUNNING_MARKER);
                  boolean deleteFolder = !running.exists();
                  if((deleteFolder && olderFileOrUnknown) || removeNoMatterWhat){
                     File[] toDelete = file.listFiles();
                     for(File f:toDelete){
                        f.delete();
                     }
                     file.delete();
                  }
               }
            }
         }
      }
   }
   
   
   
   @Override
   public File getRootFolder(){
      return OTESERVER;
   }
   
   @Override
   public File getServerFolder(){
      return SERVERS;
   }
   
   @Override
   public File getBatchesFolder(){
      return BATCHES;
   }
   
   @Override
   public File getCacheFolder(){
      return JARCACHE;
   }
   
   @Override
   public File getNewServerFolder() {
      return new File(getServerFolder(), format.format(new Date()));
   }
   
   @Override
   public File getCurrentServerFolder() {
      return currentServerFolder;
   }

   @Override
   public File getNewBatchFolder() {
      return new File(getBatchesFolder(), format.format(new Date()));
   }
   
   @Override
   public File getNewBatchFolder(String suffix) {
      if(suffix != null && suffix.length() > 0){
         return new File(getBatchesFolder(), format.format(new Date())+"__" + suffix);
      } else {
         return getNewBatchFolder();
      }
   }
   
   @Override
   public File getResultsFile(File outfile){
      return new File(outfile.getAbsolutePath() + RESULTS_FILE);
   }
   
   @Override
   public void markFolderForDelete(File folder) {
      FileOutputStream fos = null;
      try {
         folder.mkdirs();
         fos = new FileOutputStream(new File(folder, DELETE_MARKER));
      } catch (FileNotFoundException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } finally {
         if(fos != null){
            try {
               fos.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }
   
   @Override
   public void unmarkFolderForDelete(File folder) {
      File delete = new File(folder, DELETE_MARKER);
      if(delete.exists()){
         delete.delete();
      }
   }

   @Override
   public File getBatchLogFile(File batchFolder) {
      return new File(batchFolder, LOG_FILE);
   }

   @Override
   public File getBatchStatusFile(File batchFolder) {
      return new File(batchFolder, STATUS_FILE);
   }

   @Override
   public File getBatchRunList(File batchFolder) {
      return new File(batchFolder, RUNLIST_FILE);
   }

   @Override
   public List<File> getRunningServerFolders() {
      List<File> files = new ArrayList<File>();
      for(File serverDir:SERVERS.listFiles()){
         File running = new File(serverDir, RUNNING_MARKER);
         if(running.exists()){
            files.add(serverDir);
         }
      }
      return files;
   }

   @Override
   public void removeServerRunningFile(File serverFolder) {
      File file = new File(serverFolder, RUNNING_MARKER);
      if(file.exists()){
         file.delete();
      }
   }

}
