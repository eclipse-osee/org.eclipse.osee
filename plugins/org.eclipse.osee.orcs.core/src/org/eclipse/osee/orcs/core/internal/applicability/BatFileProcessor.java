/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.Stack;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.FileTypeApplicabilityData;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock.ApplicabilityType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Applies product line engineering block applicability to file of the configured file extensions. Formerly modeled
 * under the Rule Interface, BatFileProcessor works with BatStagingCreator to process these files in place in a
 * thread-safe way. BatFileProcessor focuses on the processing of individual files in place. This class is created for
 * each file in order to achieve thread safety.
 *
 * @author Ryan D. Brooks
 * @author Branden W. Phillips
 */
public class BatFileProcessor {

   private final BlockApplicabilityOps orcsApplicability;
   private final FileTypeApplicabilityData fileTypeApplicabilityData;
   private final Stack<ApplicabilityBlock> applicBlocks = new Stack<>();
   private final boolean isConfig;
   private final boolean commentNonApplicableBlocks;
   private final XResultData results = new XResultData();
   private final String charsetString = "UTF8";
   private boolean tagProcessed = false;

   public BatFileProcessor(BlockApplicabilityOps orcsApplicability, FileTypeApplicabilityData fileTypeApplicabilityData, boolean isConfig, boolean commentNonApplicableBlocks) {
      this.orcsApplicability = orcsApplicability;
      this.fileTypeApplicabilityData = fileTypeApplicabilityData;
      this.isConfig = isConfig;
      this.commentNonApplicableBlocks = commentNonApplicableBlocks;
      if (!OseeProperties.isInTest()) {
         this.results.setLogToSysErr(true);
      }
   }

   public boolean processFile(File inFile, File outFile) throws Exception {
      if (inFile.exists()) {
         CharBuffer fileContent = applyApplicabilityContent(inFile);
         if (tagProcessed) {
            results.logf("Applicability was applied to %s\n", inFile.getName());
            if (fileContent != null) {
               BufferedWriter writer =
                  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
               writer.write(fileContent.array());
               writer.close();
            }
         }
      }
      return tagProcessed;
   }

   private CharBuffer applyApplicabilityContent(File inFile) throws Exception {
      results.logf("BatFileProcessor::applyApplicabilityContent => Started for file %s\n", inFile.getPath());
      CharBuffer fileContent = Lib.fileToCharBuffer(inFile, charsetString);
      String toReturn = fileContent.toString();
      Matcher matcher = fileTypeApplicabilityData.getCommentedTagPattern().matcher(toReturn);

      int matcherIndex = 0;
      String applicabilityExpression;
      ApplicabilityType applicabilityType;
      while (matcherIndex < toReturn.length() && matcher.find(matcherIndex)) {
         String beginFeature = matcher.group(BlockApplicabilityOps.beginFeatureCommentMatcherGroup);
         String endFeature = matcher.group(BlockApplicabilityOps.endFeatureCommentMatcherGroup);
         String beginConfig = matcher.group(BlockApplicabilityOps.beginConfigCommentMatcherGroup);
         String endConfig = matcher.group(BlockApplicabilityOps.endConfigCommentMatcherGroup);
         String beginConfigGrp = matcher.group(BlockApplicabilityOps.beginConfigGrpCommentMatcherGroup);
         String endConfigGrp = matcher.group(BlockApplicabilityOps.endConfigGrpCommentMatcherGroup);

         if (beginFeature != null) {
            applicabilityType = ApplicabilityType.Feature;
            applicabilityExpression = matcher.group(BlockApplicabilityOps.beginFeatureTagMatcherGroup);
            matcherIndex = startApplicabilityBlock(applicabilityType, matcher, beginFeature, applicabilityExpression);
         } else if (beginConfig != null) {
            applicabilityExpression = matcher.group(BlockApplicabilityOps.beginConfigTagMatcherGroup);
            applicabilityType = ApplicabilityType.Configuration;
            if (beginConfig.contains("Not")) {
               applicabilityType = ApplicabilityType.NotConfiguration;
            }
            matcherIndex = startApplicabilityBlock(applicabilityType, matcher, beginConfigGrp, applicabilityExpression);
         } else if (beginConfigGrp != null) {
            applicabilityExpression = matcher.group(BlockApplicabilityOps.beginConfigGrpTagMatcherGroup);
            applicabilityType = ApplicabilityType.ConfigurationGroup;
            if (beginConfigGrp.contains("Not")) {
               applicabilityType = ApplicabilityType.NotConfigurationGroup;
            }
            matcherIndex = startApplicabilityBlock(applicabilityType, matcher, beginConfigGrp, applicabilityExpression);
         } else if (endFeature != null || endConfig != null || endConfigGrp != null) {
            if (applicBlocks.isEmpty()) {
               OseeLog.log(getClass(), Level.INFO,
                  "BatFileProcessor::applyApplicabilityContent => An Applicability End tag was found before a beginning tag for: " + inFile.getPath());
               results.warningf("An Applicability End tag was found before a beginning tag for %s\n", inFile.getPath());
               tagProcessed = false;
               return fileContent;
            }
            ApplicabilityBlock applicBlock = finishApplicabilityBlock(toReturn, matcher);
            String toReplace = toReturn.substring(applicBlock.getStartInsertIndex(), applicBlock.getEndInsertIndex());
            toReturn = toReturn.replace(toReplace, applicBlock.getInsideText());
            matcherIndex = applicBlock.getStartInsertIndex() + applicBlock.getInsideText().length();
            matcher = fileTypeApplicabilityData.getCommentedTagPattern().matcher(toReturn);

            tagProcessed = true;
         } else {
            OseeLog.log(getClass(), Level.INFO,
               "BatFileProcessor::applyApplicabilityContent => Did not find a start or end feature tag for: " + inFile.getPath());
            results.warningf("Did not find a start or end feature tag for %s but a similar tag was matched\n",
               inFile.getPath());
            tagProcessed = false;
            return fileContent;
         }
      }
      if (!applicBlocks.isEmpty()) {
         throw new TagNotPlacedCorrectlyException("Tag was not placed correctly in file: " + inFile.getPath());
      }
      results.logf("BatFileProcessor::applyApplicabilityContent => Completed for file %s and tagProcessed = %s\n",
         inFile.getPath(), tagProcessed);
      return CharBuffer.wrap(toReturn.toCharArray());
   }

