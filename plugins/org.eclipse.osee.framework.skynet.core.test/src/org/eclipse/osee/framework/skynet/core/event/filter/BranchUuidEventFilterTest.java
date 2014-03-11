/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event.filter;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link BranchUuidEventFilter}
 * 
 * @author Donald G. Dunne
 */
public class BranchUuidEventFilterTest {

   @Test
   public void testIsMatch() {
      BranchUuidEventFilter branchFilter = new BranchUuidEventFilter(CoreBranches.COMMON);
      Assert.assertTrue(branchFilter.isMatch(CoreBranches.COMMON.getUuid()));
      Assert.assertFalse(branchFilter.isMatch(23444235235324L));
   }

   @Test
   public void testIsMatchArtifacts() {
      BranchUuidEventFilter branchFilter = new BranchUuidEventFilter(CoreBranches.COMMON);

      EventBasicGuidArtifact guidArtA =
         new EventBasicGuidArtifact(EventModType.Added, Lib.generateUuid(), CoreArtifactTypes.Requirement.getGuid(),
            GUID.create());
      EventBasicGuidArtifact guidArtB =
         new EventBasicGuidArtifact(EventModType.Added, Lib.generateUuid(),
            CoreArtifactTypes.SoftwareRequirement.getGuid(), GUID.create());
      List<IBasicGuidArtifact> arts = new ArrayList<IBasicGuidArtifact>();
      arts.add(guidArtB);
      arts.add(guidArtA);

      Assert.assertFalse(branchFilter.isMatchArtifacts(arts));

      guidArtA =
         new EventBasicGuidArtifact(EventModType.Added, CoreBranches.COMMON.getUuid(),
            CoreArtifactTypes.Requirement.getGuid(), GUID.create());
      arts.clear();
      arts.add(guidArtB);
      arts.add(guidArtA);

      Assert.assertTrue(branchFilter.isMatchArtifacts(arts));
   }

   @Test
   public void testIsMatchRelationArtifacts() {
      BranchUuidEventFilter branchFilter = new BranchUuidEventFilter(CoreBranches.COMMON);

      EventBasicGuidArtifact guidArtA =
         new EventBasicGuidArtifact(EventModType.Added, Lib.generateUuid(), CoreArtifactTypes.Requirement.getGuid(),
            GUID.create());
      EventBasicGuidArtifact guidArtB =
         new EventBasicGuidArtifact(EventModType.Added, Lib.generateUuid(),
            CoreArtifactTypes.SoftwareRequirement.getGuid(), GUID.create());

      List<IBasicGuidRelation> relations = new ArrayList<IBasicGuidRelation>();
      EventBasicGuidRelation relation =
         new EventBasicGuidRelation(RelationEventType.Added, Lib.generateUuid(),
            CoreRelationTypes.SupportingInfo_SupportedBy.getGuid(), 234, 123, 55, guidArtA, 66, guidArtB);
      relations.add(relation);

      // neither in relation matches common branch
      Assert.assertFalse(branchFilter.isMatchRelationArtifacts(relations));

      guidArtA =
         new EventBasicGuidArtifact(EventModType.Added, CoreBranches.COMMON.getUuid(),
            CoreArtifactTypes.Requirement.getGuid(), GUID.create());
      guidArtB =
         new EventBasicGuidArtifact(EventModType.Added, CoreBranches.COMMON.getUuid(),
            CoreArtifactTypes.SoftwareRequirement.getGuid(), GUID.create());

      relations.clear();
      relation =
         new EventBasicGuidRelation(RelationEventType.Added, CoreBranches.COMMON.getUuid(),
            CoreRelationTypes.SupportingInfo_SupportedBy.getGuid(), 234, 123, 55, guidArtA, 66, guidArtB);
      relations.add(relation);

      // branch match
      Assert.assertTrue(branchFilter.isMatchRelationArtifacts(relations));
   }

}
