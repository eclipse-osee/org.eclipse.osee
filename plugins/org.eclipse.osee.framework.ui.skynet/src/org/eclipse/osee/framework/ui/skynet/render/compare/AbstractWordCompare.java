/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.render.compare;

import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.VbaWordDiffGenerator;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractWordCompare implements IComparator {
   private final FileSystemRenderer renderer;

   public AbstractWordCompare(FileSystemRenderer renderer) {
      this.renderer = renderer;
   }

   protected FileSystemRenderer getRenderer() {
      return renderer;
   }

   protected abstract PresentationType getMergePresentationType();

   private String getComparePath(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile) throws OseeCoreException {
      String diffPath;
      String fileName = renderer.getStringOption(IRenderer.FILE_NAME_OPTION);
      if (!Strings.isValid(fileName)) {
         if (baseVersion != null) {
            String baseFileStr = baseFile.getLocation().toOSString();
            diffPath =
               baseFileStr.substring(0, baseFileStr.lastIndexOf(')') + 1) + " to " + (newerVersion != null ? newerVersion.getTransactionNumber() : " deleted") + baseFileStr.substring(baseFileStr.lastIndexOf(')') + 1);
         } else {
            String baseFileStr = newerFile.getLocation().toOSString();
            diffPath =
               baseFileStr.substring(0, baseFileStr.lastIndexOf('(') + 1) + "new " + baseFileStr.substring(baseFileStr.lastIndexOf('(') + 1);
         }
      } else {
         PresentationType mergePresentation = getMergePresentationType();
         IFolder folder = RenderingUtil.getRenderFolder(baseVersion.getBranch(), mergePresentation);
         diffPath = folder.getLocation().toOSString() + '\\' + fileName;
      }
      return diffPath;
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType) throws OseeCoreException {
      String diffPath = getComparePath(baseVersion, newerVersion, baseFile, newerFile);

      VbaWordDiffGenerator diffGenerator = new VbaWordDiffGenerator();
      diffGenerator.initialize(presentationType == PresentationType.DIFF,
         presentationType == PresentationType.MERGE_EDIT);

      if (presentationType == PresentationType.MERGE_EDIT && baseVersion != null) {
         IFolder folder = RenderingUtil.getRenderFolder(baseVersion.getBranch(), PresentationType.MERGE_EDIT);
         renderer.addFileToWatcher(folder, diffPath.substring(diffPath.lastIndexOf('\\') + 1));

         diffGenerator.addComparison(baseFile, newerFile, diffPath, true);
         launchCompareVbs(diffGenerator, diffPath, "mergeDocs.vbs");
      } else {
         diffGenerator.addComparison(baseFile, newerFile, diffPath, false);
         launchCompareVbs(diffGenerator, diffPath, "/compareDocs.vbs");
      }
      return diffPath;
   }

   private void launchCompareVbs(VbaWordDiffGenerator diffGenerator, String diffPath, String vbsScriptName) throws OseeCoreException {
      boolean show = !renderer.getBooleanOption(IRenderer.NO_DISPLAY);
      String vbsPath = diffPath.substring(0, diffPath.lastIndexOf('\\')) + vbsScriptName;
      if (RenderingUtil.arePopupsAllowed()) {
         diffGenerator.finish(vbsPath, show);
      } else {
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO,
            String.format("Test - Skip launch of [%s] for [%s]", vbsScriptName, vbsPath));
      }
   }
}
