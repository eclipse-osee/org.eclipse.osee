/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.admin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * Replaces the old broken process links with the attribute and artifact IDs
 *
 * @author Ryan D. Brooks
 * @author Morgan E. Cook
 */
public class UpdateLinksRule extends Rule {

   private final static Pattern generalPattern = Pattern.compile("<a href=\"http://osee[^\"]+");
   private final static Pattern precisePattern = Pattern.compile(".+?guid=([^&]+)(?:&|&amp;)branchUuid=(\\d+)");
   private final static Pattern revPrecisePattern = Pattern.compile(".+?branch.uid=(\\d+)(?:&|&amp;)guid=(.+)");
   private final OrcsApi orcsApi;

   public UpdateLinksRule(OrcsApi orcsApi) {
      super("html");
      this.orcsApi = orcsApi;
   }

   private String generateLink(String branchIdString, String artifactGuid) {
      BranchId branchId = BranchId.valueOf(branchIdString);
      artifactGuid = artifactGuid.replaceAll("%2[Bb]", "+");
      artifactGuid = artifactGuid.replaceAll("%3[Dd]", "=");

      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branchId).andGuid(artifactGuid);
      ArtifactReadable artifact = query.getResults().getExactlyOne();
      Long artifactId = artifact.getUuid();

      Long attributeId = getAttributeId(artifact);

      return String.format("<a href=\"http://osee.msc.az.boeing.com/orcs/branch/%s/artifact/%s/attribute/%s", branchId,
         artifactId, attributeId);
   }

   private Long getAttributeId(ArtifactReadable artifact) {
      AttributeTypeToken attributeType;

      if (artifact.isOfType(CoreArtifactTypes.MsWholeWordDocument)) {
         attributeType = CoreAttributeTypes.WholeWordContent;

      } else if (artifact.isOfType(CoreArtifactTypes.NativeArtifact)) {
         attributeType = CoreAttributeTypes.NativeContent;
      } else {
         throw new RuntimeException("Unexpected Artifact type: " + artifact.getArtifactType());
      }

      return artifact.getSoleAttributeId(attributeType);
   }

   private void applyTextSwap(ChangeSet set, Matcher matcher, String branchIdString, String artifactGuid) {
      ruleWasApplicable = true;
      String link = generateLink(branchIdString, artifactGuid);
      set.replace(matcher.start(), matcher.end(), link);
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);
      Matcher generalMatcher = generalPattern.matcher(seq);

      while (generalMatcher.find()) {
         Matcher preciseMatcher = precisePattern.matcher(generalMatcher.group());
         Matcher revPreciseMatcher = revPrecisePattern.matcher(generalMatcher.group());

         if (preciseMatcher.find()) {
            applyTextSwap(changeSet, generalMatcher, preciseMatcher.group(2), preciseMatcher.group(1));
         } else if (revPreciseMatcher.find()) {
            applyTextSwap(changeSet, generalMatcher, revPreciseMatcher.group(1), revPreciseMatcher.group(2));
         } else {
            throw new RuntimeException("Unexpected Link Format: " + generalMatcher.group());
         }
      }

      return changeSet;
   }
}