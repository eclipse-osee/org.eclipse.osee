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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordWholeDocumentAttribute;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.word.WordConverter;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.program.Program;

public class WholeDocumentRenderer extends FileRenderer {
   // We need MS Word, so look for the program that is for .doc files
   private static final Program wordApp = Program.findProgram("doc");
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(WholeDocumentRenderer.class);
   private static final OseeUiActivator plugin = SkynetGuiPlugin.getInstance();

   public WholeDocumentRenderer() throws TransformerConfigurationException, IOException, TransformerFactoryConfigurationError {
   }

   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if (artifact instanceof WordArtifact && ((WordArtifact) artifact).isWholeWordArtifact()) {
         return SUBTYPE_TYPE_MATCH;
      }
      return NO_MATCH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getAssociatedExtension(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public String getAssociatedExtension(Artifact artifact) {
      return ".xml";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor, java.util.List, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(IProgressMonitor monitor, List<Artifact> artifacts, String option, PresentationType presentationType) throws Exception {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(IProgressMonitor monitor, Artifact artifact, String option, PresentationType presentationType) throws Exception {
      if (artifact != null) {
         Attribute<?> attribute =
               artifact.getSoleAttribute(AttributeTypeManager.getTypeWithWordContentCheck(artifact,
                     WordAttribute.CONTENT_NAME).getName());
         if (attribute == null) {
            attribute =
                  artifact.createAttribute(AttributeTypeManager.getTypeWithWordContentCheck(artifact,
                        WordAttribute.CONTENT_NAME), true);
         }
         if (presentationType == PresentationType.DIFF && attribute != null && ((WordAttribute) attribute).mergeMarkupPresent()) {
            throw new OseeCoreException(
                  "Trying to diff the " + artifact.getDescriptiveName() + " artifact on the " + artifact.getBranch().getBranchShortName() + " branch, which has tracked changes turned on.  All tracked changes must be removed before the artifacts can be compared.");

         }
      }

      InputStream stream =
            Streams.convertStringToInputStream(WordWholeDocumentAttribute.getEmptyDocumentContent(), "UTF-8");

      if (artifact != null) {
         String content =
               artifact.getSoleAttributeValue(AttributeTypeManager.getTypeWithWordContentCheck(artifact,
                     WordAttribute.CONTENT_NAME).getName());
         String myGuid = artifact.getGuid();
         content = WordUtil.addGUIDToDocument(myGuid, content);
         stream = Streams.convertStringToInputStream(content, "UTF-8");
      }
      return stream;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer#getAssociatedProgram(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      return wordApp;
   }

   @Override
   public String generateHtml(Artifact artifact, IProgressMonitor monitor) {
      String html = null;
      InputStream xml = null;

      try {
         xml = getRenderInputStream(monitor, artifact, null, PresentationType.PREVIEW);
         html = WordConverter.getInstance().toHtml(xml);
      } catch (java.lang.StackOverflowError error) {
         logger.log(Level.SEVERE, error.getLocalizedMessage(), error);
         html = "Stack overflow error caused by recursion in the xslt transform";
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         html = ex.getLocalizedMessage();
      } finally {
         try {
            if (xml != null) {
               xml.close();
            }
         } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            html = ex.getLocalizedMessage();
         }
      }
      return html;
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, String option, IProgressMonitor monitor, String fileName, PresentationType presentationType) throws Exception {
      if (baseVersion == null && newerVersion == null) throw new IllegalArgumentException(
            "baseVersion and newerVersion can't both be null.");

      Branch branch = (baseVersion != null ? baseVersion.getBranch() : newerVersion.getBranch());
      IFile baseFile;
      IFile newerFile;

      if (baseVersion != null) {
         if (presentationType == PresentationType.MERGE || presentationType == PresentationType.MERGE_EDIT) {
            baseFile = renderForMerge(monitor, baseVersion, option, presentationType);
         } else {
            baseFile = renderForDiff(monitor, baseVersion, option);
         }
      } else {
         baseFile = renderForDiff(monitor, branch, option);
      }

      if (newerVersion != null) {
         if (presentationType == PresentationType.MERGE || presentationType == PresentationType.MERGE_EDIT) {
            newerFile = renderForMerge(monitor, newerVersion, option, presentationType);
         } else {
            newerFile = renderForDiff(monitor, newerVersion, option);
         }
      } else {
         newerFile = renderForDiff(monitor, branch, null);
      }

      return compare(baseVersion, newerVersion, baseFile, newerFile, fileName, presentationType);
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, String fileName, PresentationType presentationType) throws Exception {
      String diffPath;

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
      diffGenerator.initialize(presentationType != PresentationType.MERGE, true);
      if (presentationType == PresentationType.MERGE_EDIT && baseVersion != null) {
         addFileToWatcher(getRenderFolder(baseVersion.getBranch(), PresentationType.EDIT),
               diffPath.substring(diffPath.lastIndexOf('\\') + 1));
         diffGenerator.addComparison(baseFile, newerFile, diffPath, true);
         diffGenerator.finish(diffPath.substring(0, diffPath.lastIndexOf('\\')) + "mergeDocs.vbs");
      } else {
         diffGenerator.addComparison(baseFile, newerFile, diffPath, false);
         diffGenerator.finish(diffPath.substring(0, diffPath.lastIndexOf('\\')) + "/compareDocs.vbs");
      }

      return diffPath;
   }

}