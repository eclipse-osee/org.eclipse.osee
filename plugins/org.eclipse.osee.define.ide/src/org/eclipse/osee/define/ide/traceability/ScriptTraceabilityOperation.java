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
package org.eclipse.osee.define.ide.traceability;

import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.ide.internal.Activator;
import org.eclipse.osee.define.ide.traceability.TraceUnitExtensionManager.TraceHandler;
import org.eclipse.osee.define.ide.traceability.data.RequirementData;
import org.eclipse.osee.define.ide.traceability.data.TraceMark;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class ScriptTraceabilityOperation extends TraceabilityProviderOperation {
   private static final Pattern filePattern = Pattern.compile(".*\\.(java|ada|ads|adb|c|h)");

   private static final Matcher structuredRequirementMatcher = Pattern.compile("\\[?(\\{[^\\}]+\\})(.*)").matcher("");
   private static final Matcher embeddedVolumeMatcher = Pattern.compile("\\{\\d+ (.*)\\}[ .]*").matcher("");
   private static final Matcher stripTrailingReqNameMatcher = Pattern.compile("(\\}|\\])(.*)").matcher("");
   private static final Matcher nonWordMatcher = Pattern.compile("[^A-Z_0-9]").matcher("");
   private static final Matcher subsystemMatcher = Pattern.compile("(\\w*)\\.ss").matcher("");
   private static final Matcher gitSubsystemMatcher = Pattern.compile("\\w*\\.ofp\\\\(\\w*)").matcher("");
   private final Collection<TraceHandler> traceHandlers;
   private final File file;
   private final RequirementData requirementData;
   private final ArrayList<String> noTraceabilityFiles = new ArrayList<>(200);
   private final CountingMap<Artifact> reqsTraceCounts = new CountingMap<>();
   private final HashCollectionSet<Artifact, String> requirementToCodeUnitsMap =
      new HashCollectionSet<>(LinkedHashSet::new);
   private final HashSet<String> codeUnits = new HashSet<>();
   private final CharBackedInputStream charBak;
   private final ISheetWriter excelWriter;
   private int pathPrefixLength;
   private final boolean writeOutResults;
   private final boolean isGitBased;
   private final boolean includeImpd;

   private ScriptTraceabilityOperation(RequirementData requirementData, File file, boolean writeOutResults, Collection<TraceHandler> traceHandlers, boolean isGitBased, boolean includeImpd) throws IOException {
      super("Importing Traceability", Activator.PLUGIN_ID);
      this.file = file;
      this.requirementData = requirementData;
      this.writeOutResults = writeOutResults;
      this.traceHandlers = traceHandlers;
      this.isGitBased = isGitBased;
      this.includeImpd = includeImpd;
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
   }

   public ScriptTraceabilityOperation(File file, BranchId branch, boolean writeOutResults, Collection<TraceHandler> traceHandlers, boolean isGitBased, ArtifactId viewId, boolean includeImpd) throws IOException {
      this(new RequirementData(branch, viewId), file, writeOutResults, traceHandlers, isGitBased, includeImpd);
   }

   public ScriptTraceabilityOperation(File file, BranchId branch, boolean writeOutResults, Collection<? extends IArtifactType> types, boolean withInheritance, Collection<TraceHandler> traceHandlers, boolean isGitBased, ArtifactId viewId, boolean includeImpd) throws IOException {
      this(new RequirementData(branch, types, withInheritance, viewId), file, writeOutResults, traceHandlers,
         isGitBased, includeImpd);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      monitor.worked(1);

      requirementData.initialize(monitor);
      if (writeOutResults) {
         excelWriter.startSheet("srs <--> code units", 6);
         excelWriter.writeRow("Req in DB", "Subsystem", "Code Unit", "Requirement Name",
            "Requirement Trace Mark in Code", "Trace Mark Match");
      }

      if (file.isFile()) {
         for (String path : Lib.readListFromFile(file, true)) {
            monitor.subTask(path);
            handleDirectory(new File(path), traceHandlers);
            checkForCancelledStatus(monitor);
         }
      } else if (file.isDirectory()) {
         handleDirectory(file, traceHandlers);
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

   private void handleDirectory(File directory, Collection<TraceHandler> traceHandlers) throws IOException {
      if (directory == null || directory.getParentFile() == null) {
         throw new OseeArgumentException("The path [%s] is invalid.", directory);
      }

      pathPrefixLength = directory.getParentFile().getAbsolutePath().length();

      for (File sourceFile : Lib.recursivelyListFiles(directory, filePattern)) {
         CharBuffer buffer = Lib.fileToCharBuffer(sourceFile);
         Collection<TraceMark> tracemarks = new LinkedList<>();
         for (TraceHandler handler : traceHandlers) {
            handler.getParser().setupTraceMatcher(includeImpd);
            handler.getParser().setupCommentTraceMatcher(includeImpd);
            Collection<TraceMark> marks = handler.getParser().getTraceMarks(buffer);
            tracemarks.addAll(marks);
         }

         int matchCount = 0;
         String relativePath = sourceFile.getPath().substring(pathPrefixLength);
         codeUnits.add(relativePath);
         for (TraceMark traceMark : tracemarks) {
            handelReqTrace(relativePath, traceMark, sourceFile);
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

   private void writeTraceCountsSheet() throws IOException {
      excelWriter.startSheet("trace counts", 4);
      excelWriter.writeRow("SRS Requirement from Database", "Trace Count", "Partitions", "Artifact Type");
      excelWriter.writeRow("% requirement coverage", null, "=1-COUNTIF(C2,&quot;0&quot;)/COUNTA(C2)", null);

      for (Artifact artifact : requirementData.getDirectRequirements()) {
         excelWriter.writeRow(artifact.getName(), String.valueOf(reqsTraceCounts.get(artifact)),
            Collections.toString(",", artifact.getAttributesToStringList(CoreAttributeTypes.Partition)),
            artifact.getArtifactType());
      }
      excelWriter.endSheet();
   }

   private Pair<String, String> getStructuredRequirement(String requirementMark) {
      Pair<String, String> toReturn = null;
      structuredRequirementMatcher.reset(requirementMark);
      if (structuredRequirementMatcher.matches() != false) {
         String primary = structuredRequirementMatcher.group(1);
         String secondary = structuredRequirementMatcher.group(2);

         if (Strings.isValid(primary) != false) {
            toReturn = new Pair<>(primary, secondary);
         }
      }
      return toReturn;
   }

   public String getCanonicalRequirementName(String requirementMark) {
      String canonicalReqReference = requirementMark;
      if (Strings.isValid(requirementMark) != false) {
         canonicalReqReference = requirementMark.toUpperCase();

         embeddedVolumeMatcher.reset(canonicalReqReference);
         if (embeddedVolumeMatcher.find()) {
            canonicalReqReference = embeddedVolumeMatcher.group(1);
         }

         // Added to strip trailing artifact descriptive names } ... or ] ....
         stripTrailingReqNameMatcher.reset(canonicalReqReference);
         if (stripTrailingReqNameMatcher.find()) {
            String trail = stripTrailingReqNameMatcher.group(2);
            if (Strings.isValid(trail) && !trail.startsWith(".")) {
               canonicalReqReference = canonicalReqReference.substring(0, stripTrailingReqNameMatcher.start(1) + 1);
            }
         }

         nonWordMatcher.reset(canonicalReqReference);
         canonicalReqReference = nonWordMatcher.replaceAll("");

      }
      return canonicalReqReference;
   }

   private String getSubsystem(String source, Matcher matcher) {
      String subSystem = null;
      matcher.reset(source);
      if (matcher.find()) {
         subSystem = matcher.group(1);
         subSystem = subSystem.toUpperCase();
      } else {
         subSystem = "no valid subsystem found";
      }
      return subSystem;
   }

   private void handelReqTrace(String path, TraceMark traceMark, File sourceFile) throws IOException {
      Artifact reqArtifact = null;
      String foundStr;
      String subSystem = null;
      String textContent = null;
      boolean traceMatch = false;

      subSystem = (isGitBased) ? getSubsystem(sourceFile.getPath(),
         gitSubsystemMatcher) : getSubsystem(sourceFile.getPath(), subsystemMatcher);

      if (traceMark.getTraceType().equals("Uses")) {
         foundStr = "invalid trace mark";
      } else {
         reqArtifact = requirementData.getRequirementFromTraceMark(traceMark.getRawTraceMark());
         if (reqArtifact == null) {
            Pair<String, String> structuredRequirement = getStructuredRequirement(traceMark.getRawTraceMark());
            if (structuredRequirement != null) {
               reqArtifact = requirementData.getRequirementFromTraceMark(structuredRequirement.getFirst());

               if (reqArtifact == null) {
                  foundStr = "no match in DB";
               } else {

                  // for local data and procedures search requirement text for traceMark
                  // example local data [{SUBSCRIBER}.ID] and example procedure {CURSOR_ACKNOWLEDGE}.NORMAL

                  //There is no WordTemplateContent in a button requirement so we need to verify it exists
                  //If its not there we need to render the button requirement in Word and pull out the body.
                  if (reqArtifact.getAttributeCount(CoreAttributeTypes.WordTemplateContent) > 0) {
                     textContent = WordUtil.textOnly(
                        reqArtifact.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, "")).toUpperCase();
                  } else {
                     List<Attribute<?>> attributes = reqArtifact.getAttributes();
                     for (Attribute<?> attribute : attributes) {
                        textContent = textContent + attribute.toString();
                     }
                  }
                  if (textContent != null && (textContent.contains(
                     structuredRequirement.getSecond()) || textContent.contains(
                        getCanonicalRequirementName(structuredRequirement.getSecond())))) {
                     foundStr = "req body match";
                  } else {
                     foundStr = "req name match/element missing in body";
                  }

               }
            } else {
               foundStr = "no match in DB";
            }
         } else {
            foundStr = fullMatch(reqArtifact);

            List<String> partitions = reqArtifact.getAttributesToStringList(CoreAttributeTypes.Partition);
            if (partitions.contains(subSystem)) {
               traceMatch = true;
            }
         }
      }

      String name = null;
      if (reqArtifact != null) {
         name = reqArtifact.getName();
         String inspection = getInspectionQual(reqArtifact);
         if (Strings.isValid(inspection)) {
            requirementToCodeUnitsMap.put(reqArtifact, inspection);
         } else {
            requirementToCodeUnitsMap.put(reqArtifact, path);
         }
      }

      if (writeOutResults) {
         excelWriter.writeRow(foundStr, subSystem, path, name, traceMark, traceMatch);
      }
   }

   private String fullMatch(Artifact reqArtifact) {
      reqsTraceCounts.put(reqArtifact);
      return "full match";
   }

   @Override
   public HashCollectionSet<Artifact, String> getRequirementToCodeUnitsMap() {
      return requirementToCodeUnitsMap;
   }

   /**
    * @return the codeUnits
    */
   @Override
   public HashSet<String> getCodeUnits() {
      return codeUnits;
   }

   @Override
   public RequirementData getRequirementData() {
      return requirementData;
   }

   @Override
   public Collection<Artifact> getTestUnitArtifacts(Artifact requirement) {
      Collection<Artifact> toReturn = new HashSet<>();
      Collection<String> scriptNames = requirementToCodeUnitsMap.getValues(requirement);
      if (scriptNames != null) {
         for (String script : scriptNames) {
            Artifact testScript;
            try {
               testScript = ArtifactTestRunOperator.getTestScriptFetcher().getNewArtifact(requirement.getBranch());
               testScript.setName(script);
               toReturn.add(testScript);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
      return toReturn;
   }

   @Override
   public Artifact getTestUnitByName(String name) {
      return null;
   }
}