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
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;

/**
 * @author Ryan D. Brooks
 */
public class EditingFolderVisitor implements IResourceDeltaVisitor {
   private IFolder workingFolder;
   private String prefixPath;
   private int prefixLength;
   public static int[] visitorCounters = new int[8];
   private static final Pattern tempFileP = Pattern.compile("(%7E|~)(\\$.*)");
   private Set<String> fileNameSet;

   /**
    * @param workingFolder
    */
   EditingFolderVisitor(IFolder workingFolder) {
      this.workingFolder = workingFolder;
      this.prefixPath = workingFolder.getFullPath().toString();
      this.prefixLength = prefixPath.length();
      this.fileNameSet = new HashSet<String>();
   }

   /**
    * @return whether to visit the children
    */
   public boolean visit(IResourceDelta delta) {
      visitorCounters[0]++;

      IPath visitPath = delta.getFullPath();
      String visitPathStr = visitPath.toString();

      if (visitPathStr.length() <= prefixLength) {
         // also handles case of exactly working folder
         // only visit if matches prefix so far (must visit to find children)
         visitorCounters[1]++;
         return prefixPath.startsWith(visitPathStr);
      } else if (!visitPathStr.startsWith(prefixPath + "/")) { // possible child of working folder
         // the file name separator must be inlcuded to weed out directories that include are prefix in a longer name
         // i.e. prefixPath = /.osee.data/.working and we need to weed out /.osee.data/.workingGeneral
         visitorCounters[2]++;
         return false;
      } else if (visitPath.segmentCount() == workingFolder.getFullPath().segmentCount() + 1) {
         visitorCounters[3]++;
         return true;
      }

      // should only get here if resource has a parent of workingFolder
      String fileName = visitPath.lastSegment();
      visitorCounters[4]++;
      if (!tempFileP.matcher(fileName).matches()) { // skip temporary files
         File file = AWorkspace.getIFile(visitPathStr).getLocation().toFile();
         visitorCounters[5]++;
         if (file.exists()) {
            visitorCounters[6]++;
            // we have seen this file before so this should be a save and not just a file creation
            if (fileNameSet.contains(file.getName())) {
               visitorCounters[7]++;
               UpdateArtifactJob updateJob = new UpdateArtifactJob();
               updateJob.setWorkingFile(file);
               updateJob.setUser(false);
               updateJob.setPriority(Job.SHORT);
               updateJob.schedule();
            } else {
               // don't process file on the first time it is "modified" because this happens as soon as the file is written out (and not yet modified)
               fileNameSet.add(file.getName());
            }
         }
      }

      return true;
   }
}