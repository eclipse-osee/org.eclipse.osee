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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordWholeDocumentAttribute;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.preferences.DiffPreferencePage;

/**
 * @author Jeff C. Phillips
 */
public class WholeDocumentRenderer extends WordRenderer {

   /**
    * @param rendererId
    */
   public WholeDocumentRenderer() {
      super();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#newInstance()
    */
   @Override
   public WholeDocumentRenderer newInstance() throws OseeCoreException {
      return new WholeDocumentRenderer();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#commandId()
    */
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

   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if (artifact.isOfType(WordArtifact.WHOLE_WORD)) {
         if (presentationType == PresentationType.DIFF) {
            return WORD_PUBLICATION;
         } else {
            return SUBTYPE_TYPE_MATCH;
         }
      }
      return NO_MATCH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor, java.util.List, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      return getRenderInputStream(artifacts.iterator().next(), presentationType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      if (artifact != null) {
         Attribute<?> attribute = artifact.getSoleAttribute(WordAttribute.WHOLE_WORD_CONTENT);
         if (attribute == null) {
            attribute = artifact.createAttribute(AttributeTypeManager.getType(WordAttribute.WHOLE_WORD_CONTENT), true);
         }
         if (presentationType == PresentationType.DIFF && attribute != null && ((WordAttribute) attribute).containsWordAnnotations()) {
            throw new OseeCoreException(
                  "Trying to diff the " + artifact.getDescriptiveName() + " artifact on the " + artifact.getBranch().getBranchShortName() + " branch, which has tracked changes turned on.  All tracked changes must be removed before the artifacts can be compared.");
         }
      }

      try {
         InputStream stream =
               Streams.convertStringToInputStream(WordWholeDocumentAttribute.getEmptyDocumentContent(), "UTF-8");

         if (artifact != null) {
            String content = artifact.getSoleAttributeValue(WordAttribute.WHOLE_WORD_CONTENT);
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
      if (baseVersion == null && newerVersion == null) throw new IllegalArgumentException(
            "baseVersion and newerVersion can't both be null.");

      Branch branch = (baseVersion != null ? baseVersion.getBranch() : newerVersion.getBranch());
      IFile baseFile;
      IFile newerFile;
      Pair<String, Boolean> originalValue = null;
      Pair<String, Boolean> newAnnotationValue = null;
      Pair<String, Boolean> oldAnnotationValue = null;
      if (!StaticIdManager.hasValue(UserManager.getUser(), DiffPreferencePage.REMOVE_TRACKED_CHANGES)) {
         Attribute attribute = baseVersion.getSoleAttribute(WordAttribute.WORD_TEMPLATE_CONTENT);
         if (baseVersion != null && attribute != null) {
            String value = attribute.getValue().toString();
            if (WordAnnotationHandler.containsWordAnnotations(value)) {
               oldAnnotationValue = new Pair<String, Boolean>(value, attribute.isDirty());
               attribute.setValue(WordAnnotationHandler.removeAnnotations(value));
            }
         }
         attribute = newerVersion.getSoleAttribute(WordAttribute.WORD_TEMPLATE_CONTENT);
         if (newerVersion != null && attribute != null) {
            String value = attribute.getValue().toString();
            if (WordAnnotationHandler.containsWordAnnotations(value)) {
               newAnnotationValue = new Pair<String, Boolean>(value, attribute.isDirty());
               attribute.setValue(WordAnnotationHandler.removeAnnotations(value));
            }
         }
      }
      if (!StaticIdManager.hasValue(UserManager.getUser(), DiffPreferencePage.IDENTFY_IMAGE_CHANGES)) {
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
            baseVersion != null ? baseVersion.getSoleAttribute(WordAttribute.WORD_TEMPLATE_CONTENT) : null,
            oldAnnotationValue != null ? oldAnnotationValue : originalValue);
      WordImageChecker.restoreOriginalValue(
            newerVersion != null ? newerVersion.getSoleAttribute(WordAttribute.WORD_TEMPLATE_CONTENT) : null,
            newAnnotationValue);
      return compare(baseVersion, newerVersion, baseFile, newerFile, presentationType, show);
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, boolean show) throws OseeCoreException {
      String diffPath;
      String fileName = getStringOption("filename");
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
         String baseFileStr = baseFile.getLocation().toOSString();
         diffPath = baseFileStr.substring(0, baseFileStr.lastIndexOf('\\')) + '\\' + fileName;
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
         diffGenerator.addComparison(baseFile, newerFile, diffPath, false);
         diffGenerator.finish(diffPath.substring(0, diffPath.lastIndexOf('\\')) + "/compareDocs.vbs", show);
      }

      return diffPath;
   }
}