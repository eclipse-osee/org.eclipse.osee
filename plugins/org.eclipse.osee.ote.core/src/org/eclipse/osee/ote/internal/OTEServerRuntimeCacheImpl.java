package org.eclipse.osee.ote.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.io.streams.StreamPumper;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.OTEServerRuntimeCache;
import org.eclipse.osee.ote.OTEServerFolder;

public class OTEServerRuntimeCacheImpl implements OTEServerRuntimeCache  {
   
   private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd___kk_mm_ss"); 

   private File folder;
   private OTEServerFolder oteServerFolder;

   public OTEServerRuntimeCacheImpl(){
   }
   
   /**
    * ds component method
    */
   public void start(){
      folder = oteServerFolder.getCacheFolder();
      if (!folder.exists()) {
         if (!folder.mkdirs()) {
            OseeLog.log(getClass(), Level.SEVERE, "Could not create JAR cache at " + folder.getAbsolutePath());
         }
      }
      if (!folder.isDirectory()) {
         OseeLog.log(getClass(), Level.SEVERE, "The JAR cache is not a directory! Path=" + folder.getAbsolutePath());
      }
      clean();
   }
   
   /**
    * ds component method
    */
   public void stop(){
   }
   
   /**
    * ds component method
    */
   public void bindOTEServerFolder(OTEServerFolder oteServerFolder){
      this.oteServerFolder = oteServerFolder;
   }
   
   /**
    * ds component method
    */
   public void unbindOTEServerFolder(OTEServerFolder oteServerFolder){
      this.oteServerFolder = oteServerFolder;
   }
   
   @Override
   public void clearJarCache() {
      File[] bundleFolders = folder.listFiles();
      for(File bundleFolder: bundleFolders){
         if(bundleFolder.isDirectory()){
            File[] jars = bundleFolder.listFiles(new FileFilter() {
               @Override
               public boolean accept(File file) {
                  return file.getAbsolutePath().endsWith(".jar");
               }
            });
            for(File jar:jars){
               jar.delete();
            }
         } else {
            bundleFolder.delete();
         }
      }
   }
   
   @Override
   public File save(String symbolicName, String md5Digest, InputStream servedBundleIn) throws IOException {
      File namedFolder = new File(folder, symbolicName);
      if(namedFolder.exists() && !namedFolder.isDirectory()){
         namedFolder.delete();
      }
      namedFolder.mkdirs();
      
      File newCachedFile = new File(namedFolder, md5Digest + ".jar");
      OutputStream cachedFileOut = new FileOutputStream(newCachedFile);
      StreamPumper.pumpData(servedBundleIn, cachedFileOut);
      cachedFileOut.close();
      servedBundleIn.close();
      writeDateFile(newCachedFile);
      return newCachedFile;
   }

   @Override
   public File get(String symbolicName, String md5Digest) throws FileNotFoundException {
      File foundFile = new File(new File(folder, symbolicName), md5Digest + ".jar");
      if(foundFile.exists()){
         writeDateFile(foundFile);
         return foundFile; 
      }
      return null;
   }

   /**
    * Write out a file that has the date a cached file was last accessed.  This file will be used to determine when it is ok to remove a file.
    * 
    * @param foundFile
    */
   private void writeDateFile(File foundFile) {
      File dateFile = new File(foundFile.getParentFile(), foundFile.getName()+ ".date");
      FileOutputStream fos = null;
      try{
         fos = new FileOutputStream(dateFile);
         fos.write(formatter.format(new Date()).getBytes());
         fos.flush();
      } catch(IOException ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      } finally {
         if(fos != null){
            try {
               fos.close();
            } catch (IOException e) {
            }
         }
      }
   }
   
   private void clean(){
      if(folder.exists()){
         File[] bundleFolders = folder.listFiles();
         if(bundleFolders != null){
            for(File bundleFolder: bundleFolders){
               if(bundleFolder.isDirectory()){
                  File[] jars = bundleFolder.listFiles(new FileFilter() {
                     @Override
                     public boolean accept(File file) {
                        return file.getAbsolutePath().endsWith(".jar");
                     }
                  });
                  if(jars.length == 0){
                     if(bundleFolder.listFiles().length == 0){
                        bundleFolder.delete();
                     }
                  } else {
                     for(File jar:jars){
                        File dateFile = new File(jar.getAbsolutePath() + ".date");
                        if(dateFile.exists()){
                           if(isDateFileOld(dateFile)){//delete files that haven't been recently used
                              if(jar.exists()){
                                 jar.delete();
                              }
                              dateFile.delete();
                           }  
                        } else { //delete a jar without a ".date" file
                           jar.delete();
                        }
                     }
                  }
               } else { //delete legacy style cache items
                  bundleFolder.delete();
               }
            }
         }
      }
   }

   private boolean isDateFileOld(File dateFile) {
      String dateStr = getDateFromFile(dateFile);
      Date date;
      try {
         date = formatter.parse(dateStr);
      } catch (ParseException e) {
         return true;
      }
      Date oldDate = getOldDate();
      if(date.before(oldDate)){
         return true;
      } else {
         return false;
      }
   }
   
   private String getDateFromFile(File dateFile){
      FileInputStream fis = null;
      String dateStr = null;
      try{
         byte[] buffer = new byte[1024];
         fis = new FileInputStream(dateFile);
         fis.read(buffer);
         dateStr = new String(buffer);
      } catch(IOException ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      } finally {
         if(fis != null){
            try {
               fis.close();
            } catch (IOException e) {
            }
         }
      }
      return dateStr;
   }
   
   private Date getOldDate(){
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.add(Calendar.DAY_OF_MONTH, -7);
      return cal.getTime();
   }

}
