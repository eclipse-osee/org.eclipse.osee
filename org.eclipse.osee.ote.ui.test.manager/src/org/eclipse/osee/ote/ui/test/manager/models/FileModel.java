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
package org.eclipse.osee.ote.ui.test.manager.models;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.ui.PlatformUI;



public class FileModel {

   
   private File file = null;
   private IFile iFile = null;
   private long lastModified = 0;
   private String path = "";
   private String rawFilename = "";
   private String text = null;

   public FileModel(String rawFilename) {
      this.rawFilename = rawFilename;
      if (getIFile() != null)
         lastModified = getIFile().getModificationStamp();
   }

   public boolean exists() {
      return getFile().exists();
   }

   /**
    * @return Returns the file.
    */
   public File getFile() {
      if (file == null) {
         file = new File(rawFilename);
      }
      return file;
   }

   /**
    * @return Returns the iFile.
    */
   public IFile getIFile() {
      if (iFile == null) {
         if (path.equals("")) {
            if (!rawFilename.equals("")) {
               iFile = AWorkspace.getIFile(rawFilename);
            }
         }
      }
      return iFile;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return AFile.justFilename(rawFilename);
   }

   /**
    * @return Returns the path.
    */
   public String getPath() {
      if (iFile == null)
         iFile = getIFile();
      if (iFile != null)
         path = iFile.getFullPath().toString();
      return path;
   }

   /**
    * @return Returns the rawFilename.
    */
   public String getRawFilename() {
      return rawFilename;
   }
   
   public String getWorkspaceRelativePath() {
      IWorkspace ws = ResourcesPlugin.getWorkspace();
      IFile ifile = ws.getRoot().getFileForLocation(new Path(rawFilename));
      if (!ifile.exists()) {
         return null;
      } else {
         return ifile.getFullPath().toString();
      }
   }

   public String getText() {
      if (iFile == null)
         getIFile();
      if (iFile == null)
         return "";
      if (text == null || iFile.getModificationStamp() != lastModified) {
         text = AFile.readFile(rawFilename);
         OseeLog.log(TestManagerPlugin.class, Level.INFO, "getText: Reading file " + getName());
      }
      else
         OseeLog.log(TestManagerPlugin.class, Level.INFO, "getText: Using buffered file " + getName());
      lastModified = iFile.getModificationStamp();
      return text;
   }

   public boolean isModified() {
      if (iFile == null)
         getIFile();
      if (iFile == null) {
         OseeLog.log(TestManagerPlugin.class, Level.WARNING, "Can't Read iFile");
         return true;
      }
      return (iFile.getModificationStamp() != lastModified);
   }

   public void openEditor() {
      if(getIFile() != null){
         AWorkspace.openEditor(getIFile());
      }
   }

   public void openPackageExplorer() {
      OseeLog.log(TestManagerPlugin.class, Level.INFO, "Show in explorer " + getName());
      // Open in Package Explorer and error if can't
      boolean success = AWorkspace.showInPackageExplorer(getIFile());
//      if(!success){
//         success = AWorkspace.showInResourceNavigator(getIFile());
//      }
      if (!success) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Error",
               "Can't Show in Explorer\n\n" + getName());
      }
      // As a convenience, open in Navigator, but don't error
      success = AWorkspace.showInResourceNavigator(getIFile());
   }

   /**
    * @param path The path to set.
    */
   public void setPath(String path) {
      this.path = path;
   }

   /**
    * @param rawFilename The rawFilename to set.
    */
   public void setRawFilename(String rawFilename) {
      this.rawFilename = rawFilename;
   }
}