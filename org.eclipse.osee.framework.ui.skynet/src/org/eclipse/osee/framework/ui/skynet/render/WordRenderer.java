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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.io.streams.StreamCatcher;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.template.TemplateLocator;
import org.eclipse.osee.framework.skynet.core.word.WordConverter;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor;
import org.eclipse.swt.program.Program;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Renders WordML content.
 * 
 * @author Jeff C. Phillips
 */
public class WordRenderer extends FileRenderer {
   public static final String DEFAULT_SET_NAME = "Default";
   public static final String ARTIFACT_NAME = "Word Renderer";
   public static final String TEMPLATE_ATTRIBUTE = "Word Template";
   public static final String ARTIFACT_SCHEMA = "http://eclipse.org/artifact.xsd";
   private static final String EMBEDDED_OBJECT_NO = "w:embeddedObjPresent=\"no\"";
   private static final String EMBEDDED_OBJECT_YES = "w:embeddedObjPresent=\"yes\"";
   private static final String STYLES_END = "</w:styles>";
   private static final String OLE_START = "<w:docOleData>";
   private static final String OLE_END = "</w:docOleData>";
   private static final OseeUiActivator plugin = SkynetGuiPlugin.getInstance();
   private static final QName fo = new QName("ns0", "unused_localname", ARTIFACT_SCHEMA);
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(WordRenderer.class);

   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final Pattern pattern =
         Pattern.compile("<v:imagedata[^>]*src=\"wordml://(\\d+\\.\\w+)\"[^>]*>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   // We need MS Word, so look for the program that is for .doc files
   private static final Program wordApp = Program.findProgram("doc");
   private WordTemplateProcessor templateProcessor;
   private WordTemplateProvider templateProvider;

   public WordRenderer() throws TransformerConfigurationException, IOException, TransformerFactoryConfigurationError {
      this.templateProcessor = new WordTemplateProcessor();
      this.templateProvider = WordTemplateProvider.getInstance();
   }

   /**
    * Creates a difference report for each artifact between baseArtifact and newerArtifact. Then produces a single
    * report by combining each of the difference reports together for a single report.
    */
   public void compareArtifacts(final List<Artifact> baseArtifacts, final List<Artifact> newerArtifact, final String option, IProgressMonitor monitor, final Branch branch) throws CoreException, Exception {
      if (branch == null) {
         throw new IllegalArgumentException("Branch can not be null");
      }

      if ((baseArtifacts == null && baseArtifacts.isEmpty()) || (newerArtifact == null && newerArtifact.isEmpty())) {
         throw new IllegalArgumentException("Artifact lists can not empty.");
      }

      if (baseArtifacts.size() != newerArtifact.size()) {
         throw new IllegalArgumentException(
               "base artifacts size: " + baseArtifacts.size() + " must match newer artifacts size: " + newerArtifact.size() + ".");
      }

      Jobs.startJob(new Job("Word Change Report") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               monitor.beginTask("Word Change Report ", newerArtifact.size());

               ArrayList<String> fileNames = new ArrayList<String>(newerArtifact.size());
               IFolder baseFolder = getRenderFolder(branch, PresentationType.DIFF);
               IFolder changeReportFolder = OseeData.getFolder(".diff/" + GUID.generateGuidStr());
               String baseFileStr = null;
               String fileName = null;

               for (int i = 0; i < newerArtifact.size(); i++) {
                  IFile baseFile =
                        renderToFile(baseFolder, getFilenameFromArtifact(null, PresentationType.DIFF), branch,
                              getRenderInputStream(monitor, baseArtifacts.get(i), option, PresentationType.DIFF),
                              PresentationType.DIFF);
                  IFile newerFile =
                        renderToFile(baseFolder, getFilenameFromArtifact(null, PresentationType.DIFF), branch,
                              getRenderInputStream(monitor, newerArtifact.get(i), option, PresentationType.DIFF),
                              PresentationType.DIFF);

                  baseFileStr = changeReportFolder.getLocation().toOSString();
                  fileName = baseFileStr + "/" + GUID.generateGuidStr() + ".xml";
                  fileNames.add(fileName);

                  monitor.setTaskName("Processing diff for: " + (newerArtifact.get(i) == null ? baseArtifacts.get(i).getDescriptiveName() : newerArtifact.get(
                        i).getDescriptiveName()));
                  monitor.worked(1);

                  // support the cancel feature
                  if (monitor.isCanceled()) {
                     monitor.done();
                     return Status.CANCEL_STATUS;
                  }
                  compare(baseFile, newerFile, fileName, false);
               }
               createAggregateArtifactDiffReport(fileNames, baseFileStr, null, monitor);
            } catch (Exception ex) {
               return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, Status.OK, ex.getLocalizedMessage(), ex);
            }
            return Status.OK_STATUS;
         }
      });
   }

   private void createAggregateArtifactDiffReport(ArrayList<String> fileNames, String baseFileStr, Artifact artifact, IProgressMonitor monitor) {
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

      String diffFile = baseFileStr + "/" + GUID.generateGuidStr() + "_diff.xml";
      AFile.writeFile(diffFile, file);

      monitor.done();
      getAssociatedProgram(artifact).execute(diffFile);
   }

   @Override
   public void compare(Artifact baseVersion, Artifact newerVersion, String option, IProgressMonitor monitor) throws Exception {
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

      if (baseVersion != null) {
         String baseFileStr = baseFile.getLocation().toOSString();
         diffPath =
               baseFileStr.substring(0, baseFileStr.lastIndexOf(')')) + " to " + (newerVersion != null ? newerVersion.getPersistenceMemo().getTransactionNumber() : " deleted") + baseFileStr.substring(baseFileStr.lastIndexOf(')'));
      } else {
         String baseFileStr = newerFile.getLocation().toOSString();
         diffPath =
               baseFileStr.substring(0, baseFileStr.lastIndexOf('(') + 1) + "new " + baseFileStr.substring(baseFileStr.lastIndexOf('(') + 1);
      }

      compare(baseFile, newerFile, diffPath, true);
   }

   private void compare(IFile baseFile, IFile newerFile, String diffPath, boolean visible) throws IOException, InterruptedException {
      File vbDiffScript =
            visible ? plugin.getPluginFile("support/compareDocs.vbs") : plugin.getPluginFile("support/notVisiblecompareDocs.vbs");

      // quotes are neccessary because of Runtime.exec wraps the last element in quotes...crazy
      String cmd[] =
            {
                  "cmd",
                  "/s /c",
                  "\"" + vbDiffScript.getPath() + "\"",
                  "/author:CoolOseeUser\" /diffPath:\"" + diffPath + "\" /detectFormatChanges:true /ver1:\"" + baseFile.getLocation().toOSString() + "\" /ver2:\"" + newerFile.getLocation().toOSString()};

      Process proc = Runtime.getRuntime().exec(cmd);

      StreamCatcher errorCatcher = new StreamCatcher(proc.getErrorStream(), "ERROR", logger);
      StreamCatcher outputCatcher = new StreamCatcher(proc.getInputStream(), "OUTPUT");

      errorCatcher.start();
      outputCatcher.start();
      proc.waitFor();
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
      if (artifact instanceof WordArtifact) {
         return ARTIFACT_TYPE_MATCH;
      }
      return NO_MATCH;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getAssociatedExtension()
    */
   @Override
   public String getAssociatedExtension(Artifact artifact) {
      return "xml";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getAssociatedProgram()
    */
   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      return wordApp;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#getEditOptions()
    */
   @Override
   public List<String> getEditOptions() throws Exception {
      return getTemplateOptions();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#getPreviewOptions()
    */
   @Override
   public List<String> getPreviewOptions() throws Exception {
      return getTemplateOptions();
   }

   private List<String> getTemplateOptions() throws Exception {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor,
    *      org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String,
    *      org.eclipse.osee.framework.ui.skynet.render.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(IProgressMonitor monitor, Artifact artifact, String option, PresentationType presentationType) throws Exception {
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>(1);
      if (artifact != null) {
         artifacts.add(artifact);
      }
      return getRenderInputStream(monitor, artifacts, option, presentationType);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor,
    *      org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String,
    *      org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(IProgressMonitor monitor, List<Artifact> artifacts, String option, PresentationType presentationType) throws Exception {
      final List<Artifact> notMultiEditableArtifacts = new LinkedList<Artifact>();
      final BlamVariableMap variableMap = new BlamVariableMap();
      String template;

      if (artifacts.isEmpty()) {
         //  Still need to get a default template with a null artifact list
         template = getTemplate(branchManager.getCommonBranch(), null, presentationType, option);
      } else {
         boolean isSingleEdit = artifacts.size() == 1;
         Artifact firstArtifact = artifacts.iterator().next();
         template = getTemplate(firstArtifact.getBranch(), firstArtifact, presentationType, option);

         if (isSingleEdit) {
            if (!firstArtifact.getSoleAttributeValue(WordAttribute.OLE_DATA_NAME).equals("")) {
               template = template.replaceAll(EMBEDDED_OBJECT_NO, EMBEDDED_OBJECT_YES);
               template =
                     template.replaceAll(
                           STYLES_END,
                           STYLES_END + OLE_START + firstArtifact.getSoleAttributeValue(WordAttribute.OLE_DATA_NAME) + OLE_END);
            }
         } else {
            for (Artifact artifact : artifacts) {
               if (!artifact.getSoleAttributeValue(WordAttribute.OLE_DATA_NAME).equals("") && presentationType == PresentationType.EDIT) {
                  notMultiEditableArtifacts.add(artifact);
               }
            }
            displayNotMultiEditArtifacts(notMultiEditableArtifacts);
         }

         artifacts.removeAll(notMultiEditableArtifacts);
      }

      variableMap.setValue(DEFAULT_SET_NAME, artifacts);
      return templateProcessor.applyTemplate(variableMap, template, null);
   }

   public void addTemplate(PresentationType presentationType, String bundleName, String templateName, String templatePath, Branch branch) throws Exception {
      TemplateLocator templateLocation = new TemplateLocator(bundleName, templateName, templatePath);
      templateProvider.addTemplate(getId(), branch, presentationType.name(), templateLocation);
   }

   public void setDefaultTemplates(Artifact document, PresentationType presentationType, Branch branch) throws Exception {
      templateProvider.setDefaultTemplates(getId(), document, presentationType.name(), branch);
   }

   private String getTemplate(Branch branch, Artifact artifact, PresentationType presentationType, String option) throws Exception {
      return templateProvider.getTemplate(getId(), branch, artifact, presentationType.name(), option);
   }
}