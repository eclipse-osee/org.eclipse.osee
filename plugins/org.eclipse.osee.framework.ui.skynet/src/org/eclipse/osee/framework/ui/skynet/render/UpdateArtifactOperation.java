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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.artifactElement.IElementExtractor;
import org.eclipse.osee.framework.ui.skynet.render.artifactElement.MergeEditArtifactElementExtractor;
import org.eclipse.osee.framework.ui.skynet.render.artifactElement.WordExtractorData;
import org.eclipse.osee.framework.ui.skynet.render.artifactElement.WordImageArtifactElementExtractor;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class UpdateArtifactOperation extends AbstractOperation {
   private final File workingFile;
   private final List<Artifact> artifacts;
   private final IOseeBranch branch;
   private final boolean threeWayMerge;

   public UpdateArtifactOperation(File workingFile, List<Artifact> artifacts, IOseeBranch branch, boolean threeWayMerge) {
      super("Update Artifact", Activator.PLUGIN_ID);
      this.workingFile = workingFile;
      this.artifacts = artifacts;
      this.branch = branch;
      this.threeWayMerge = threeWayMerge;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Collection<WordExtractorData> extractorDatas;
      Element oleDataElement;
      {
         IElementExtractor elementExtractor;
         Document document = extractJaxpDocument();
         if (threeWayMerge) {
            elementExtractor = new MergeEditArtifactElementExtractor(document);
         } else {
            elementExtractor = new WordImageArtifactElementExtractor(document);
         }
         extractorDatas = elementExtractor.extractElements();
         oleDataElement = elementExtractor.getOleDataElement();
      }
      wordArtifactUpdate(extractorDatas, oleDataElement);
   }

   private Document extractJaxpDocument() throws ParserConfigurationException, SAXException, IOException {
      Document document;
      InputStream inputStream = new BufferedInputStream(new FileInputStream(workingFile));
      try {
         document = Jaxp.nonDeferredReadXmlDocument(inputStream, "UTF-8");
      } finally {
         Lib.close(inputStream);
      }
      return document;
   }

   private void wordArtifactUpdate(Collection<WordExtractorData> extractorDatas, Element oleDataElement) throws OseeCoreException, XMLStreamException, DOMException {
      List<Artifact> deletedArtifacts = new LinkedList<Artifact>();
      try {

         boolean singleArtifact = extractorDatas.size() == 1;
         boolean containsOleData = false;
         for (WordExtractorData extractorData : extractorDatas) {
            Artifact artifact = getArtifact(extractorData);
            if (artifact.isDeleted()) {
               deletedArtifacts.add(artifact);
            } else {
               if (artifact.isReadOnly()) {
                  logUpdateSkip(artifact);
                  continue;
               }
               containsOleData = artifact.getAttributeCount(CoreAttributeTypes.WordOleData) > 0;

               if (oleDataElement == null && containsOleData) {
                  artifact.setSoleAttributeValue(CoreAttributeTypes.WordOleData, "");
               } else if (oleDataElement != null && singleArtifact) {
                  artifact.setSoleAttributeFromStream(CoreAttributeTypes.WordOleData, new ByteArrayInputStream(
                     WordTemplateRenderer.getFormattedContent(oleDataElement)));
               }
               String content = null;
               try {
                  content =
                     Lib.inputStreamToString(new ByteArrayInputStream(
                        WordTemplateRenderer.getFormattedContent(extractorData.getParentEelement())));
               } catch (IOException ex) {
                  OseeExceptions.wrapAndThrow(ex);
               }

               // Only update if editing a single artifact or if in
               // multi-edit mode only update if the artifact has at least one textual change (if
               // the MUTI_EDIT_SAVE_ALL_CHANGES preference is not set).
               boolean multiSave;
               {
                  String originalContent =
                     artifact.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent).toString();
                  multiSave =
                     UserManager.getBooleanSetting(MsWordPreferencePage.MUTI_EDIT_SAVE_ALL_CHANGES) || !WordUtil.textOnly(
                        originalContent).equals(WordUtil.textOnly(content)) || !WordUtil.referencesOnly(originalContent).equals(
                        WordUtil.referencesOnly(content));
               }

               if (singleArtifact || multiSave) {
                  // TODO Do we need this?
                  if (extractorData.getParentEelement().getNodeName().endsWith("body")) {
                     // This code pulls out all of the stuff after the inserted listnum reordering
                     // stuff. This needs to be
                     // here so that we remove unwanted template information from single editing
                     content = content.replace(WordMLProducer.LISTNUM_FIELD_HEAD, "");
                  }
                  LinkType linkType = LinkType.OSEE_SERVER_LINK;
                  content = WordMlLinkHandler.unlink(linkType, artifact, content);
                  artifact.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, content);
               }
               artifact.persist(getClass().getSimpleName());
            }
         }
      } finally {
         if (!deletedArtifacts.isEmpty()) {
            throw new OseeStateException("The following deleted artifacts could not be saved [%s]",
               Collections.toString(",", deletedArtifacts));
         }
      }
   }

   private void logUpdateSkip(Artifact artifact) {
      OseeLog.logf(Activator.class, Level.INFO, "Skipping update - artifact [%s] is read-only", artifact.toString());
   }

   private Artifact getArtifact(WordExtractorData artifactElement) throws OseeCoreException {
      Artifact artifact;
      if (artifacts.size() == 1) {
         return artifacts.get(0);
      }

      artifact = artifactElement.getArtifact(branch);
      if (artifact != null) {
         return artifact;
      }

      throw new OseeArgumentException("didn't find the guid attribure in element [%s]", artifactElement);
   }
}
