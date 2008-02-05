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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.WholeDocumentArtifact;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.swt.program.Program;

public class WholeDocumentRenderer extends FileRenderer {
   // We need MS Word, so look for the program that is for .doc files
   private static final Program wordApp = Program.findProgram("doc");

   public WholeDocumentRenderer() throws TransformerConfigurationException, IOException, TransformerFactoryConfigurationError {
   }

   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if (artifact instanceof WholeDocumentArtifact) {
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
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(IProgressMonitor monitor, Artifact artifact, String option, PresentationType presentationType) throws Exception {
      InputStream inputStream = null;

      if (artifact instanceof WholeDocumentArtifact) {
         WholeDocumentArtifact wholeDocumentArtifact = (WholeDocumentArtifact) artifact;
         String content = wholeDocumentArtifact.getSoleAttributeValue(WordAttribute.CONTENT_NAME);
         if (content == null || content.matches("")) {
            String wordLeader1 =
                  "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" + "<?mso-application progid='Word.Document'?>";
            String wordLeader2 =
                  "<w:wordDocument xmlns:w='http://schemas.microsoft.com/office/word/2003/wordml' xmlns:v='urn:schemas-microsoft-com:vml' xmlns:w10='urn:schemas-microsoft-com:office:word' xmlns:sl='http://schemas.microsoft.com/schemaLibrary/2003/core' xmlns:aml='http://schemas.microsoft.com/aml/2001/core' xmlns:wx='http://schemas.microsoft.com/office/word/2003/auxHint' xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:dt='uuid:C2F41010-65B3-11d1-A29F-00AA00C14882' xmlns:wsp='http://schemas.microsoft.com/office/word/2003/wordml/sp2' xmlns:ns0='http://www.w3.org/2001/XMLSchema' xmlns:ns1='http://eclipse.org/artifact.xsd' xmlns:st1='urn:schemas-microsoft-com:office:smarttags' w:macrosPresent='no' w:embeddedObjPresent='no' w:ocxPresent='no' xml:space='preserve'>";
            String wordBody = "<w:body></w:body>";
            String wordTrailer = "</w:wordDocument> ";
            content = wordLeader1 + wordLeader2 + wordBody + wordTrailer;
         }
         String myGuid = artifact.getGuid();
         content = WordUtil.addGUIDToDocument(myGuid, content);
         inputStream = Streams.convertStringToInputStream(content, "UTF-8");

      }
      return inputStream;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer#getAssociatedProgram(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      return wordApp;
   }

}