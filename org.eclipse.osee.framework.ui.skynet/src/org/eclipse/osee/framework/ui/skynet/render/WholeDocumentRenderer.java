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
import org.eclipse.osee.framework.jdk.core.util.io.streams.StreamCatcher;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordWholeDocumentAttribute;
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
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

      try {
         artifact.getSoleAttributeValue(AttributeTypeManager.getTypeWithWordContentCheck(artifact,
               WordAttribute.CONTENT_NAME).getName());
      } catch (AttributeDoesNotExist ex) {
         artifact.createAttribute(
               AttributeTypeManager.getTypeWithWordContentCheck(artifact, WordAttribute.CONTENT_NAME), true);
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
   public String compare(Artifact baseVersion, Artifact newerVersion, String option, IProgressMonitor monitor, String fileName, boolean visible) throws Exception {
      if (baseVersion == null && newerVersion == null) throw new IllegalArgumentException(
            "baseVersion and newerVersion can't both be null.");

      Branch branch = (baseVersion != null ? baseVersion.getBranch() : newerVersion.getBranch());
      IFile baseFile;
      IFile newerFile;

      if (baseVersion != null) {
         baseFile = renderForDiff(monitor, baseVersion, option);
      } else {
         baseFile = renderForDiff(monitor, branch, option);
      }

      if (newerVersion != null) {
         newerFile = renderForDiff(monitor, newerVersion, option);
      } else {
         newerFile = renderForDiff(monitor, branch, null);
      }

      String diffPath;

      if (fileName == null || fileName.equals("")) {
         if (baseVersion != null) {
            String baseFileStr = baseFile.getLocation().toOSString();
            diffPath =
                  baseFileStr.substring(0, baseFileStr.lastIndexOf(')')) + " to " + (newerVersion != null ? newerVersion.getTransactionNumber() : " deleted") + baseFileStr.substring(baseFileStr.lastIndexOf(')'));
         } else {
            String baseFileStr = newerFile.getLocation().toOSString();
            diffPath =
                  baseFileStr.substring(0, baseFileStr.lastIndexOf('(') + 1) + "new " + baseFileStr.substring(baseFileStr.lastIndexOf('(') + 1);
         }
      } else {
         String baseFileStr = baseFile.getLocation().toOSString();
         diffPath = baseFileStr.substring(0, baseFileStr.lastIndexOf('\\')) + '\\' + fileName;
      }

      compare(baseFile, newerFile, diffPath, visible);

      return diffPath;
   }

   private void compare(IFile baseFile, IFile newerFile, String diffPath, boolean visible) throws IOException, InterruptedException {
      File vbDiffScript = plugin.getPluginFile("support/compareDocs.vbs");

      // quotes are neccessary because of Runtime.exec wraps the last element in quotes...crazy
      String cmd[] =
            {
                  "cmd",
                  "/s /c",
                  "\"" + vbDiffScript.getPath() + "\"",
                  "/author:CoolOseeUser\" /diffPath:\"" + diffPath + "\" /detectFormatChanges:true /ver1:\"" + baseFile.getLocation().toOSString() + "\" /ver2:\"" + newerFile.getLocation().toOSString() + "\" /visible:\"" + visible};

      Process proc = Runtime.getRuntime().exec(cmd);

      StreamCatcher errorCatcher = new StreamCatcher(proc.getErrorStream(), "ERROR", logger);
      StreamCatcher outputCatcher = new StreamCatcher(proc.getInputStream(), "OUTPUT");

      errorCatcher.start();
      outputCatcher.start();
      proc.waitFor();
   }

}