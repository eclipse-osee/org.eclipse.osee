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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
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
   private final Map<String, Matcher> fileExtensionTofeatureStart;
   private final Map<String, Matcher> fileExtensionTofeatureEnd;
   private final BlockApplicabilityOps orcsApplicability;
   private final BranchId branch;
   private final ArtifactToken view;
   private Matcher startMatcher;
   private Matcher endMatcher;

   public BlockApplicabilityRule(BlockApplicabilityOps orcsApplicability, BranchId branch, ArtifactToken view, Map<String, String> fileExtensionToCommentPrefix) {
      super(null); // don't change extension on resulting file (i.e. overwrite the original file)

      this.orcsApplicability = orcsApplicability;
      this.branch = branch;
      this.view = view;
      fileExtensionTofeatureStart = new HashMap<>();
      fileExtensionTofeatureEnd = new HashMap<>();

      for (Entry<String, String> entry : fileExtensionToCommentPrefix.entrySet()) {
         String commentPrefix = entry.getValue();
         fileExtensionTofeatureStart.put(entry.getKey(),
            Pattern.compile(commentPrefix + "(Feature\\[([^\\]]+)\\])").matcher(""));
         fileExtensionTofeatureEnd.put(entry.getKey(),
            Pattern.compile(commentPrefix + "End Feature[ \t]*").matcher(""));
      }
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);
      setupMatchers(seq);

      int endMatcherIndex = 0;
      while (startMatcher.find(endMatcherIndex)) {
         String feature = startMatcher.group(2);

         if (endMatcher.find(startMatcher.end())) {
            endMatcherIndex = endMatcher.end();
            ApplicabilityBlock applicBlock =
               orcsApplicability.createApplicabilityBlock(ApplicabilityType.Feature, startMatcher.group(1));
            applicBlock.setInsideText(changeSet.subSequence(startMatcher.end(), endMatcher.start()).toString());

            String replacementText = orcsApplicability.evaluateApplicabilityExpression(applicBlock);
            changeSet.replace(startMatcher.start(), endMatcher.end(), replacementText);
            ruleWasApplicable = true;
         } else {
            throw new OseeCoreException("Didn't find matching End Feature for Feature[%s", feature);
         }
      }
      return changeSet;
   }

   private void setupMatchers(CharSequence seq) {
      String fileExtension = Lib.getExtension(getInputFile().getName());
      startMatcher = fileExtensionTofeatureStart.get(fileExtension).reset(seq);
      endMatcher = fileExtensionTofeatureEnd.get(fileExtension).reset(seq);
   }
}