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
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
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
   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet/debug/Renderer"));

   public UpdateArtifactJob() {
      super("Update Artifact");
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      try {
         processUpdate();
      } catch (Exception ex) {
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, Status.OK, ex.getLocalizedMessage(), ex);
      }
      return Status.OK_STATUS;
   }

   private void processUpdate() throws Exception {
      Branch branch = BranchManager.fromFileName(workingFile.getParentFile().getName());
      if (branch.isEditable()) {
         String guid = WordUtil.getGUIDFromFile(workingFile);
         if (guid == null) {
            processNonWholeDocumentUpdates(branch);
         } else {
            Artifact myArtifact = ArtifactQuery.getArtifactFromId(guid, branch);
            updateWholeDocumentArtifact(myArtifact);
         }
      }
   }

   private void processNonWholeDocumentUpdates(Branch branch) throws OseeCoreException, ParserConfigurationException, SAXException, IOException {
      Matcher singleEditMatcher = guidPattern.matcher(workingFile.getName());
      Matcher multiEditMatcher = multiPattern.matcher(workingFile.getName());
      Artifact artifact;
      boolean isSingleEdit = false;

      if (singleEditMatcher.matches()) {
         singleGuid = singleEditMatcher.group(1);
         artifact = ArtifactQuery.getArtifactFromId(singleGuid, branch);

         if (artifact.isAttributeTypeValid(CoreAttributeTypes.WHOLE_WORD_CONTENT.getName()) || artifact.isAttributeTypeValid(CoreAttributeTypes.WORD_TEMPLATE_CONTENT.getName())) {
            isSingleEdit = true;
            wordArtifactUpdate(isSingleEdit, branch);
         } else if (artifact.isAttributeTypeValid(CoreAttributeTypes.NATIVE_CONTENT.getName())) {
            updateNativeArtifact(artifact);
         } else {
            throw new OseeArgumentException("Artifact must be of type WordArtifact or NativeArtifact.");
         }
      } else if (multiEditMatcher.matches()) {
         isSingleEdit = false;
         wordArtifactUpdate(isSingleEdit, branch);
      } else {
         throw new OseeArgumentException("File name did not contain the artifact guid");
      }
   }

   private void logUpdateSkip(Artifact artifact) {
      OseeLog.log(SkynetGuiPlugin.class, Level.INFO, String.format("Skipping update - artifact [%s] is read-only",
            artifact.toString()));
   }

   private void updateNativeArtifact(Artifact artifact) throws OseeCoreException, FileNotFoundException {
      if (!artifact.isReadOnly()) {
         InputStream stream = null;
         try {
            stream = new BufferedInputStream(new FileInputStream(workingFile));
            artifact.setSoleAttributeFromStream(CoreAttributeTypes.NATIVE_CONTENT.getName(), stream);
            artifact.persist();
         } finally {
            Lib.close(stream);
         }
      } else {
         logUpdateSkip(artifact);
      }
   }

   private void wordArtifactUpdate(boolean isSingle, Branch branch) throws OseeCoreException, DOMException, ParserConfigurationException, SAXException, IOException {
      List<String> deletedGuids = new LinkedList<String>();
      InputStream inputStream = new BufferedInputStream(new FileInputStream(workingFile));
      Document document;
      try {
         document = Jaxp.readXmlDocument(inputStream);
      } finally {
         Lib.close(inputStream);
      }

      WordArtifactElementExtractor extractor = new WordArtifactElementExtractor(document);
      Collection<Element> artElements = extractor.extract(isSingle);
      oleDataElement = extractor.getOleDataElement();

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
               containsOleData = !artifact.getSoleAttributeValue(WordAttribute.OLE_DATA_NAME, "").equals("");

               if (oleDataElement == null && containsOleData) {
                  artifact.setSoleAttributeValue(WordAttribute.OLE_DATA_NAME, "");
               } else if (oleDataElement != null && singleArtifact) {
                  artifact.setSoleAttributeFromStream(WordAttribute.OLE_DATA_NAME, new ByteArrayInputStream(
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
                           artifact.getSoleAttributeValue(WordAttribute.WORD_TEMPLATE_CONTENT).toString()).equals(
                           WordUtil.textOnly(content));

               if (singleArtifact || multiSave) {
                  // TODO
                  if (DEBUG) {
                     System.err.println("Initial: " + content);
                  }
                  if (artElement.getNodeName().endsWith("body")) {
                     // This code pulls out all of the stuff after the inserted listnum reordering
                     // stuff. This needs to be
                     // here so that we remove unwanted template information from single editing
                     content = content.replace(WordMLProducer.LISTNUM_FIELD_HEAD, "");
                     if (DEBUG) {
                        System.err.println("AFTER:  " + content);
                     }
                  }
                  LinkType linkType = LinkType.OSEE_SERVER_LINK;
                  content = WordMlLinkHandler.unlink(linkType, artifact, content);
                  artifact.setSoleAttributeValue(WordAttribute.WORD_TEMPLATE_CONTENT, content);
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

   private void updateWholeDocumentArtifact(Artifact artifact) throws FileNotFoundException, OseeCoreException {
      if (!artifact.isReadOnly()) {
         String content = null;
         InputStream inputStream = null;
         try {
            inputStream = new BufferedInputStream(new FileInputStream(workingFile));
            content = Lib.inputStreamToString(inputStream);
         } catch (IOException ex) {
            OseeExceptions.wrapAndThrow(ex);
         } finally {
            Lib.close(inputStream);
         }
         LinkType linkType = LinkType.OSEE_SERVER_LINK;
         content = WordMlLinkHandler.unlink(linkType, artifact, content);
         artifact.setSoleAttributeFromString(WordAttribute.WHOLE_WORD_CONTENT, content);
         artifact.persist();
      } else {
         logUpdateSkip(artifact);
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