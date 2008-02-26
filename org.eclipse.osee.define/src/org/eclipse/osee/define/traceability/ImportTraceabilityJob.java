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
package org.eclipse.osee.define.traceability;

import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class ImportTraceabilityJob extends Job {
   private static final Pattern ofpReqTraceP = Pattern.compile("\\^SRS\\s*([^;\n\r]+);");
   private final Matcher ofpReqTraceMatcher;
   private static final Pattern scriptReqTraceP =
         Pattern.compile("addTraceability\\s*\\(\\\"(?:SubDD|SRS|CSID)?\\s*([^\\\"]+)\\\"");
   private final Matcher scriptReqTraceMatcher;
   private static final Pattern structuredReqNameP = Pattern.compile("\\[?(\\{[^\\}]+\\})(.*)");
   private static final Pattern filePattern = Pattern.compile(".*\\.(java|ada|ads|adb|c|h)");
   private static final Pattern embeddedVolumePattern = Pattern.compile("\\{\\d+ (.*)\\}[ .]*");
   private static final Pattern invalidTraceMarkPattern = Pattern.compile("(\\[[A-Za-z]|USES_).*");
   private static final Pattern nonWordPattern = Pattern.compile("[^A-Z0-9]");

   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private final File file;
   private final Branch branch;
   private final ArrayList<String> noTraceabilityFiles;
   private final HashMap<String, Artifact> softwareReqs;
   private final HashMap<String, Artifact> indirectReqs;
   private final CountingMap<Artifact> reqsTraceCounts;
   private final HashCollection<Artifact, String> requirementToCodeUnitsMap;
   private final HashSet<String> codeUnits;
   private final CharBackedInputStream charBak;
   private final ISheetWriter excelWriter;
   private int pathPrefixLength;
   private Collection<Artifact> softwareReqList;
   private Collection<Artifact> indirectSoftwareReqList;
   private boolean writeOutResults;

   public ImportTraceabilityJob(File file, Branch branch, boolean writeOutResults) throws IllegalArgumentException, CoreException, SQLException, IOException {
      super("Importing Traceability");
      this.file = file;
      this.branch = branch;
      noTraceabilityFiles = new ArrayList<String>(200);
      softwareReqs = new HashMap<String, Artifact>(3500);
      indirectReqs = new HashMap<String, Artifact>(700);
      reqsTraceCounts = new CountingMap<Artifact>();
      codeUnits = new HashSet<String>();
      requirementToCodeUnitsMap = new HashCollection<Artifact, String>(false, LinkedList.class);
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
      ofpReqTraceMatcher = ofpReqTraceP.matcher("");
      scriptReqTraceMatcher = scriptReqTraceP.matcher("");
      this.writeOutResults = writeOutResults;
   }

   private void getReqs(String artifactTypeName, Collection<Artifact> requirementsList, HashMap<String, Artifact> artifactMap, IProgressMonitor monitor) throws SQLException {
      monitor.subTask("Aquiring " + artifactTypeName + "s"); // bulk load for performance reasons
      if (requirementsList == null) {
         requirementsList = artifactManager.getArtifactsFromSubtypeName(artifactTypeName, branch);
      }

      for (Artifact artifact : requirementsList) {
         artifactMap.put(getCanonicalReqName(artifact.getDescriptiveName()), artifact);
      }
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   public IStatus run(IProgressMonitor monitor) {
      try {
         monitor.beginTask("Importing From " + file.getName(), 100);
         monitor.worked(1);

         getReqs("Software Requirement", softwareReqList, softwareReqs, monitor);
         monitor.worked(30);

         getReqs("Indirect Software Requirement", indirectSoftwareReqList, indirectReqs, monitor);
         monitor.worked(7);

         if (writeOutResults) {
            excelWriter.startSheet("srs <--> code units", 6);
            excelWriter.writeRow("Req in DB", "Code Unit", "Requirement Name", "Requirement Trace Mark in Code");
         }

         if (file.isFile()) {
            for (String path : Lib.readListFromFile(file, true)) {
               monitor.subTask(path);
               handleDirectory(new File(path));
            }
         } else if (file.isDirectory()) {
            handleDirectory(file);
         } else {
            throw new IllegalStateException("unexpected file system type");
         }

         if (writeOutResults) {
            excelWriter.endSheet();

            writeNoTraceFilesSheet();
            writeTraceCountsSheet();

            excelWriter.endWorkbook();
            IFile iFile = OseeData.getIFile("CodeUnit_To_SRS_Trace.xml");
            AIFile.writeToFile(iFile, charBak);
            Program.launch(iFile.getLocation().toOSString());
         }

         monitor.done();
         return Status.OK_STATUS;
      } catch (Exception ex) {
         return new Status(Status.ERROR, DefinePlugin.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
      }
   }

   private String getCanonicalReqName(String reqReference) {
      String canonicalReqReference = reqReference.toUpperCase();

      Matcher embeddedVolumeMatcher = embeddedVolumePattern.matcher(canonicalReqReference);
      if (embeddedVolumeMatcher.find()) {
         canonicalReqReference = embeddedVolumeMatcher.group(1);
      }

      canonicalReqReference = nonWordPattern.matcher(canonicalReqReference).replaceAll("");

      return canonicalReqReference;
   }

   private void handleDirectory(File directory) throws IOException, SQLException {
      if (directory == null || directory.getParentFile() == null) {
         OSEELog.logWarning(DefinePlugin.class, "The path " + directory + " is invalid.", true);
         return;
      }

      pathPrefixLength = directory.getParentFile().getAbsolutePath().length();

      for (File sourceFile : Lib.recursivelyListFiles(directory, filePattern)) {
         CharBuffer buf = Lib.fileToCharBuffer(sourceFile);
         Matcher reqTraceMatcher = getReqTraceMatcher(sourceFile);
         reqTraceMatcher.reset(buf);

         int matchCount = 0;
         String relativePath = sourceFile.getPath().substring(pathPrefixLength);
         codeUnits.add(relativePath);
         while (reqTraceMatcher.find()) {
            handelReqTrace(relativePath, reqTraceMatcher.group(1));
            matchCount++;
         }
         if (matchCount == 0) {
            noTraceabilityFiles.add(relativePath);
         }
      }
   }

   private Matcher getReqTraceMatcher(File sourceFile) {
      if (sourceFile.getName().endsWith("java")) {
         return scriptReqTraceMatcher;
      }
      return ofpReqTraceMatcher;
   }

   private void writeNoTraceFilesSheet() throws IOException {
      excelWriter.startSheet("no match files", 1);
      for (String path : noTraceabilityFiles) {
         excelWriter.writeRow(path);
      }
      excelWriter.endSheet();
   }

   private void writeTraceCountsSheet() throws IOException, SQLException {
      excelWriter.startSheet("trace counts", 3);
      excelWriter.writeRow("SRS Requirement from Database", "Trace Count", "Partitions");
      excelWriter.writeRow("% requirement coverage", null, "=1-COUNTIF(C2,&quot;0&quot;)/COUNTA(C2)");

      for (Artifact artifact : softwareReqs.values()) {
         excelWriter.writeRow(artifact.getDescriptiveName(), String.valueOf(reqsTraceCounts.get(artifact)),
               Collections.toString(",", artifact.getAttributesToStringCollection("Partition")));
      }

      excelWriter.endSheet();
   }

   private void handelReqTrace(String path, String traceMark) throws SQLException, IOException {
      String foundStr;
      Artifact reqArtifact = null;

      Matcher invalidTraceMarkMatcher = invalidTraceMarkPattern.matcher(traceMark);
      if (invalidTraceMarkMatcher.matches()) {
         foundStr = "invalid trace mark";
      } else {
         reqArtifact = getRequirementArtifact(traceMark);
         if (reqArtifact == null) {
            Matcher structuredReqNameMatcher = structuredReqNameP.matcher(traceMark);
            if (structuredReqNameMatcher.matches()) {
               reqArtifact = getRequirementArtifact(structuredReqNameMatcher.group(1));

               if (reqArtifact == null) {
                  foundStr = "no match in DB";
               } else {
                  // for local data and procedures search requirement text for traceMark
                  // example local data [{SUBSCRIBER}.ID] and example procedure {CURSOR_ACKNOWLEDGE}.NORMAL
                  String textContent =
                        WordUtil.textOnly(reqArtifact.getSoleAttributeValue(WordAttribute.CONTENT_NAME)).toUpperCase();
                  if (textContent.contains(getCanonicalReqName(structuredReqNameMatcher.group(2)))) {
                     foundStr = "req body match";
                  } else {
                     foundStr = "paritial match";
                  }
               }
            } else {
               foundStr = "no match in DB";
            }
         } else {
            foundStr = fullMatch(reqArtifact);
         }
      }

      String name = null;
      if (reqArtifact != null) {
         name = reqArtifact.getDescriptiveName();
         requirementToCodeUnitsMap.put(reqArtifact, path);
      }

      if (writeOutResults) {
         excelWriter.writeRow(foundStr, path, name, traceMark);
      }
   }

   private Artifact getRequirementArtifact(String traceMark) {
      String canonicalTraceMark = getCanonicalReqName(traceMark);
      Artifact reqArtifact = softwareReqs.get(canonicalTraceMark);
      if (reqArtifact == null) {
         reqArtifact = indirectReqs.get(canonicalTraceMark);
      }
      return reqArtifact;
   }

   private String fullMatch(Artifact reqArtifact) {
      reqsTraceCounts.put(reqArtifact);
      return "full match";
   }

   public HashCollection<Artifact, String> getRequirementToCodeUnitsMap() {
      return requirementToCodeUnitsMap;
   }

   /**
    * @return the codeUnits
    */
   public HashSet<String> getCodeUnits() {
      return codeUnits;
   }

   /**
    * @param softwareReqList the softwareReqList to set
    */
   public void setSoftwareReqLists(Collection<Artifact> softwareReqList, Collection<Artifact> indirectSoftwareReqList) {
      this.softwareReqList = softwareReqList;
      this.indirectSoftwareReqList = indirectSoftwareReqList;
   }
}