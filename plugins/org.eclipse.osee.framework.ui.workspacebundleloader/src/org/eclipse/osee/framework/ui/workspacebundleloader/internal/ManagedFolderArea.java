package org.eclipse.osee.framework.ui.workspacebundleloader.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.workspacebundleloader.BundleInfoLite;
import org.eclipse.osee.framework.ui.workspacebundleloader.WorkspaceBundleLoadCoordinator;

public class ManagedFolderArea {

   private File bundleLocationFolder;

   public ManagedFolderArea(File temporaryBundleLocationFolder) {
      this.bundleLocationFolder = temporaryBundleLocationFolder;
   }

   public void initialize() {
      bundleLocationFolder = setupTemporaryBundle(this.bundleLocationFolder);
   }

   private File setupTemporaryBundle(final File folder) {
      File folderToReturn = folder;
      if (!folderToReturn.exists()) {
         if (!folderToReturn.mkdirs()) {
            folderToReturn = makeTempFolder();
         }
      } else if (folderToReturn.exists() && !folderToReturn.isDirectory()) {
         folderToReturn = makeTempFolder();
      } else if (folderToReturn.exists()) {
         cleanOutDirectory(folderToReturn);
      }
      return folderToReturn;
   }

   /**
    * should be a flat list of folders with the symbolic name of the bundle and then a version for each jar underneath
    * each folder.
    *
    * @param folderToReturn
    */
   private void cleanOutDirectory(File folderRoot) {
      File[] symbolicNameFolders = folderRoot.listFiles();
      for (File folder : symbolicNameFolders) {
         if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
               file.delete();
            }
            folder.delete();
         }
      }
   }

   private File makeTempFolder() {
      File folder = new File(System.getProperty("java.io.tmpdir"));
      File oteFolder = new File(folder, "otebundleload");
      if (!oteFolder.exists()) {
         oteFolder.mkdirs();
      }
      return oteFolder;
   }

   @SuppressWarnings("resource")
   public List<BundleInfoLite> copyDeltasToManagedFolder(List<BundleInfoLite> copies) {
      List<BundleInfoLite> bundlesAdded = new ArrayList<>();
      for (BundleInfoLite info : copies) {
         File folder = new File(bundleLocationFolder, info.getSymbolicName());
         folder.mkdirs();
         File newFile = new File(folder, info.getVersion() + ".jar");
         if (newFile.exists()) {
            newFile.delete();
         }
         FileChannel out = null;
         FileChannel in = null;
         try {
            out = new FileOutputStream(newFile).getChannel();
            String path = info.getSystemLocation().toURI().getPath();
            in = new FileInputStream(new File(path)).getChannel();

            long position = 0;
            long size = in.size();
            while (position < size) {
               position += in.transferTo(position, size, out);
            }
            BundleInfoLite newBundle = new BundleInfoLite(newFile.toURI().toURL());
            bundlesAdded.add(newBundle);
         } catch (IOException e) {
            OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
         } catch (URISyntaxException e) {
            OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
         } finally {
            try {
               if (in != null) {
                  in.close();
               }
            } catch (IOException e) {
               // do nothing
            }
            try {
               if (out != null) {
                  out.close();
               }
            } catch (IOException e) {
               // do nothing
            }
         }
      }
      return bundlesAdded;
   }

}
