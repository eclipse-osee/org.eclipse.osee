package org.eclipse.osee.ote;

import java.io.File;

/**
 * This class is used to help manage the OTE Server Folder area.  This includes the runtime cache and 
 * the batches area.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public interface OTEServerFolder {
   
   /**
    * This class will clean out all batch folders that have been marked for delete.  It 
    * will not clean out sub-folders so if those exist the folder will not be completely
    * removed.
    */
   public void cleanOldBatchFolders();
   
   public File getServerFolder();
   
   /**
    * This is the root folder for all batch folders.
    * 
    * @return
    */
   public File getBatchesFolder();
   
   public File getCacheFolder();
   
   /**
    * This is the batch log file that contains all LEVEL.SEVERE and above log events that happened during 
    * a batch.
    * 
    * @param batchFolder
    * @return
    */
   public File getBatchLogFile(File batchFolder);

   /**
    * Generates a new File at the batches root folder with the current time as it's name.  This method does 
    * not call mkdirs.
    * 
    * @return
    */
   public File getNewBatchFolder();
   
   /**
    * Generates a new File at the batches root folder with the current time as it's name and the suffix appended to it.  This method does 
    * not call mkdirs.
    * 
    * @return
    */
   public File getNewBatchFolder(String suffix);
   
   public void markFolderForDelete(File folder);
   
   public void unmarkFolderForDelete(File folder);

   /**
    * Returns a file that is the results summary of the given outfile.  The format of the returned file
    * if it has content is: <script name>,<results string>,<elapsed time>.  There is no guarantee that the 
    * returned file exists or that it has any data, the user must check for existence and validity.
    * 
    * @param outfile
    * @return
    */
   public File getResultsFile(File outfile);

   /**
    * Returns a file that specifies the status of a given batch.  Possible values in the file are 'in queue',
    * 'running', and 'complete'.  There is no guarantee that the file exists or that it has any content.
    * 
    * @param batchFolder
    * @return
    */
   public File getBatchStatusFile(File batchFolder);

   /**
    * Returns a file tht specifies the runlist of a batch.  It will be of the format scripts<newline>scripts<newline>...
    * There is no guarantee that the file exists or that it has any content.
    * 
    * @param batchFolder
    * @return
    */
   public File getBatchRunList(File batchFolder);

}
