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

import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.VbaWordDiffGenerator;
import org.eclipse.osee.framework.ui.skynet.render.WordImageChecker;

public class WholeWordCompare implements IComparator {
   private static final IAttributeType ATTRIBUTE_TYPE = CoreAttributeTypes.WHOLE_WORD_CONTENT;
   private final ArtifactDeltaToFileConverter converter;

   public WholeWordCompare(FileSystemRenderer renderer) {
      this.converter = new ArtifactDeltaToFileConverter(renderer);
   }

   @Override
   public String compare(IProgressMonitor monitor, PresentationType presentationType, ArtifactDelta delta, boolean show) throws OseeCoreException {
      if (delta.getStartArtifact() == null && delta.getEndArtifact() == null) {
         throw new OseeArgumentException("baseVersion and newerVersion can't both be null.");
      }
      Pair<String, Boolean> originalValue = null;
      Pair<String, Boolean> newAnnotationValue = null;
      Pair<String, Boolean> oldAnnotationValue = null;

      Artifact baseArtifact = delta.getStartArtifact();
      Artifact newerArtifact = delta.getEndArtifact();
      Attribute<String> baseContent = getWordContent(baseArtifact, ATTRIBUTE_TYPE);
      Attribute<String> newerContent = getWordContent(newerArtifact, ATTRIBUTE_TYPE);

      if (!UserManager.getUser().getBooleanSetting(MsWordPreferencePage.REMOVE_TRACKED_CHANGES)) {
         oldAnnotationValue = removeAnnotations(baseContent);
         newAnnotationValue = removeAnnotations(newerContent);
      }

      if (!UserManager.getUser().getBooleanSetting(MsWordPreferencePage.IDENTFY_IMAGE_CHANGES)) {
         originalValue = WordImageChecker.checkForImageDiffs(baseContent, newerContent);
      }

      Pair<IFile, IFile> compareFiles = converter.convertToFile(presentationType, delta);

      WordImageChecker.restoreOriginalValue(baseContent,
            oldAnnotationValue != null ? oldAnnotationValue : originalValue);
      WordImageChecker.restoreOriginalValue(newerContent, newAnnotationValue);
      return compare(baseArtifact, newerArtifact, compareFiles.getFirst(), compareFiles.getSecond(), presentationType,
            show);
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, boolean show) throws OseeCoreException {
      String diffPath;
      String fileName = converter.getRenderer().getStringOption("fileName");
      if (fileName == null || fileName.equals("")) {
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
         diffPath =
               RenderingUtil.getRenderFolder(baseVersion.getBranch(), PresentationType.SPECIALIZED_EDIT).getLocation().toOSString() + '\\' + fileName;
      }

      VbaWordDiffGenerator diffGenerator = new VbaWordDiffGenerator();
      diffGenerator.initialize(presentationType == PresentationType.DIFF,
            presentationType == PresentationType.MERGE_EDIT);
      if (presentationType == PresentationType.MERGE_EDIT && baseVersion != null) {
         IFolder folder = RenderingUtil.getRenderFolder(baseVersion.getBranch(), PresentationType.MERGE_EDIT);
         converter.getRenderer().addFileToWatcher(folder, diffPath.substring(diffPath.lastIndexOf('\\') + 1));
         diffGenerator.addComparison(baseFile, newerFile, diffPath, true);
         diffGenerator.finish(diffPath.substring(0, diffPath.lastIndexOf('\\')) + "mergeDocs.vbs", show);
      } else {
         if (RenderingUtil.arePopupsAllowed()) {
            diffGenerator.addComparison(baseFile, newerFile, diffPath, false);
            diffGenerator.finish(diffPath.substring(0, diffPath.lastIndexOf('\\')) + "/compareDocs.vbs", show);
         }
      }
      return diffPath;
   }

   @Override
   public void compareArtifacts(IProgressMonitor monitor, PresentationType presentationType, Collection<ArtifactDelta> itemsToCompare) throws OseeCoreException {
      for (ArtifactDelta entry : itemsToCompare) {
         compare(monitor, presentationType, entry, true);
      }
   }

   private Pair<String, Boolean> removeAnnotations(Attribute<String> attribute) throws OseeCoreException {
      Pair<String, Boolean> annotation = null;
      if (attribute != null) {
         String value = attribute.getValue();
         if (WordAnnotationHandler.containsWordAnnotations(value)) {
            annotation = new Pair<String, Boolean>(value, attribute.isDirty());
            attribute.setFromString(WordAnnotationHandler.removeAnnotations(value));
         }
      }
      return annotation;
   }

   private Attribute<String> getWordContent(Artifact artifact, IAttributeType attributeType) throws OseeCoreException {
      Attribute<String> toReturn = null;
      if (artifact != null && !artifact.isDeleted()) {
         toReturn = artifact.getSoleAttribute(attributeType);
      }
      return toReturn;
   }

}
