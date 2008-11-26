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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor;
import org.eclipse.osee.framework.ui.skynet.templates.TemplateManager;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Renders WordML content.
 * 
 * @author Jeff C. Phillips
 */
public class WordTemplateRenderer extends WordRenderer implements ITemplateRenderer {
   private static final Pattern pattern =
         Pattern.compile("<v:imagedata[^>]*src=\"wordml://(\\d+\\.\\w+)\"[^>]*>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   public static final String WORD_RENDERER_EXTENSION = "org.eclipse.osee.framework.ui.skynet.word";
   public static final String DEFAULT_SET_NAME = "Default";
   public static final String ARTIFACT_NAME = "Word Renderer";
   public static final String TEMPLATE_ATTRIBUTE = "Word Template";
   public static final String ARTIFACT_SCHEMA = "http://eclipse.org/artifact.xsd";
   private static final String EMBEDDED_OBJECT_NO = "w:embeddedObjPresent=\"no\"";
   private static final String EMBEDDED_OBJECT_YES = "w:embeddedObjPresent=\"yes\"";
   private static final String STYLES_END = "</w:styles>";
   private static final String OLE_START = "<w:docOleData>";
   private static final String OLE_END = "</w:docOleData>";
   private static final QName fo = new QName("ns0", "unused_localname", ARTIFACT_SCHEMA);
   public static final String UPDATE_PARAGRAPH_NUMBER_OPTION = "updateParagraphNumber";

   private final WordTemplateProcessor templateProcessor = new WordTemplateProcessor(this);

   /**
    * @param rendererId
    */
   public WordTemplateRenderer(String rendererId) {
      super(rendererId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#newInstance()
    */
   @Override
   public WordTemplateRenderer newInstance() throws OseeCoreException {
      return new WordTemplateRenderer(getId());
   }

   public void publishSRS(VariableMap variableMap) throws OseeCoreException {
      templateProcessor.publishSRS(variableMap);
   }

   /**
    * Creates a difference report for each artifact between baseArtifact and newerArtifact. Then produces a single
    * report by combining each of the difference reports together for a single report.
    */
   @Override
   public void compareArtifacts(final List<Artifact> baseArtifacts, final List<Artifact> newerArtifact, IProgressMonitor monitor, final Branch branch, PresentationType presentationType) throws OseeCoreException {
      if (branch == null) {
         throw new OseeArgumentException("Branch can not be null");
      }

      if (baseArtifacts.size() != newerArtifact.size()) {
         throw new OseeArgumentException(
               "base artifacts size: " + baseArtifacts.size() + " must match newer artifacts size: " + newerArtifact.size() + ".");
      }

      Jobs.startJob(new Job("Word Change Report") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               String fileName = getStringOption("fileName");
               
               //if the file name is null we will give it a GUID
               if (fileName == null) {
                     fileName = GUID.generateGuidStr() + ".xml";
               }
               
               monitor.beginTask("Word Change Report ", newerArtifact.size() * 2);
               ArrayList<String> fileNames = new ArrayList<String>(newerArtifact.size());
               IFolder baseFolder = getRenderFolder(branch, PresentationType.DIFF);
               IFolder changeReportFolder = OseeData.getFolder(".diff/" + GUID.generateGuidStr());
               String baseFileStr = "c:/UserData";
               String localFileName = null;

               VbaWordDiffGenerator generator = new VbaWordDiffGenerator();
               generator.initialize(false, false);
               for (int i = 0; i < newerArtifact.size(); i++) {
                  IFile baseFile =
                        renderToFile(baseFolder, getFilenameFromArtifact(null, PresentationType.DIFF), branch,
                              getRenderInputStream(baseArtifacts.get(i), PresentationType.DIFF), PresentationType.DIFF);
                  IFile newerFile =
                        renderToFile(baseFolder, getFilenameFromArtifact(null, PresentationType.DIFF), branch,
                              getRenderInputStream(newerArtifact.get(i), PresentationType.DIFF), PresentationType.DIFF);

                  baseFileStr = changeReportFolder.getLocation().toOSString();
                  localFileName = baseFileStr + "/" + GUID.generateGuidStr() + ".xml";
                  fileNames.add(localFileName);

                  monitor.setTaskName("Adding to Diff Script: " + (newerArtifact.get(i) == null ? baseArtifacts.get(i).getDescriptiveName() : newerArtifact.get(
                        i).getDescriptiveName()));
                  monitor.worked(1);

                  // support the cancel feature
                  if (monitor.isCanceled()) {
                     monitor.done();
                     return Status.CANCEL_STATUS;
                  }
                  generator.addComparison(baseFile, newerFile, localFileName, false);
                  //compare(baseFile, newerFile, fileName, false, plugin.getPluginStoreFile("support/compareDocs3.vbs"));

               }
               monitor.setTaskName("Running Diff Script");
               generator.finish(baseFileStr + "/compareDocs.vbs");
               //if (fileNames.size() == 1) {
               //   getAssociatedProgram(null).execute(baseFileStr + fileName);
               // } else {
               createAggregateArtifactDiffReport(fileNames, baseFileStr, null, baseFileStr + "/" + fileName, monitor);
               // }
            } catch (OseeCoreException ex) {
               return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, Status.OK, ex.getLocalizedMessage(), ex);
            }
            return Status.OK_STATUS;
         }
      });
   }

   private void createAggregateArtifactDiffReport(ArrayList<String> fileNames, String baseFileStr, Artifact artifact, String fileName, IProgressMonitor monitor) throws OseeCoreException {
      monitor.setTaskName("Writing final document");
      ArrayList<String> datas = new ArrayList<String>(fileNames.size());
      int startIndex;
      int endIndex;

      for (String filename : fileNames) {
         String data = AFile.readFile(filename);
         startIndex = data.indexOf("<w:body>") + 8;
         endIndex = data.indexOf("</w:body>");

         data = data.substring(startIndex, endIndex);

         Matcher m = pattern.matcher(data);
         while (m.find()) {
            String name = m.group(1);
            data = data.replace(name, GUID.generateGuidStr() + name);
         }

         datas.add(data);
      }

      String firstFileName = fileNames.get(0);
      String file = AFile.readFile(firstFileName);
      datas.remove(0);
      file = file.replace("</w:body>", Collections.toString("", datas) + "</w:body>");

      if (!file.contains("xmlns:ns2=\"http")) {
         file = file.replaceAll("ns2", "ns1");
      }

      if (!file.contains("xmlns:ns1=\"http")) {
         file = file.replaceAll("ns1", "ns0");
      }
      if (fileName == null) {
         fileName = baseFileStr + "/" + GUID.generateGuidStr() + "_diff.xml";
      }
      AFile.writeFile(fileName, file);

      monitor.done();
      getAssociatedProgram(artifact).execute(fileName);
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IProgressMonitor monitor, PresentationType presentationType, boolean show) throws OseeCoreException {
      if (baseVersion == null && newerVersion == null) throw new OseeArgumentException(
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

      String fileName = getStringOption("fileName");
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
         diffPath =
               getRenderFolder(baseVersion.getBranch(), PresentationType.EDIT).getLocation().toOSString() + '\\' + fileName;
      }

      VbaWordDiffGenerator diffGenerator = new VbaWordDiffGenerator();
      diffGenerator.initialize(presentationType == PresentationType.DIFF,
            presentationType == PresentationType.MERGE_EDIT);

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

   /**
    * Displays a list of artifacts in the Artifact Explorer that could not be multi edited because they contained
    * artifacts that had an OLEData attribute.
    * 
    * @param artifacts
    */
   private void displayNotMultiEditArtifacts(final Collection<Artifact> artifacts) {
      if (!artifacts.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {

            public void run() {
               ArtifactExplorer.explore(artifacts);
            }
         });
      }
   }

   public static QName getFoNamespace() {
      return fo;
   }

   public static byte[] getFormattedContent(Element formattedItemElement) {
      ByteArrayOutputStream data = new ByteArrayOutputStream((int) Math.pow(2, 10));
      OutputFormat format = Jaxp.getCompactFormat(formattedItemElement.getOwnerDocument());
      format.setOmitDocumentType(true);
      format.setOmitXMLDeclaration(true);
      XMLSerializer serializer = new XMLSerializer(data, format);

      try {
         for (Element e : Jaxp.getChildDirects(formattedItemElement))
            serializer.serialize(e);
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      }

      return data.toByteArray();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#isValidFor(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if (artifact instanceof WordArtifact && !((WordArtifact) artifact).isWholeWordArtifact()) {
         return ARTIFACT_TYPE_MATCH;
      }
      return NO_MATCH;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor,
    *      org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String,
    *      org.eclipse.osee.framework.ui.skynet.render.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>(1);
      if (artifact != null) {
         artifacts.add(artifact);
      }
      return getRenderInputStream(artifacts, presentationType);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor,
    *      org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String,
    *      org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      final List<Artifact> notMultiEditableArtifacts = new LinkedList<Artifact>();
      String template;

      if (artifacts.isEmpty()) {
         //  Still need to get a default template with a null artifact list
         template = getTemplate(null, presentationType);
      } else {
         Artifact firstArtifact = artifacts.iterator().next();
         template = getTemplate(firstArtifact, presentationType);

         for (Artifact artifact : artifacts) {
            Attribute<?> attribute = artifact.getSoleAttribute(WordAttribute.WORD_TEMPLATE_CONTENT);
            if (attribute == null) {
               attribute =
                     artifact.createAttribute(AttributeTypeManager.getType(WordAttribute.WORD_TEMPLATE_CONTENT), true);
            }
            if (presentationType == PresentationType.DIFF && attribute != null && ((WordAttribute) attribute).mergeMarkupPresent()) {
               throw new OseeCoreException(
                     "Trying to diff the " + artifact.getDescriptiveName() + " artifact on the " + artifact.getBranch().getBranchShortName() + " branch, which has tracked changes turned on.  All tracked changes must be removed before the artifacts can be compared.");

            }
         }

         if (presentationType == PresentationType.EDIT && artifacts.size() > 1) {
            // currently we can't support the editing of multiple artifacts with OLE data
            for (Artifact artifact : artifacts) {
               if (!artifact.getSoleAttributeValue(WordAttribute.OLE_DATA_NAME, "").equals("") && presentationType == PresentationType.EDIT) {
                  notMultiEditableArtifacts.add(artifact);
               }
            }
            displayNotMultiEditArtifacts(notMultiEditableArtifacts);
            artifacts.removeAll(notMultiEditableArtifacts);
         } else { // support OLE data when appropriate
            if (!firstArtifact.getSoleAttributeValue(WordAttribute.OLE_DATA_NAME, "").equals("")) {
               template = template.replaceAll(EMBEDDED_OBJECT_NO, EMBEDDED_OBJECT_YES);
               template =
                     template.replaceAll(STYLES_END, STYLES_END + OLE_START + firstArtifact.getSoleAttributeValue(
                           WordAttribute.OLE_DATA_NAME, "") + OLE_END);
            }
         }
      }

      template = WordUtil.removeGUIDFromTemplate(template);
      return templateProcessor.applyTemplate(artifacts, template, null, presentationType);
   }

   protected String getTemplate(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      return TemplateManager.getTemplate(this, artifact, presentationType.name(), getStringOption(TEMPLATE_OPTION)).getSoleAttributeValue(
            WordAttribute.WHOLE_WORD_CONTENT);
   }
}