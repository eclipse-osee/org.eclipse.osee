/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeEventFilterTest {

   @Test
   public void testArtifactEventFilters_artifactTypeAndInherited() throws Exception {

      ArtifactTypeEventFilter typeFilter = new ArtifactTypeEventFilter(CoreArtifactTypes.Requirement);
      EventBasicGuidArtifact guidArt =
         new EventBasicGuidArtifact(EventModType.Added, COMMON, CoreArtifactTypes.Requirement);
      List<EventBasicGuidArtifact> guidArts = Arrays.asList(guidArt);
      Assert.assertTrue("Should match cause same artifact type", typeFilter.isMatchArtifacts(guidArts));

      // inherited type
      guidArt.setArtTypeGuid(CoreArtifactTypes.SoftwareRequirementMsWord);

      Assert.assertTrue("Should match cause SoftwareRequirement is subclass of Requirement",
         typeFilter.isMatchArtifacts(guidArts));

      // not inherited type
      typeFilter = new ArtifactTypeEventFilter(CoreArtifactTypes.SoftwareRequirementMsWord);
      guidArt.setArtTypeGuid(CoreArtifactTypes.Requirement);

      Assert.assertFalse("Should NOT match cause Requirement is NOT subclass of Software Requirement",
         typeFilter.isMatchArtifacts(guidArts));
   }

   @Test
   public void testBranchMatch_relationType() throws Exception {
      ArtifactTypeEventFilter typeFilter = new ArtifactTypeEventFilter(CoreArtifactTypes.Requirement);

      EventBasicGuidArtifact guidArtA =
         new EventBasicGuidArtifact(EventModType.Added, COMMON, CoreArtifactTypes.Requirement);
      EventBasicGuidArtifact guidArtB =
         new EventBasicGuidArtifact(EventModType.Added, COMMON, CoreArtifactTypes.SoftwareRequirementMsWord);

      List<IBasicGuidRelation> relations = new ArrayList<>();
      EventBasicGuidRelation relation = new EventBasicGuidRelation(RelationEventType.Added, BranchId.SENTINEL,
         CoreRelationTypes.SupportingInfo_IsSupportedBy.getGuid(), 234L, GammaId.valueOf(123), ArtifactId.valueOf(55),
         guidArtA, ArtifactId.valueOf(66), guidArtB);
      relations.add(relation);

      // guidArt in relation matches
      Assert.assertTrue(typeFilter.isMatchRelationArtifacts(relations));

      // no art in relation matches
      guidArtA.setArtTypeGuid(CoreArtifactTypes.AccessControlModel);
      guidArtB.setArtTypeGuid(CoreArtifactTypes.Folder);
      Assert.assertFalse(typeFilter.isMatchRelationArtifacts(relations));
   }

   @Test
   public void testBranchMatch_isMatch() throws Exception {
      ArtifactTypeEventFilter typeFilter = new ArtifactTypeEventFilter(CoreArtifactTypes.Requirement);
      Assert.assertTrue(typeFilter.isMatch(COMMON));
      Assert.assertTrue(typeFilter.isMatch(CoreBranches.SYSTEM_ROOT));
   }
}