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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.define.traceability.data.RequirementData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class ImportTraceabilityOperation extends AbstractOperation {
   private static final Pattern filePattern = Pattern.compile(".*\\.(java|ada|ads|adb|c|h)");
   private static final TraceabilityExtractor traceExtractor = TraceabilityExtractor.getInstance();

   private final File file;
   private final RequirementData requirementData;
   private final ArrayList<String> noTraceabilityFiles;
   private final CountingMap<Artifact> reqsTraceCounts;
   private final HashCollection<Artifact, String> requirementToCodeUnitsMap;
   private final HashSet<String> codeUnits;
   private final CharBackedInputStream charBak;
   private final ISheetWriter excelWriter;
   private int pathPrefixLength;
   private final boolean writeOutResults;

   public ImportTraceabilityOperation(File file, Branch branch, boolean writeOutResults) throws IOException {
      super("Importing Traceability", DefinePlugin.PLUGIN_ID);
      this.file = file;
      this.requirementData = new RequirementData(branch);
      noTraceabilityFiles = new ArrayList<String>(200);
      reqsTraceCounts = new CountingMap<Artifact>();
      codeUnits = new HashSet<String>();
      requirementToCodeUnitsMap = new HashCollection<Artifact, String>(false, LinkedList.class);
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
      this.writeOutResults = writeOutResults;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      monitor.worked(1);

      requirementData.initialize(monitor);
      if (writeOutResults) {
         excelWriter.startSheet("srs <--> code units", 6);
         excelWriter.writeRow("Req in DB", "Code Unit", "Requirement Name", "Requirement Trace Mark in Code");
      }

      if (file.isFile()) {
         for (String path : Lib.readListFromFile(file, true)) {
            monitor.subTask(path);
            handleDirectory(new File(path));
            checkForCancelledStatus(monitor);
         }
      } else if (file.isDirectory()) {
         handleDirectory(file);
      } else {
         throw new OseeStateException("Invalid path [%s]", file.getCanonicalPath());
      }

      checkForCancelledStatus(monitor);
      if (writeOutResults) {
         excelWriter.endSheet();

         writeNoTraceFilesSheet();
         writeTraceCountsSheet();

         excelWriter.endWorkbook();
         IFile iFile = OseeData.getIFile("CodeUnit_To_SRS_Trace.xml");
         AIFile.writeToFile(iFile, charBak);
         Program.launch(iFile.getLocation().toOSString());
      }
   }

   private void handleDirectory(File directory) throws IOException, OseeCoreException {
      if (directory == null || directory.getParentFile() == null) {
         throw new OseeArgumentException("The path [%s] is invalid.", directory);
      }

      pathPrefixLength = directory.getParentFile().getAbsolutePath().length();

      for (File sourceFile : Lib.recursivelyListFiles(directory, filePattern)) {
         List<String> traceMarks = traceExtractor.getTraceMarksFromFile(sourceFile);

         int matchCount = 0;
         String relativePath = sourceFile.getPath().substring(pathPrefixLength);
         codeUnits.add(relativePath);
         for (String traceMark : traceMarks) {
            handelReqTrace(relativePath, traceMark);
            matchCount++;
         }
         if (matchCount == 0) {
            noTraceabilityFiles.add(relativePath);
         }
      }
   }

   private void writeNoTraceFilesSheet() throws IOException {
      excelWriter.startSheet("no match files", 1);
      for (String path : noTraceabilityFiles) {
         excelWriter.writeRow(path);
      }
      excelWriter.endSheet();
   }

   private void writeTraceCountsSheet() throws IOException, OseeCoreException {
      excelWriter.startSheet("trace counts", 3);
      excelWriter.writeRow("SRS Requirement from Database", "Trace Count", "Partitions");
      excelWriter.writeRow("% requirement coverage", null, "=1-COUNTIF(C2,&quot;0&quot;)/COUNTA(C2)");

      for (Artifact artifact : requirementData.getDirectSwRequirements()) {
         excelWriter.writeRow(artifact.getName(), String.valueOf(reqsTraceCounts.get(artifact)),
            Collections.toString(",", artifact.getAttributesToStringList(CoreAttributeTypes.Partition)));
      }

      excelWriter.endSheet();
   }

   private void handelReqTrace(String path, String traceMark) throws OseeCoreException, IOException {
      String foundStr;
      Artifact reqArtifact = null;

      if (traceExtractor.isValidTraceMark(traceMark) != true) {
         foundStr = "invalid trace mark";
      } else {
         reqArtifact = requirementData.getRequirementFromTraceMark(traceMark);
         if (reqArtifact == null) {
            Pair<String, String> structuredRequirement = traceExtractor.getStructuredRequirement(traceMark);
            if (structuredRequirement != null) {
               reqArtifact = requirementData.getRequirementFromTraceMark(structuredRequirement.getFirst());

               if (reqArtifact == null) {
                  foundStr = "no match in DB";
               } else {
                  // for local data and procedures search requirement text for traceMark
                  // example local data [{SUBSCRIBER}.ID] and example procedure {CURSOR_ACKNOWLEDGE}.NORMAL
                  String textContent =
                     WordUtil.textOnly(reqArtifact.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, "")).toUpperCase();
                  if (textContent.contains(traceExtractor.getCanonicalRequirementName(structuredRequirement.getSecond()))) {
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
         name = reqArtifact.getName();
         requirementToCodeUnitsMap.put(reqArtifact, path);
      }

      if (writeOutResults) {
         excelWriter.writeRow(foundStr, path, name, traceMark);
      }
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

   public RequirementData getRequirementData() {
      return requirementData;
   }
}