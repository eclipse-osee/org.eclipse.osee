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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.artifactElement.IElementExtractor;
import org.eclipse.osee.framework.ui.skynet.render.artifactElement.MergeEditArtifactElementExtractor;
import org.eclipse.osee.framework.ui.skynet.render.artifactElement.WordArtifactElementExtractor;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class UpdateArtifactJob extends UpdateJob {
   private static final Pattern guidPattern = Pattern.compile(".*\\(([^)]+)\\)[^()]*");
   private static final Pattern multiPattern = Pattern.compile(".*[^()]*");
   private Element oleDataElement;
   private String singleGuid = null;

   public UpdateArtifactJob() {
      super("Update Artifact");
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      try {
         processUpdate();
      } catch (Exception ex) {
         return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, IStatus.OK, ex.getLocalizedMessage(), ex);
      }
      return Status.OK_STATUS;
   }

   private void processUpdate() throws Exception {
      Branch branch = BranchManager.fromFileName(workingFile.getParentFile().getName());
      if (branch.isEditable()) {
         Matcher singleEditMatcher = guidPattern.matcher(workingFile.getName());
         Matcher multiEditMatcher = multiPattern.matcher(workingFile.getName());

         if (singleEditMatcher.matches()) {
            singleGuid = singleEditMatcher.group(1);

            if (isMergeEdit()) {
               processMergeEdit(new MergeEditArtifactElementExtractor(extractJaxpDocument()), branch);
            } else {
               Artifact artifact = ArtifactQuery.getArtifactFromId(singleGuid, branch);
               processSingleEdit(artifact);
            }
         } else if (multiEditMatcher.matches()) {
            WordArtifactElementExtractor elementExtractor = new WordArtifactElementExtractor(extractJaxpDocument());
            processMultiEdit(elementExtractor, branch);
         }
      }
   }

   private boolean isMergeEdit() {
      return workingFile.getAbsolutePath().contains("mergeEdit");
   }

   private Document extractJaxpDocument() throws ParserConfigurationException, SAXException, IOException {
      Document document;
      InputStream inputStream = new BufferedInputStream(new FileInputStream(workingFile));
      try {
         document = Jaxp.readXmlDocument(inputStream);
      } finally {
         Lib.close(inputStream);
      }
      return document;
   }

   private void processMergeEdit(IElementExtractor elementExtractor, Branch branch) throws OseeCoreException, DOMException, ParserConfigurationException, SAXException, IOException {
      wordArtifactUpdate(elementExtractor, branch);
   }

   private void processSingleEdit(Artifact artifact) throws OseeCoreException, ParserConfigurationException, SAXException, IOException {
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.WORD_TEMPLATE_CONTENT)) {
         WordArtifactElementExtractor elementExtractor = new WordArtifactElementExtractor(extractJaxpDocument());
         wordArtifactUpdate(elementExtractor, artifact.getBranch());
      } else {
         processNativeDocuments(artifact);
      }
   }

   private void processNativeDocuments(Artifact artifact) throws OseeCoreException, FileNotFoundException {
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.WHOLE_WORD_CONTENT)) {
         updateNativeArtifact(artifact, CoreAttributeTypes.WHOLE_WORD_CONTENT);
      } else if (artifact.isAttributeTypeValid(CoreAttributeTypes.NATIVE_CONTENT)) {
         updateNativeArtifact(artifact, CoreAttributeTypes.NATIVE_CONTENT);
      } else {
         throw new OseeArgumentException("Artifact must be of type WordArtifact or NativeArtifact.");
      }
   }

   private void processMultiEdit(IElementExtractor elementExtractor, Branch branch) throws OseeCoreException, ParserConfigurationException, SAXException, IOException {
      wordArtifactUpdate(elementExtractor, branch);
   }

   private void logUpdateSkip(Artifact artifact) {
      OseeLog.log(SkynetGuiPlugin.class, Level.INFO,
         String.format("Skipping update - artifact [%s] is read-only", artifact.toString()));
   }

   private void updateNativeArtifact(Artifact artifact, CoreAttributeTypes attributeType) throws OseeCoreException, FileNotFoundException {
      if (!artifact.isReadOnly()) {
         InputStream stream = null;
         try {
            stream = new BufferedInputStream(new FileInputStream(workingFile));
            artifact.setSoleAttributeFromStream(attributeType, stream);
            artifact.persist();
         } finally {
            Lib.close(stream);
         }
      } else {
         logUpdateSkip(artifact);
      }
   }

   private void wordArtifactUpdate(IElementExtractor elementExtractor, Branch branch) throws OseeCoreException, DOMException, ParserConfigurationException, SAXException, IOException {
      List<String> deletedGuids = new LinkedList<String>();
      Collection<Element> artElements = elementExtractor.extractElements();
      oleDataElement = elementExtractor.getOleDataElement();

      try {
         boolean singleArtifact = artElements.size() == 1;
         boolean containsOleData = false;
         for (Element artElement : artElements) {
            String guid = getGuid(artElement);
            Artifact artifact = ArtifactQuery.getArtifactFromId(guid, branch);

            if (artifact == null) {
               deletedGuids.add(guid);
            } else {
               if (artifact.isReadOnly()) {
                  logUpdateSkip(artifact);
                  continue;
               }
               containsOleData = !artifact.getSoleAttributeValue(CoreAttributeTypes.WORD_OLE_DATA, "").equals("");

               if (oleDataElement == null && containsOleData) {
                  artifact.setSoleAttributeValue(CoreAttributeTypes.WORD_OLE_DATA, "");
               } else if (oleDataElement != null && singleArtifact) {
                  artifact.setSoleAttributeFromStream(CoreAttributeTypes.WORD_OLE_DATA, new ByteArrayInputStream(
                     WordTemplateRenderer.getFormattedContent(oleDataElement)));
               }
               String content = null;
               try {
                  content =
                     Lib.inputStreamToString(new ByteArrayInputStream(
                        WordTemplateRenderer.getFormattedContent(artElement)));
               } catch (IOException ex) {
                  OseeExceptions.wrapAndThrow(ex);
               }
               // Only update if editing a single artifact or if in
               // multi-edit mode only update if the artifact has at least one textual change (if
               // the MUTI_EDIT_SAVE_ALL_CHANGES preference is not set).
               boolean multiSave =
                  UserManager.getUser().getBooleanSetting(MsWordPreferencePage.MUTI_EDIT_SAVE_ALL_CHANGES) || !WordUtil.textOnly(
                     artifact.getSoleAttributeValue(CoreAttributeTypes.WORD_TEMPLATE_CONTENT).toString()).equals(
                     WordUtil.textOnly(content));

               if (singleArtifact || multiSave) {
                  // TODO
                  if (artElement.getNodeName().endsWith("body")) {
                     // This code pulls out all of the stuff after the inserted listnum reordering
                     // stuff. This needs to be
                     // here so that we remove unwanted template information from single editing
                     content = content.replace(WordMLProducer.LISTNUM_FIELD_HEAD, "");
                  }
                  LinkType linkType = LinkType.OSEE_SERVER_LINK;
                  content = WordMlLinkHandler.unlink(linkType, artifact, content);
                  artifact.setSoleAttributeValue(CoreAttributeTypes.WORD_TEMPLATE_CONTENT, content);
               }
               artifact.persist();
            }
         }
      } finally {
         if (!deletedGuids.isEmpty()) {
            throw new OseeStateException("The following deleted artifacts could not be saved: " + Collections.toString(
               ",", deletedGuids));
         }
      }
   }

   private String getGuid(Element artifactElement) throws OseeCoreException {
      if (singleGuid != null) {
         return singleGuid;
      }

      NamedNodeMap attributes = artifactElement.getAttributes();
      for (int i = 0; i < attributes.getLength(); i++) {
         // MS Word has a nasty habit of changing the namespace say from
         // ns0 to ns1, so we must
         // ignore the namespace by using endsWith()
         if (attributes.item(i).getNodeName().endsWith("guid")) {
            return attributes.item(i).getNodeValue();
         }
      }
      throw new OseeArgumentException("didn't find the guid attribure in element: " + artifactElement);
   }
}