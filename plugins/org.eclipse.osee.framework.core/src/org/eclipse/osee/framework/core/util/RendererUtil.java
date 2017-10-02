/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class RendererUtil {

   private static IFolder workingFolder;
   private static IFolder compareFolder;
   private static IFolder previewFolder;

   private static final String FILENAME_WARNING_MESSAGE =
      "\n\nis approaching a large size which may cause the opening application to error. " + "\nSuggest moving your workspace to avoid potential errors. ";

   private static final Random generator = new Random();
   private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
   private static final int FILENAME_LIMIT = 215;

   public static String toFileName(IOseeBranch branch)  {
      // replace invalid filename characters \/:"*?<>| and . and ' with _
      String shortName = Strings.saferReplace(branch.getShortName(), "[\\.\\/:\"*?<>|'\\\\]+", "_");
      return encode(shortName);
   }

   private static String encode(String name)  {
      String toReturn = null;
      try {
         toReturn = URLEncoder.encode(name, "UTF-8");
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return toReturn;
   }

   private static String constructFilename(String mainName, String fileNamePrefix, String extension) {
      StringBuilder name = new StringBuilder(128);
      if (Strings.isValid(fileNamePrefix)) {
         name.append(fileNamePrefix);
         name.append("_");
      }

      name.append(mainName);
      name.append("_");
      name.append(dateFormat.format(new Date()));
      name.append("-");
      name.append(generator.nextInt(99999) + 1);
      name.append(extension);
      return name.toString();
   }

   public static IFile getRenderFile(IOseeBranch branch, PresentationType presentationType, String pathPrefix, String mainName, String extension) {
      String subFolder = toFileName(branch);
      String fileNamePrefix = null;
      if (Strings.isValid(pathPrefix)) {
         int index = pathPrefix.lastIndexOf("/");
         if (index != -1) {
            subFolder = pathPrefix.substring(0, index);
         }
         fileNamePrefix = pathPrefix.substring(index + 1);
         fileNamePrefix = fileNamePrefix.trim();
      }
      subFolder = subFolder.trim();
      String fileName = constructFilename(mainName, fileNamePrefix, extension);
      return getRenderFile(subFolder, fileName, presentationType);
   }

   private static IFile getRenderFile(String subFolder, String fileName, PresentationType presentationType)  {
      try {
         IFolder baseFolder = ensureRenderFolderExists(presentationType);
         IFolder renderFolder = baseFolder.getFolder(subFolder);
         if (!renderFolder.exists()) {
            renderFolder.create(true, true, null);
         }
         IFile file = renderFolder.getFile(fileName);

         return file;
      } catch (CoreException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public static IFolder ensureRenderFolderExists(PresentationType presentationType)  {
      IFolder toReturn = null;
      switch (presentationType) {
         case MERGE:
         case DIFF:
            compareFolder = getOrCreateFolder(compareFolder, ".compare");
            toReturn = compareFolder;
            break;
         case SPECIALIZED_EDIT:
            workingFolder = getOrCreateFolder(workingFolder, ".working");
            toReturn = workingFolder;
            break;
         case PREVIEW:
            previewFolder = getOrCreateFolder(previewFolder, ".preview");
            toReturn = previewFolder;
            break;
         default:
            throw new OseeArgumentException("Unexpected presentation type: %s", presentationType);
      }
      return toReturn;
   }

   private static IFolder getOrCreateFolder(IFolder folder, String name)  {
      IFolder toCheck = folder;
      if (toCheck == null || !toCheck.exists()) {
         toCheck = OseeData.getFolder(name);
      }
      return toCheck;
   }

   public static boolean ensureFilenameLimit(IFile file) {
      boolean withinLimit = true;
      if (Lib.isWindows()) {
         String absPath = file.getLocation().toFile().getAbsolutePath();
         if (absPath.length() > FILENAME_LIMIT) {
            withinLimit = false;
         }
      }
      return withinLimit;
   }

}
