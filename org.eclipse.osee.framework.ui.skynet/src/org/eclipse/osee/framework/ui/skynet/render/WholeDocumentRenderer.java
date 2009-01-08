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
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordWholeDocumentAttribute;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Jeff C. Phillips
 */
public class WholeDocumentRenderer extends WordRenderer {

   public static final String RENDERER_EXTENSION = "org.eclipse.osee.framework.ui.skynet.render.WholeDocumentRenderer";

   /**
    * @param rendererId
    */
   public WholeDocumentRenderer(String rendererId) {
      super(rendererId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#newInstance()
    */
   @Override
   public WholeDocumentRenderer newInstance() throws OseeCoreException {
      return new WholeDocumentRenderer(getId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#commandId()
    */
   @Override
   public String getCommandId() {
      return "org.eclipse.osee.framework.ui.skynet.wholedocumenteditor.command";
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
         if (presentationType == PresentationType.DIFF && attribute != null && ((WordAttribute) attribute).mergeMarkupPresent()) {
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