   private int startApplicabilityBlock(ApplicabilityType applicabilityType, Matcher matcher, String beginTag, String applicabilityExpression) {
      ApplicabilityBlock applicStart = new ApplicabilityBlock(applicabilityType);
      applicStart.setApplicabilityExpression(applicabilityExpression);
      applicStart.setStartInsertIndex(matcher.start());
      applicStart.setStartTextIndex(matcher.end());
      applicStart.setBeginTag(beginTag);
      applicBlocks.add(applicStart);
      return matcher.end();
   }

   private ApplicabilityBlock finishApplicabilityBlock(String toReturn, Matcher matcher) throws IOException {
      ApplicabilityBlock applicBlock = applicBlocks.pop();
      applicBlock.setEndTextIndex(matcher.start());
      applicBlock.setEndInsertIndex(matcher.end());

      String insideText =
         toReturn.subSequence(applicBlock.getStartTextIndex(), applicBlock.getEndTextIndex()).toString();
      applicBlock.setInsideText(insideText);

      String replacementText = orcsApplicability.evaluateApplicabilityExpression(applicBlock);
      /**
       * BlockApplicabilityOps currently removes else statements using WordCoreUtil regex, for the BAT this leaves
       * behind the comment portion of the else. The below line is used to remove those lines along with any other
       * potential leftover empty comments.
       */
      if (!replacementText.isEmpty()) {
         if (!fileTypeApplicabilityData.getCommentPrefixRegex().isEmpty() && !fileTypeApplicabilityData.getCommentSuffixRegex().isEmpty()) {
            replacementText = replacementText.replaceAll(System.getProperty(
               "line.separator") + BlockApplicabilityOps.INLINE_WHITESPACE + fileTypeApplicabilityData.getCommentPrefixRegex() + BlockApplicabilityOps.INLINE_WHITESPACE + fileTypeApplicabilityData.getCommentSuffixRegex() + BlockApplicabilityOps.INLINE_WHITESPACE + System.getProperty(
                  "line.separator"),
               "");
         }
         if (!fileTypeApplicabilityData.getCommentPrefixRegex().isEmpty()) {
            replacementText = replacementText.replaceAll(
               BlockApplicabilityOps.INLINE_WHITESPACE + fileTypeApplicabilityData.getCommentPrefixRegex() + BlockApplicabilityOps.INLINE_WHITESPACE + System.getProperty(
                  "line.separator"),
               System.getProperty("line.separator"));
            replacementText = replacementText.replaceAll(
               BlockApplicabilityOps.INLINE_WHITESPACE + fileTypeApplicabilityData.getCommentPrefixRegex() + BlockApplicabilityOps.INLINE_WHITESPACE + '$',
               "");
         }
         if (!fileTypeApplicabilityData.getCommentSuffixRegex().isEmpty()) {
            replacementText = replacementText.replaceAll(System.getProperty(
               "line.separator") + BlockApplicabilityOps.INLINE_WHITESPACE + fileTypeApplicabilityData.getCommentSuffixRegex() + BlockApplicabilityOps.INLINE_WHITESPACE,
               System.getProperty("line.separator"));
            replacementText = replacementText.replaceAll(
               '^' + BlockApplicabilityOps.INLINE_WHITESPACE + fileTypeApplicabilityData.getCommentSuffixRegex() + BlockApplicabilityOps.INLINE_WHITESPACE,
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
            toReturn.subSequence(applicBlock.getStartInsertIndex(), applicBlock.getEndInsertIndex()).toString();
         fullText = getCommentedString(fullText, fileTypeApplicabilityData.getCommentPrefix(),
            fileTypeApplicabilityData.getCommentSuffix());
         String commentedReplacementText = getCommentedString(replacementText,
            fileTypeApplicabilityData.getCommentPrefix(), fileTypeApplicabilityData.getCommentSuffix());
         replacementText = fullText.replace(commentedReplacementText, replacementText);
      }

      applicBlock.setInsideText(replacementText);
      return applicBlock;
   }

   private String getCommentedString(String text, String commentPrefix, String commentSuffix) throws IOException {
      Pattern whitespacePattern = Pattern.compile("\\s*");
      BufferedReader reader = new BufferedReader(new StringReader(text));
      StringBuilder strB = new StringBuilder();
      String line;
      String newLine = getNewLineFromFile(text);
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

}
