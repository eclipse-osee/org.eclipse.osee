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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordWholeDocumentAttribute;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;

/**
 * @author Jeff C. Phillips
 */
public class WholeDocumentRenderer extends WordRenderer {

   private static boolean noPopups = false;

   /**
    * @param rendererId
    */
   public WholeDocumentRenderer() {
      super();
   }

   @Override
   public WholeDocumentRenderer newInstance() throws OseeCoreException {
      return new WholeDocumentRenderer();
   }

   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(1);

      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.wholedocumenteditor.command");
      } else if (presentationType == PresentationType.PREVIEW) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.wholewordpreview.command");
      }

      return commandIds;
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.WHOLE_WORD_CONTENT.getName())) {
         if (presentationType == PresentationType.DIFF) {
            return WORD_PUBLICATION;
         } else {
            return SUBTYPE_TYPE_MATCH;
         }
      }
      return NO_MATCH;
   }

   @Override
   public InputStream getRenderInputStream(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      return getRenderInputStream(artifacts.iterator().next(), presentationType);
   }

   @Override
   public InputStream getRenderInputStream(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      try {
         InputStream stream;

         if (artifact == null) {
            stream = Streams.convertStringToInputStream(WordWholeDocumentAttribute.getEmptyDocumentContent(), "UTF-8");
         } else {
            String content = artifact.getOrInitializeSoleAttributeValue(WordAttribute.WHOLE_WORD_CONTENT);
            if (presentationType == PresentationType.DIFF && WordAnnotationHandler.containsWordAnnotations(content)) {
               throw new OseeStateException(
                     "Trying to diff the " + artifact.getName() + " artifact on the " + artifact.getBranch().getShortName() + " branch, which has tracked changes turned on.  All tracked changes must be removed before the artifacts can be compared.");
            }

            String myGuid = artifact.getGuid();
            content = WordUtil.addGUIDToDocument(myGuid, content);

            LinkType linkType = LinkType.OSEE_SERVER_LINK;
            content = WordMlLinkHandler.link(linkType, artifact, content);
            stream = Streams.convertStringToInputStream(content, "UTF-8");
         }
         return stream;
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IProgressMonitor monitor, PresentationType presentationType, boolean show) throws OseeCoreException {
      if (baseVersion == null && newerVersion == null) {
         throw new IllegalArgumentException("baseVersion and newerVersion can't both be null.");
      }

      Branch branch = baseVersion != null ? baseVersion.getBranch() : newerVersion.getBranch();
      IFile baseFile;
      IFile newerFile;
      Pair<String, Boolean> originalValue = null;
      Pair<String, Boolean> newAnnotationValue = null;
      Pair<String, Boolean> oldAnnotationValue = null;
      if (!UserManager.getUser().getBooleanSetting(MsWordPreferencePage.REMOVE_TRACKED_CHANGES)) {

         if (baseVersion != null) {
            Attribute<?> baseAttribute = baseVersion.getSoleAttribute(WordAttribute.WHOLE_WORD_CONTENT);
            if (baseAttribute != null) {
               String value = baseAttribute.getValue().toString();
               if (WordAnnotationHandler.containsWordAnnotations(value)) {
                  oldAnnotationValue = new Pair<String, Boolean>(value, baseAttribute.isDirty());
                  baseAttribute.setFromString(WordAnnotationHandler.removeAnnotations(value));
               }
            }
         }

         if (newerVersion != null) {
            Attribute<?> newerAttribute = newerVersion.getSoleAttribute(WordAttribute.WHOLE_WORD_CONTENT);
            if (newerAttribute != null) {
               String value = newerAttribute.getValue().toString();
               if (WordAnnotationHandler.containsWordAnnotations(value)) {
                  newAnnotationValue = new Pair<String, Boolean>(value, newerAttribute.isDirty());
                  newerAttribute.setFromString(WordAnnotationHandler.removeAnnotations(value));
               }
            }
         }
      }

      if (!UserManager.getUser().getBooleanSetting(MsWordPreferencePage.IDENTFY_IMAGE_CHANGES)) {
         originalValue =
               WordImageChecker.checkForImageDiffs(
                     baseVersion != null ? baseVersion.getSoleAttribute(WordAttribute.WHOLE_WORD_CONTENT) : null,
                     newerVersion != null ? newerVersion.getSoleAttribute(WordAttribute.WHOLE_WORD_CONTENT) : null);
      }
      if (baseVersion != null) {
         if (presentationType == PresentationType.MERGE || presentationType == PresentationType.MERGE_EDIT) {
            baseFile = renderForMerge(monitor, baseVersion, presentationType);
         } else {
            baseFile = renderForDiff(monitor, baseVersion);
         }
      } else {
         baseFile = renderForDiff(monitor, branch);
      }

      if (newerVersion != null) {
         if (presentationType == PresentationType.MERGE || presentationType == PresentationType.MERGE_EDIT) {
            newerFile = renderForMerge(monitor, newerVersion, presentationType);
         } else {
            newerFile = renderForDiff(monitor, newerVersion);
         }
      } else {
         newerFile = renderForDiff(monitor, branch);
      }
      WordImageChecker.restoreOriginalValue(
            baseVersion != null ? baseVersion.getSoleAttribute(WordAttribute.WHOLE_WORD_CONTENT) : null,
            oldAnnotationValue != null ? oldAnnotationValue : originalValue);
      WordImageChecker.restoreOriginalValue(
            newerVersion != null ? newerVersion.getSoleAttribute(WordAttribute.WHOLE_WORD_CONTENT) : null,
            newAnnotationValue);
      return compare(baseVersion, newerVersion, baseFile, newerFile, presentationType, show);
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, boolean show) throws OseeCoreException {
      String diffPath;
      String fileName = getStringOption("fileName");
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
               getRenderFolder(baseVersion.getBranch(), PresentationType.SPECIALIZED_EDIT).getLocation().toOSString() + '\\' + fileName;
      }

      VbaWordDiffGenerator diffGenerator = new VbaWordDiffGenerator();
      diffGenerator.initialize(presentationType == PresentationType.DIFF,
            presentationType == PresentationType.MERGE_EDIT);
      if (presentationType == PresentationType.MERGE_EDIT && baseVersion != null) {
         addFileToWatcher(getRenderFolder(baseVersion.getBranch(), PresentationType.SPECIALIZED_EDIT),
               diffPath.substring(diffPath.lastIndexOf('\\') + 1));
         diffGenerator.addComparison(baseFile, newerFile, diffPath, true);
         diffGenerator.finish(diffPath.substring(0, diffPath.lastIndexOf('\\')) + "mergeDocs.vbs", show);
      } else {
         if (!noPopups) {
            diffGenerator.addComparison(baseFile, newerFile, diffPath, false);
            diffGenerator.finish(diffPath.substring(0, diffPath.lastIndexOf('\\')) + "/compareDocs.vbs", show);
         }
      }

      return diffPath;
   }

   public static boolean isNoPopups() {
      return noPopups;
   }

   public static void setNoPopups(boolean noPopups) {
      WholeDocumentRenderer.noPopups = noPopups;
   }
}