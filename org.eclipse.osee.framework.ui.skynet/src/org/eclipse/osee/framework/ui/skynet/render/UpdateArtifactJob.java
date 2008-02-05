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
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.VisitorEvent;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class UpdateArtifactJob extends UpdateJob {
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final ArtifactPersistenceManager persistenceManager = ArtifactPersistenceManager.getInstance();
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(UpdateArtifactJob.class);
   private static final Pattern guidPattern = Pattern.compile(".*\\(([^)]+)\\)[^()]*");
   private static final Pattern multiPattern = Pattern.compile(".*[^()]*");
   private Element oleDataElement;

   public UpdateArtifactJob() {
      super("Update Artifact");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      try {
         processUpdate();
      } catch (Exception ex) {
         return new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, Status.OK, ex.getLocalizedMessage(), ex);
      }
      return Status.OK_STATUS;
   }

   private void processUpdate() throws Exception {
      int branchId = Branch.getBranchIdFromBranchFolderName(workingFile.getParentFile().getName());
      Branch branch = BranchPersistenceManager.getInstance().getBranch(branchId);
      FileInputStream myFileInputStream = new FileInputStream(workingFile);
      String guid = WordUtil.getGUIDFromFileInputStream(myFileInputStream);
      if (guid == null) {
         processNonWholeDocumentUpdates(branch);
      } else {
         Artifact myArtifact = persistenceManager.getArtifact(guid, branch);
         updateWholeDocumentArtifact(myArtifact);
      }
   }

   private void processNonWholeDocumentUpdates(Branch myBranch) throws Exception {
      String guid;
      Artifact artifact;

      Matcher singleEditMatcher = guidPattern.matcher(workingFile.getName());
      Matcher multiEditMatcher = multiPattern.matcher(workingFile.getName());

      if (singleEditMatcher.matches()) {
         guid = singleEditMatcher.group(1);
         artifact = persistenceManager.getArtifact(guid, myBranch);

         if (artifact instanceof WordArtifact) {
            updateWordArtifact(myBranch);
         } else if (artifact instanceof NativeArtifact) {
            updateNativeArtifact(artifact);
         } else {
            throw new IllegalArgumentException("Artifact must be of type WordArtifact or NativeArtifact.");
         }
      } else if (multiEditMatcher.matches()) {
         updateWordArtifact(myBranch);
      } else {
         throw new IllegalArgumentException("File name did not contain the artifact guid");
      }
   }

   private void updateWordArtifact(Branch branch) throws SQLException, IOException, ParserConfigurationException, SAXException {
      try {
         new WordArtifactUpdateTx(branch, getArtifacts(workingFile)).execute();
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }

   }

   private void updateNativeArtifact(Artifact artifact) throws IllegalStateException, FileNotFoundException, SQLException {
      artifact.setAttribute(NativeArtifact.CONTENT_NAME, new FileInputStream(workingFile));

      artifact.persistAttributes();
      eventManager.kick(new VisitorEvent(artifact, this));
   }

   private void updateWholeDocumentArtifact(Artifact artifact) throws IllegalStateException, FileNotFoundException, SQLException {
      artifact.setAttribute(WordAttribute.CONTENT_NAME, new FileInputStream(workingFile));

      artifact.persistAttributes();
      eventManager.kick(new VisitorEvent(artifact, this));
   }

   @SuppressWarnings( {"unchecked", "serial"})
   private Collection<Element> getArtifacts(File wordFile) throws ParserConfigurationException, SAXException, IOException {
      final Collection<Element> artifacts = new LinkedList<Element>();
      final String elementNameForWordAttribute = WordTemplateProcessor.elementNameFor(WordAttribute.CONTENT_NAME);

      Document doc = Jaxp.readXmlDocument(wordFile);
      Element rootElement = doc.getDocumentElement();

      oleDataElement = null;

      NodeList nodeList = rootElement.getElementsByTagName("*");
      for (int i = 0; i < nodeList.getLength(); i++) {
         Element element = (Element) nodeList.item(i);

         if (element.getNodeName().endsWith(elementNameForWordAttribute)) {
            artifacts.add(element);
         } else if (oleDataElement == null && element.getNodeName().endsWith("docOleData")) {
            oleDataElement = element;
         }
      }

      return artifacts;
   }

   private final class WordArtifactUpdateTx extends AbstractSkynetTxTemplate {
      private List<String> deletedGuids;
      private Collection<Element> artElements;
      private Set<Artifact> changedArtifacts;

      public WordArtifactUpdateTx(Branch branch, Collection<Element> artElements) {
         super(branch);
         this.artElements = artElements;
         this.deletedGuids = new LinkedList<String>();
         this.changedArtifacts = new HashSet<Artifact>();
      }

      @Override
      protected void handleTxWork() throws Exception {
         boolean singleArtifact = artElements.size() == 1;
         boolean containsOleData = false;
         for (Element artElement : artElements) {
            String guid = getGuid(artElement);
            Artifact artifact = persistenceManager.getArtifact(guid, getTxBranch());

            if (artifact != null) {
               containsOleData = !artifact.getSoleAttributeValue(WordAttribute.OLE_DATA_NAME).equals("");

               if (oleDataElement == null && containsOleData) {
                  artifact.setSoleAttributeValue(WordAttribute.OLE_DATA_NAME, "");
               } else if (oleDataElement != null && singleArtifact) {
                  artifact.setAttribute(WordAttribute.OLE_DATA_NAME, new ByteArrayInputStream(
                        WordRenderer.getFormattedContent(oleDataElement)));
               }

               String content =
                     Lib.inputStreamToString(new ByteArrayInputStream(WordRenderer.getFormattedContent(artElement)));
               StringBuilder stringBuffer = new StringBuilder();

               // Decided not to support multi edit of artifacts that contain equations.
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

               artifact.setSoleAttributeValue(WordAttribute.CONTENT_NAME, content);
               if (artifact.isDirty()) {
                  artifact.persistAttributes();
                  changedArtifacts.add(artifact);
               }
            } else {
               deletedGuids.add(guid);
            }
         }
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate#handleTxFinally()
       */
      @Override
      protected void handleTxFinally() throws Exception {
         super.handleTxFinally();
         for (Artifact artifact : changedArtifacts) {
            eventManager.kick(new VisitorEvent(artifact, this));
         }
         if (!deletedGuids.isEmpty()) {
            throw new IllegalArgumentException(
                  "The following deleted artifacts could not be saved: " + Collections.toString(",", deletedGuids));
         }
      }

      private String getGuid(Element artifactElement) {
         NamedNodeMap attributes = artifactElement.getAttributes();
         for (int i = 0; i < attributes.getLength(); i++) {
            // MS Word has a nasty habit of changing the namespace say from ns0 to ns1, so we must
            // ignore the namespace by using endsWith()
            if (attributes.item(i).getNodeName().endsWith("guid")) {
               return attributes.item(i).getNodeValue();
            }
         }
         throw new IllegalArgumentException("didn't find the guid attribure in element: " + artifactElement);
      }
   }
}