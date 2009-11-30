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

import java.io.ByteArrayInputStream;
import java.io.File;
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
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
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
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
         FileInputStream myFileInputStream = new FileInputStream(workingFile);

         String guid = WordUtil.getGUIDFromFileInputStream(myFileInputStream);
         if (guid == null) {
            processNonWholeDocumentUpdates(branch);
         } else {
            Artifact myArtifact = ArtifactQuery.getArtifactFromId(guid, branch);
            updateWholeDocumentArtifact(myArtifact);
         }
      }
   }

   private void processNonWholeDocumentUpdates(Branch branch) throws OseeCoreException, ParserConfigurationException, SAXException, IOException {
      Artifact artifact;

      Matcher singleEditMatcher = guidPattern.matcher(workingFile.getName());
      Matcher multiEditMatcher = multiPattern.matcher(workingFile.getName());

      if (singleEditMatcher.matches()) {
         singleGuid = singleEditMatcher.group(1);
         artifact = ArtifactQuery.getArtifactFromId(singleGuid, branch);

         if (artifact.isAttributeTypeValid(CoreAttributeTypes.WHOLE_WORD_CONTENT.getName()) || artifact.isAttributeTypeValid(CoreAttributeTypes.WORD_TEMPLATE_CONTENT.getName())) {
            wordArtifactUpdate(getArtifacts(workingFile, true), branch);
         } else if (artifact.isAttributeTypeValid(CoreAttributeTypes.NATIVE_CONTENT.getName())) {
            updateNativeArtifact(artifact);
         } else {
            throw new OseeArgumentException("Artifact must be of type WordArtifact or NativeArtifact.");
         }
      } else if (multiEditMatcher.matches()) {
         wordArtifactUpdate(getArtifacts(workingFile, false), branch);
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
         artifact.setSoleAttributeFromStream(CoreAttributeTypes.NATIVE_CONTENT.getName(), new FileInputStream(workingFile));
         artifact.persist();
      } else {
         logUpdateSkip(artifact);
      }
   }

   private void wordArtifactUpdate(Collection<Element> artElements, Branch branch) throws OseeCoreException {
      List<String> deletedGuids = new LinkedList<String>();
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

               String content;
               try {
                  content =
                        Lib.inputStreamToString(new ByteArrayInputStream(
                              WordTemplateRenderer.getFormattedContent(artElement)));
               } catch (IOException ex) {
                  throw new OseeWrappedException(ex);
               }

               StringBuilder stringBuffer = new StringBuilder();

               // Decided not to support multi edit of artifacts that
               // contain equations.
               if (false && containsOleData && !singleArtifact) {
                  int startIndex = 0, endIndex = 0, tagCursorStart = 0, tagCursorEnd = 0;
                  String equationTag;

                  content = content.replaceAll("MyObject", "OLEObject").replaceAll("number", "ObjectID");

                  while (endIndex < content.length()) {
                     tagCursorStart = content.indexOf(":OLEObject ", startIndex);

                     if (tagCursorStart != -1) {
                        tagCursorStart = content.lastIndexOf('<', tagCursorStart);

                        tagCursorEnd = content.indexOf(">", tagCursorStart) + 1;
                        equationTag = content.substring(tagCursorStart, tagCursorEnd);

                        tagCursorEnd = content.indexOf("OLEObject>", tagCursorStart) + "OLEObject>".length();
                        content = content.replace(content.subSequence(tagCursorStart, tagCursorEnd), "");

                        endIndex = content.indexOf("</w:pict>", startIndex);
                        stringBuffer.append(content.substring(startIndex, endIndex));

                        equationTag = equationTag.replaceFirst("ns\\d+", "o").replace(">", "/>");
                        stringBuffer.append(equationTag + "</w:pict>");
                        startIndex = endIndex + "</w:pict>".length();
                     } else {
                        endIndex = content.length();
                        stringBuffer.append(content.substring(startIndex, endIndex));
                     }
                  }
                  content = stringBuffer.toString();
               }
               // Only update if editing a single artifact or if in
               // multi-edit mode only update if the artifact has at least one textual change (if the MUTI_EDIT_SAVE_ALL_CHANGES preference is not set).
               boolean multiSave =
                     UserManager.getUser().getBooleanSetting(MsWordPreferencePage.MUTI_EDIT_SAVE_ALL_CHANGES) || !WordUtil.textOnly(
                           artifact.getSoleAttributeValue(WordAttribute.WORD_TEMPLATE_CONTENT).toString()).equals(
                           WordUtil.textOnly(content));

               if (singleArtifact || multiSave) {
                  //TODO    
                  if (DEBUG) {
                     System.err.println("Initial: " + content);
                  }
                  if (artElement.getNodeName().endsWith("body")) {
                     //This code pulls out all of the stuff after the inserted listnum reordering stuff.  This needs to be
                     //here so that we remove unwanted template information from single editing
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
      InputStream inputStream = null;
      try {
         if (!artifact.isReadOnly()) {
            inputStream = new FileInputStream(workingFile);
            String content = Lib.inputStreamToString(inputStream);
            LinkType linkType = LinkType.OSEE_SERVER_LINK;
            content = WordMlLinkHandler.unlink(linkType, artifact, content);
            artifact.setSoleAttributeFromString(WordAttribute.WHOLE_WORD_CONTENT, content);
            artifact.persist();
         } else {
            logUpdateSkip(artifact);
         }
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      }
   }

   private Collection<Element> getArtifacts(File wordFile, boolean single) throws ParserConfigurationException, SAXException, IOException, OseeCoreException {
      final Collection<Element> artifacts = new LinkedList<Element>();
      final String elementNameForWordAttribute =
            WordTemplateProcessor.elementNameFor(WordAttribute.WORD_TEMPLATE_CONTENT);

      Document doc = Jaxp.readXmlDocument(wordFile);
      Element paragraphRoot = null;
      Collection<Element> sectList = new LinkedList<Element>();
      Element rootElement = doc.getDocumentElement();
      Element body = null;
      boolean containsTag = false;
      oleDataElement = null;

      NodeList nodeList = rootElement.getElementsByTagName("*");
      for (int i = 0; i < nodeList.getLength(); i++) {
         Element element = (Element) nodeList.item(i);
         if (element.getNodeName().endsWith(elementNameForWordAttribute)) {
            artifacts.add(element);
            containsTag = true;
         }
         if (element.getNodeName().endsWith("wx:sect")) {
            paragraphRoot = element;
            //handle the case where there exists two wx:sext elements
            if (element != null) {
               sectList.add(element);
            }
         }
         if (element.getNodeName().endsWith("body") && single) {
            artifacts.add(element);
            body = element;
         } else if (oleDataElement == null && element.getNodeName().endsWith("docOleData")) {
            oleDataElement = element;
         }
      }
      //When creating a three way merge the tags are not added as they create conflicts.  Therefore
      //we remove template information using the listnum fldChar tag.  The following code checks for the 
      //attribute tags and if they are not there removes all the paragraphs following the one that contains the 
      //fldChar
      if (containsTag) {
         artifacts.remove(body);
      } else if (!sectList.isEmpty()) {
         handleMultiSectTags(sectList);
      }
      return artifacts;
   }

   private void handleMultiSectTags(Collection<Element> sectList) throws OseeCoreException {
      boolean containTag = false;
      // need to check all wx:sect for the listnum tag
      for (Element sectElem : sectList) {
         containTag |= cleanUpParagraph(sectElem);
      }
      if (!containTag) {
         throw new OseeCoreException("This document does not contain the approporate tags to be correctly saved.");
      }
   }

   //To handle the case of sub-sections
   private boolean cleanUpParagraph(Node rootNode) throws OseeCoreException {
      boolean worked = false;
      boolean delete = false;
      Node node = rootNode.getFirstChild();
      while (node != null) {
         Node nextNode = node.getNextSibling();
         if (node.getNodeName().endsWith("sub-section")) {
            worked = cleanUpParagraph(node);
         } else {
            String content = node.getTextContent();
            if (DEBUG) {
               System.out.println(" " + node.getNodeName());
               System.out.println("    " + content);
            }
            if (content != null && content.contains("LISTNUM\"listreset\"")) {
               delete = true;
            }
            if (delete) {
               rootNode.removeChild(node);
            }
         }
         node = nextNode;
      }
      return worked || delete;
   }

   private String getGuid(Element artifactElement) throws OseeArgumentException {
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