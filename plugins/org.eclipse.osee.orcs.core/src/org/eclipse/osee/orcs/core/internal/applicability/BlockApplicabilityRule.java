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
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
   private final boolean commentNonApplicableBlocks;
   private boolean isConfig = false;
   private final Stack<ApplicabilityBlock> applicBlocks = new Stack<>();

   public BlockApplicabilityRule(BlockApplicabilityOps orcsApplicability, Map<String, FileTypeApplicabilityData> fileTypeApplicabilityData, boolean commentNonApplicableBlocks) {
      super(null); // don't change extension on resulting file (i.e. overwrite the original file)

      this.orcsApplicability = orcsApplicability;
      this.fileTypeApplicabilityDataMap = fileTypeApplicabilityData;
      this.commentNonApplicableBlocks = commentNonApplicableBlocks;
   }

   /**
    * Similar format to Rule.process but with some special adjustments for BlockApplicability.<br/>
    * If the inFile is an excluded file, the method returns null.<br/>
    * <br/>
    * When the file is a directory, the method handles special cases for the config file, along with situations where
    * the staging directory already exists and is being refreshed instead of created from scratch. Any file still
    * existing in the staging children, means that the file was once staged but now is not applicable for various
    * reasons and is deleted. The filesInConfig list keeps track of the excluded files scope. Any sibling or descendant
    * can be excluded by a config file.<br/>
    * <br/>
    * If a file is not a directory, to be processed it must have an accepted file extension via the GlobalPreferences
    * artifact. If a former stage file existed, it is deleted before processing. If the file was not changed, a link is
    * created. If it was changed, that new file is set as ReadOnly.<br/>
    * <br/>
    *
    * @param inFile - File to process
    * @param stagePath - Path to place the outfile in
    * @param excludedFiles - A list of files that will not be included in the staging area
    * @return File - The staged file, or null if the file was not included
    * @throws OseeCoreException
    */
   public File process(File inFile, String stagePath, Set<String> excludedFiles) throws OseeCoreException {
      if (!excludedFiles.contains(inFile.getName())) {
         File stageFile = new File(stagePath, inFile.getName());
         if (inFile.isDirectory()) {
            if (!stageFile.exists() && !stageFile.mkdir()) {
               throw new OseeCoreException("Could not create stage directory");
            }
            // Get the children for the inFile
            List<File> children = new ArrayList<>();
            children.addAll(Arrays.asList(inFile.listFiles()));

            // Get children of the potentially existing stageFile
            List<File> stagedChildren = new ArrayList<>();
            stagedChildren.addAll(Arrays.asList(stageFile.listFiles()));

            Set<String> filesInConfig = processConfig(children, stagedChildren, stageFile);
            excludedFiles.addAll(filesInConfig);
            for (File child : children) {
               File stagedFile = process(child, stageFile.getPath(), excludedFiles);

               /**
                * Since the file was processed, it does not need to be removed. If null was returned, nothing is
                * removed.
                */
               stagedChildren.remove(stagedFile);
            }

            // Any staged child that was not processed, is now deleted.
            for (File fileToRemove : stagedChildren) {
               try {
                  /**
                   * This line will go through a directory's tree and delete all the files. In order to delete a
                   * directory, its' children must be deleted. If the file is not a directory, this will delete the file
                   * anyway.
                   */
                  Files.walk(fileToRemove.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(
                     File::delete);
               } catch (IOException ex) {
                  throw new OseeCoreException("Error when cleaning up leftover files during refresh");
               }
            }

            // The excluded files from this scope are removed from the list
            excludedFiles.removeAll(filesInConfig);
         } else {
            try {
               boolean ruleWasApplicable = false;
               if (fileNamePattern.matcher(inFile.getName()).matches()) {
                  if (stageFile.exists()) {
                     // In case this file has already been staged, we remove it before processing
                     stageFile.delete();
                  }

                  inputFile = inFile;
                  process(inFile, stageFile);
                  ruleWasApplicable = this.ruleWasApplicable;
               }
               if (!ruleWasApplicable) {
                  Files.createLink(stageFile.toPath(), inFile.toPath());
               } else {
                  // Only want to set new files to read only, otherwise the original will also be read only
                  stageFile.setReadOnly();
               }
            } catch (IOException ex) {
               OseeCoreException.wrap(ex);
            }
         }
         return stageFile;
      } else {
         /**
          * By returning null, we signal to the parent call that this file was not processed and if a staged version
          * exists, it should be deleted.
          */
         return null;
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
         String beginConfig = matcher.group(BlockApplicabilityOps.beginConfigCommentMatcherGroup);
         String endConfig = matcher.group(BlockApplicabilityOps.endConfigCommentMatcherGroup);
         String beginConfigGrp = matcher.group(BlockApplicabilityOps.beginConfigGrpCommentMatcherGroup);
         String endConfigGrp = matcher.group(BlockApplicabilityOps.endConfigGrpCommentMatcherGroup);
         String applicabilityExpression;

         if (beginFeature != null) {
            applicabilityExpression = matcher.group(BlockApplicabilityOps.beginFeatureTagMatcherGroup);
            matcherIndex = startApplicabilityBlock(ApplicabilityType.Feature, matcher, beginFeature,
               applicabilityExpression, fileTypeApplicabilityData);
         } else if (beginConfig != null) {
            applicabilityExpression = matcher.group(BlockApplicabilityOps.beginConfigTagMatcherGroup);
            ApplicabilityType applicabilityType = ApplicabilityType.Configuration;
            if (beginConfig.contains("Not")) {
               applicabilityType = ApplicabilityType.NotConfiguration;
            }
            matcherIndex = startApplicabilityBlock(applicabilityType, matcher, beginConfigGrp, applicabilityExpression,
               fileTypeApplicabilityData);
         } else if (beginConfigGrp != null) {
            applicabilityExpression = matcher.group(BlockApplicabilityOps.beginConfigGrpTagMatcherGroup);
            ApplicabilityType applicabilityType = ApplicabilityType.ConfigurationGroup;
            if (beginConfigGrp.contains("Not")) {
               applicabilityType = ApplicabilityType.NotConfigurationGroup;
            }
            matcherIndex = startApplicabilityBlock(applicabilityType, matcher, beginConfigGrp, applicabilityExpression,
               fileTypeApplicabilityData);
         } else if (endFeature != null || endConfig != null || endConfigGrp != null) {
            matcherIndex = finishApplicabilityBlock(changeSet, matcher);
            ruleWasApplicable = true;
         } else {
            throw new OseeCoreException("Did not find a start or end feature tag");
         }
      }
      return changeSet;
   }

   private int startApplicabilityBlock(ApplicabilityType applicabilityType, Matcher matcher, String beginTag, String applicabilityExpression, FileTypeApplicabilityData fileTypeApplicabilityData) {
      ApplicabilityBlock applicStart = new ApplicabilityBlock(applicabilityType);
      applicStart.setFileTypeApplicabilityData(fileTypeApplicabilityData);
      applicStart.setApplicabilityExpression(applicabilityExpression);
      applicStart.setStartInsertIndex(matcher.start());
      applicStart.setStartTextIndex(matcher.end());
      applicStart.setBeginTag(beginTag);
      applicBlocks.add(applicStart);
      return matcher.end();
   }

   private int finishApplicabilityBlock(ChangeSet changeSet, Matcher matcher) {
      if (applicBlocks.isEmpty()) {
         throw new OseeCoreException("An Applicability End tag was found before a beginning tag");
      }
      ApplicabilityBlock applicBlock = applicBlocks.pop();
      applicBlock.setEndTextIndex(matcher.start());
      applicBlock.setEndInsertIndex(matcher.end());

      String insideText =
         changeSet.subSequence(applicBlock.getStartTextIndex(), applicBlock.getEndTextIndex()).toString();
      applicBlock.setInsideText(insideText);

      String replacementText = orcsApplicability.evaluateApplicabilityExpression(applicBlock);
      /**
       * BlockApplicabilityOps currently removes else statements using WordCoreUtil regex, for the BAT this leaves
       * behind the comment portion of the else. The below line is used to remove those lines along with any other
       * potential leftover empty comments.
       */
      if (!replacementText.isEmpty()) {
         if (!applicBlock.getFileTypeApplicabilityData().getCommentPrefixRegex().isEmpty()) {
            replacementText = replacementText.replaceAll(
               BlockApplicabilityOps.INLINE_WHITESPACE + applicBlock.getFileTypeApplicabilityData().getCommentPrefixRegex() + BlockApplicabilityOps.INLINE_WHITESPACE,
               "");
         }
         if (!applicBlock.getFileTypeApplicabilityData().getCommentSuffixRegex().isEmpty()) {
            replacementText = replacementText.replaceAll(
               BlockApplicabilityOps.INLINE_WHITESPACE + applicBlock.getFileTypeApplicabilityData().getCommentSuffixRegex() + BlockApplicabilityOps.INLINE_WHITESPACE,
               "");
         }
      }

      if (!isConfig && commentNonApplicableBlocks) {
         /**
          * To apply comments to the block, first the entire block is commented which includes the feature tags. Then,
          * the replacement text that was returned has comments applied to it that way there is text to match within the
          * full text block. Finally, using those strings, a replaceAll is performed to substitute in the applicable
          * uncommented text.
          */
         String fullText =
            changeSet.subSequence(applicBlock.getStartInsertIndex(), applicBlock.getEndInsertIndex()).toString();
         fullText = getCommentedString(fullText, applicBlock.getFileTypeApplicabilityData().getCommentPrefix(),
            applicBlock.getFileTypeApplicabilityData().getCommentSuffix());
         String commentedReplacementText =
            getCommentedString(replacementText, applicBlock.getFileTypeApplicabilityData().getCommentPrefix(),
               applicBlock.getFileTypeApplicabilityData().getCommentSuffix());
         replacementText = fullText.replace(commentedReplacementText, replacementText);
      }

      changeSet.replace(applicBlock.getStartInsertIndex(), applicBlock.getEndInsertIndex(), replacementText);
      return matcher.end();
   }

   private String getCommentedString(String text, String commentPrefix, String commentSuffix) {
      Pattern whitespacePattern = Pattern.compile("\\s*");
      BufferedReader reader = new BufferedReader(new StringReader(text));
      StringBuilder strB = new StringBuilder();
      String line;
      String newLine = getNewLineFromFile(text);
      try {
         while ((line = reader.readLine()) != null) {
            if (!line.isEmpty()) {
               boolean noPrefix = commentPrefix.isEmpty() ? true : !line.contains(commentPrefix);
               boolean noSuffix = commentSuffix.isEmpty() ? true : !line.contains(commentSuffix);
               if (noPrefix && noSuffix) {
                  Matcher match = whitespacePattern.matcher(line);
                  if (match.find()) {
                     strB.append(match.group());
                  }
                  strB.append(commentPrefix);
                  strB.append(line.substring(match.end()));
                  strB.append(commentSuffix);
               } else {
                  strB.append(line);
               }
            }
            strB.append(newLine);
         }
         reader.close();
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }

      return strB.toString();
   }

   /**
    * Using the given text, this finds the first instance of a newline character and returns that to be replaced back
    * into the file. This is to protect from different new line characters styles between operating systems. The style
    * that comes in is the style that goes out.
    */
   private String getNewLineFromFile(String text) {
      Matcher matcher = Pattern.compile(BlockApplicabilityOps.SINGLE_NEW_LINE).matcher(text);
      if (matcher.find()) {
         return matcher.group();
      } else {
         return System.lineSeparator();
      }
   }

   /**
    * This method checks the children of a directory for a BlockApplicabilityConfig text file. If found, it will process
    * that file to find files that have applicability applied to them. The processing of this file relies on the only
    * text being left are applicable file names. If commenting is turned on, the isConfig boolean is used to fix that
    * and make sure no commenting is enabled during that file processing.
    */
   private Set<String> processConfig(List<File> children, List<File> stagedChildren, File stageFile) {
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
               isConfig = true;
               process(configFile, stagedConfig);
               isConfig = false;

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