package org.eclipse.osee.ote.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.osee.ote.OTEServerFolder;

public class OTEFolderImpl implements OTEServerFolder{
   private static final String DELETE_MARKER = ".delete";
   private static final String LOG_FILE = ".log";
   private static final String STATUS_FILE = ".status";
   private static final String RUNLIST_FILE = ".runlist";
   private static final String RESULTS_FILE = ".result";
   
   private static File OTESERVER = new File(System.getProperty("user.home") + File.separator + "OTESERVER");
   private static File BATCHES = new File(OTESERVER, "batches");
   private static File JARCACHE = new File(OTESERVER, "runtimeCache");
   private static SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd___kk_mm_ss");
   
   /**
    * ds component method
    */
   public void start(){
      cleanOldBatchFolders();
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
      File[] files = BATCHES.listFiles();
      for(File file:files){
         if(file.isDirectory()){
            File delete = new File(file, DELETE_MARKER);
            boolean deleteFolder = delete.exists();
            if(deleteFolder){
               File[] toDelete = file.listFiles();
               for(File f:toDelete){
                  f.delete();
               }
               file.delete();
            }
         }
      }
   }
   
   @Override
   public File getServerFolder(){
      return OTESERVER;
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
   public File getNewBatchFolder() {
      return new File(getBatchesFolder(), format.format(new Date()));
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
         e.printStackTrace();
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
}
