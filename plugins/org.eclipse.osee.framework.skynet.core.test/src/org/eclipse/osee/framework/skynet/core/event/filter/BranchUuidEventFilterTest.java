/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.skynet.core.event.filter;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Requirement;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SoftwareRequirementMsWord;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link BranchIdEventFilter}
 *
 * @author Donald G. Dunne
 */
public class BranchUuidEventFilterTest {

   @Test
   public void testIsMatch() {
      BranchIdEventFilter branchFilter = new BranchIdEventFilter(COMMON);
      Assert.assertTrue(branchFilter.isMatch(COMMON));
      Assert.assertFalse(branchFilter.isMatch(SYSTEM_ROOT));
   }

   @Test
   public void testIsMatchArtifacts() {
      BranchIdEventFilter branchFilter = new BranchIdEventFilter(COMMON);

      EventBasicGuidArtifact guidArtA = new EventBasicGuidArtifact(EventModType.Added, BranchId.create(), Requirement);
      EventBasicGuidArtifact guidArtB =
         new EventBasicGuidArtifact(EventModType.Added, BranchId.create(), SoftwareRequirementMsWord);
      List<DefaultBasicGuidArtifact> arts = new ArrayList<>();
      arts.add(guidArtB);
      arts.add(guidArtA);

      Assert.assertFalse(branchFilter.isMatchArtifacts(arts));

      guidArtA = new EventBasicGuidArtifact(EventModType.Added, COMMON, Requirement);
      arts.clear();
      arts.add(guidArtB);
      arts.add(guidArtA);

      Assert.assertTrue(branchFilter.isMatchArtifacts(arts));
   }

   @Test
   public void testIsMatchRelationArtifacts() {
      BranchIdEventFilter branchFilter = new BranchIdEventFilter(COMMON);

      EventBasicGuidArtifact guidArtA = new EventBasicGuidArtifact(EventModType.Added, BranchId.create(), Requirement);
      EventBasicGuidArtifact guidArtB =
         new EventBasicGuidArtifact(EventModType.Added, BranchId.create(), SoftwareRequirementMsWord);

      List<IBasicGuidRelation> relations = new ArrayList<>();
      EventBasicGuidRelation relation = new EventBasicGuidRelation(RelationEventType.Added, BranchId.SENTINEL,
         CoreRelationTypes.SupportingInfo_IsSupportedBy.getGuid(), 234L, GammaId.valueOf(123), 55, guidArtA, 66,
         guidArtB);
      relations.add(relation);

      // neither in relation matches common branch
      Assert.assertFalse(branchFilter.isMatchRelationArtifacts(relations));

      guidArtA = new EventBasicGuidArtifact(EventModType.Added, COMMON, Requirement);
      guidArtB = new EventBasicGuidArtifact(EventModType.Added, COMMON, SoftwareRequirementMsWord);

      relations.clear();
      relation = new EventBasicGuidRelation(RelationEventType.Added, COMMON,
         CoreRelationTypes.SupportingInfo_IsSupportedBy.getGuid(), 234L, GammaId.valueOf(123), 55, guidArtA, 66,
         guidArtB);
      relations.add(relation);

      // branch match
      Assert.assertTrue(branchFilter.isMatchRelationArtifacts(relations));
   }

}
