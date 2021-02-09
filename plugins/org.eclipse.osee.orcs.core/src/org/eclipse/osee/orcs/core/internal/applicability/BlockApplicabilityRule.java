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

import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
   private final Map<String, Pattern> fileExtensionToPatternMap;
   private final Stack<ApplicabilityBlock> applicBlocks = new Stack<>();

   public BlockApplicabilityRule(BlockApplicabilityOps orcsApplicability, Map<String, Pattern> fileExtensionToPatternMap) {
      super(null); // don't change extension on resulting file (i.e. overwrite the original file)

      this.orcsApplicability = orcsApplicability;
      this.fileExtensionToPatternMap = fileExtensionToPatternMap;
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);
      Matcher matcher = fileExtensionToPatternMap.get(Lib.getExtension(getInputFile().getName())).matcher(seq);

      int matcherIndex = 0;
      while (matcherIndex < seq.length() && matcher.find(matcherIndex)) {
         String beginFeature = matcher.group(BlockApplicabilityOps.beginFeatureCommentMatcherGroup);
         String endFeature = matcher.group(BlockApplicabilityOps.endFeatureCommentMatcherGroup);

         if (beginFeature != null) {
            matcherIndex = startApplicabilityBlock(beginFeature, matcher);
         } else if (endFeature != null) {
            matcherIndex = finishApplicabilityBlock(changeSet, matcher);
            ruleWasApplicable = true;
         } else {
            throw new OseeCoreException("Did not find a start or end feature tag");
         }
      }
      return changeSet;
   }

   private int startApplicabilityBlock(String beginFeature, Matcher matcher) {
      ApplicabilityBlock applicStart = new ApplicabilityBlock(ApplicabilityType.Feature);
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
}