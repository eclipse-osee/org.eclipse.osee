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
package org.eclipse.osee.define.traceability.blam;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.TestUnitTagger;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Roberto E. Escobar
 */
public class AddTraceMarksToTraceUnits extends AbstractBlam {

   private static final Pattern commentPattern =
      Pattern.compile("/\\*\\s*\\*\\s*SCRIPT TRACEABILITY.*?\\*/", Pattern.DOTALL);

   @Override
   public String getName() {
      return "Add Trace Marks to Resource";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Trace");
   }

   @Override
   public String getDescriptionUsage() {
      return "Adds trace marks to files selected.\n*** WARNING_OVERLAY: When \"Persist Changes\" is selected, files will be modified in place.\n There is no way to undo this operation - make sure you know what you are doing. ***\n ";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append(
         "<XWidget xwidgetType=\"XLabel\" displayName=\"Select File Or Folder (file can have a list of folders separated by newlines)\"/>");
      builder.append("<XWidget xwidgetType=\"XFileSelectionDialog\" displayName=\"Select File\" />");
      builder.append("<XWidget xwidgetType=\"XDirectorySelectionDialog\" displayName=\"Select Folder\" />");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Trace Types:\"/>");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Sub-Folders\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append(
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Requirements Branch\" toolTip=\"Select a requirements branch.\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private void checkPath(String filePath, String type) {
      if (!Strings.isValid(filePath)) {
         throw new OseeArgumentException("Please enter a valid %s path", type);
      }
      File file = new File(filePath);
      if (!file.exists()) {
         throw new OseeArgumentException("%s path [%s] is not accessible", type, filePath);
      }
   }

   private URI getSourceURI(VariableMap variableMap) {
      String filePath = variableMap.getString("Select File");
      String folderPath = variableMap.getString("Select Folder");

      String pathToUse = null;
      if (Strings.isValid(folderPath) && Strings.isValid(filePath)) {
         throw new OseeArgumentException("Enter file or folder but not both");
      } else if (Strings.isValid(folderPath)) {
         checkPath(folderPath, "folder");
         pathToUse = folderPath;
      } else {
         checkPath(filePath, "file");
         pathToUse = filePath;
      }
      return new File(pathToUse).toURI();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      try {
         final URI source = getSourceURI(variableMap);
         final boolean isRecursionAllowed = variableMap.getBoolean("Include Sub-Folders");
         BranchId requirementsBranch = variableMap.getBranch("Requirements Branch");

         final int TOTAL_WORK = Integer.MAX_VALUE;
         monitor.beginTask(getName(), TOTAL_WORK);

         List<File> files;
         File sourceFile = new File(source.getPath());
         if (sourceFile.isDirectory()) {
            if (isRecursionAllowed) {
               files = Lib.recursivelyListFiles(new File(source.getPath()), Pattern.compile(".+\\.java"));
            } else {
               files = Arrays.asList(sourceFile.listFiles(new FileFilter() {

                  @Override
                  public boolean accept(File pathname) {
                     return pathname.getName().endsWith(".java");
                  }
               }));
            }
         } else {
            files = Collections.singletonList(sourceFile);
         }
         processFiles(files, requirementsBranch);
      } finally {
         monitor.done();
      }
   }

   private void processFiles(List<File> files, BranchId branch) {
      Matcher matcher = TestUnitTagger.ANNOTATION_PATTERN.matcher("");
      for (File file : files) {
         String guid = null;
         try {
            guid = TestUnitTagger.getInstance().getSourceTag(file.toURI());
         } catch (Exception ex) {
            logf("Error reading guid from %s", file.getName());
            continue;
         }
         if (GUID.isValid(guid)) {
            Artifact testCase = ArtifactQuery.getArtifactFromId(guid, branch);
            List<Artifact> uses = testCase.getRelatedArtifacts(CoreRelationTypes.Uses__Requirement);
            List<Artifact> verifies = testCase.getRelatedArtifacts(CoreRelationTypes.Verification__Requirement);
            StringBuilder sb = new StringBuilder();
            for (Artifact art : verifies) {
               sb.append(" * Verifies: ");
               sb.append(art.getName());
               sb.append("\n");
            }
            for (Artifact art : uses) {
               sb.append(" * Uses: ");
               sb.append(art.getName());
               sb.append("\n");
            }
            if (sb.length() > 0) {
               Matcher commentMatcher = commentPattern.matcher("");
               sb.insert(0, "/*\n * SCRIPT TRACEABILITY:\n *\n");
               sb.append(" */\n");
               String contents;
               try {
                  contents = Lib.fileToString(file);
               } catch (IOException ex) {
                  logf("Error reading contents from %s", file.getName());
                  continue;
               }
               commentMatcher.reset(contents);
               matcher.reset(contents);
               String modified = null;
               if (commentMatcher.find()) {
                  ChangeSet change = new ChangeSet(contents);
                  change.replace(commentMatcher.start(), commentMatcher.end(), sb.toString());
                  modified = change.applyChangesToSelf().toString();
               } else if (matcher.find()) {
                  ChangeSet change = new ChangeSet(contents);
                  change.insertBefore(matcher.start(), sb.toString());
                  modified = change.applyChangesToSelf().toString();
               }
               if (modified != null) {
                  try {
                     Lib.writeStringToFile(modified, file);
                  } catch (IOException ex) {
                     logf("Error writing changes to %s", file.getName());
                     continue;
                  }
               }
            }

         }
      }
   }
}
