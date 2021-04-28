/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.applicability;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.FileTypeApplicabilityData;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock.ApplicabilityType;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Rule Applies product line engineering block applicability to file of the configured file extensions. This class is
 * deliberately not thread-safe.
 *
 * @author Ryan D. Brooks
 */
public class BlockApplicabilityRule extends Rule {
   private final BlockApplicabilityOps orcsApplicability;
   private final Map<String, FileTypeApplicabilityData> fileTypeApplicabilityDataMap;
   private final Stack<ApplicabilityBlock> applicBlocks = new Stack<>();

   public BlockApplicabilityRule(BlockApplicabilityOps orcsApplicability, Map<String, FileTypeApplicabilityData> fileTypeApplicabilityData) {
      super(null); // don't change extension on resulting file (i.e. overwrite the original file)

      this.orcsApplicability = orcsApplicability;
      this.fileTypeApplicabilityDataMap = fileTypeApplicabilityData;
   }

   /**
    * Follows a similar pattern to the default process with a few differences. Those being, saving the processed file
    * into a parallel staging directory, creating links if the file did not have applied applicability, and finally a
    * system of handling a config file that defines entire files applicability. The scope of the configuration file is
    * any siblings, or any descendants. They are removed from the excluded files set one the scope is complete.
    */
   public void process(File inFile, String stagePath, Set<String> excludedFiles) throws OseeCoreException {
      if (!excludedFiles.contains(inFile.getName())) {
         File stageFile = new File(stagePath, inFile.getName());
         if (inFile.isDirectory()) {
            if (!stageFile.exists() && !stageFile.mkdir()) {
               throw new OseeCoreException("Could not create stage directory");
            }
            List<File> children = new ArrayList<>();
            children.addAll(Arrays.asList(inFile.listFiles()));
            Set<String> filesInCurrentScope = processConfig(children, stageFile);
            excludedFiles.addAll(filesInCurrentScope);
            for (File child : children) {
               process(child, stageFile.getPath(), excludedFiles);
            }
            excludedFiles.removeAll(filesInCurrentScope);
         } else {
            try {
               boolean ruleWasApplicable = false;
               if (fileNamePattern.matcher(inFile.getName()).matches()) {
                  inputFile = inFile;
                  process(inFile, stageFile);
                  ruleWasApplicable = this.ruleWasApplicable;
               }
               if (!ruleWasApplicable) {
                  Files.createLink(stageFile.toPath(), inFile.toPath());
               }
            } catch (IOException ex) {
               OseeCoreException.wrap(ex);
            }
         }
      }
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);
      FileTypeApplicabilityData fileTypeApplicabilityData =
         fileTypeApplicabilityDataMap.get(Lib.getExtension(getInputFile().getName()));
      Matcher matcher = fileTypeApplicabilityData.getCommentedTagPattern().matcher(seq);

      int matcherIndex = 0;
      while (matcherIndex < seq.length() && matcher.find(matcherIndex)) {
         String beginFeature = matcher.group(BlockApplicabilityOps.beginFeatureCommentMatcherGroup);
         String endFeature = matcher.group(BlockApplicabilityOps.endFeatureCommentMatcherGroup);

         if (beginFeature != null) {
            matcherIndex = startApplicabilityBlock(beginFeature, matcher, fileTypeApplicabilityData);
         } else if (endFeature != null) {
            matcherIndex = finishApplicabilityBlock(changeSet, matcher);
            ruleWasApplicable = true;
         } else {
            throw new OseeCoreException("Did not find a start or end feature tag");
         }
      }
      return changeSet;
   }

   private int startApplicabilityBlock(String beginFeature, Matcher matcher, FileTypeApplicabilityData fileTypeApplicabilityData) {
      ApplicabilityBlock applicStart = new ApplicabilityBlock(ApplicabilityType.Feature);
      applicStart.setFileTypeApplicabilityData(fileTypeApplicabilityData);
      applicStart.setApplicabilityExpression(matcher.group(BlockApplicabilityOps.beginFeatureTagMatcherGroup));
      applicStart.setStartInsertIndex(matcher.start());
      applicStart.setStartTextIndex(matcher.end());
      applicStart.setBeginTag(beginFeature);
      applicBlocks.add(applicStart);
      return matcher.end();
   }

   private int finishApplicabilityBlock(ChangeSet changeSet, Matcher matcher) {
      if (applicBlocks.isEmpty()) {
         throw new OseeCoreException("An End Feature tag was found before a beginning Feature tag");
      }
      ApplicabilityBlock applicBlock = applicBlocks.pop();
      applicBlock.setEndTextIndex(matcher.start());
      applicBlock.setEndInsertIndex(matcher.end());
      applicBlock.setInsideText(
         changeSet.subSequence(applicBlock.getStartTextIndex(), applicBlock.getEndTextIndex()).toString());
      String replacementText = orcsApplicability.evaluateApplicabilityExpression(applicBlock);
      changeSet.replace(applicBlock.getStartInsertIndex(), applicBlock.getEndInsertIndex(), replacementText);
      return matcher.end();
   }

   /**
    * This method checks the children of a directory for a BlockApplicabilityConfig text file. If found, it will process
    * that file to find files that have applicability applied to them. Each entry into the text file should follow a
    * syntax of /FileName.ext/.
    */
   private Set<String> processConfig(List<File> children, File stageFile) {
      Set<String> filesToExclude = new HashSet<>();
      BufferedReader reader;
      String readLine;
      File configFile = null;

      for (File child : children) {
         String fileName = child.getName();
         if (fileName.equals(".fileApplicability")) {
            configFile = child;
            FileTypeApplicabilityData fileTypeApplicabilityData =
               fileTypeApplicabilityDataMap.get(Lib.getExtension(configFile.getName()));
            Pattern tagPattern = fileTypeApplicabilityData.getCommentedTagPattern();

            try {
               // Reading the original file for all names
               reader = new BufferedReader(new FileReader(configFile));
               while ((readLine = reader.readLine()) != null) {
                  if (!tagPattern.matcher(readLine).matches() && !readLine.isEmpty()) {
                     filesToExclude.add(readLine);
                  }
               }
               reader.close();

               // Using existing BlockApplicability logic to process the file
               File stagedConfig = new File(stageFile, configFile.getName());
               inputFile = configFile;
               process(configFile, stagedConfig);

               // Reading the new staged config file, any names that are left are included in the publish and removed from the list

               reader = new BufferedReader(new FileReader(stagedConfig));
               while ((readLine = reader.readLine()) != null) {
                  if (!tagPattern.matcher(readLine).matches() && !readLine.isEmpty()) {
                     filesToExclude.remove(readLine);
                  }
               }
               reader.close();
               stagedConfig.delete();
            } catch (IOException ex) {
               OseeCoreException.wrap(ex);
            }
            break;
         }
      }

      // Remove the config file from the children list so it is not processed again
      children.remove(configFile);

      return filesToExclude;
   }
}