/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.List;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
@Ignore
public class LoadDeletedRelationTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private Artifact left, right;
   private RelationTypeToken type;

   @Before
   public void setUp() {
      left = TestUtil.createSimpleArtifact(CoreArtifactTypes.Requirement, "Left", SAW_Bld_2);
      right = TestUtil.createSimpleArtifact(CoreArtifactTypes.Requirement, "Right", SAW_Bld_2);
      left.persist(getClass().getSimpleName());
      right.persist(getClass().getSimpleName());
      type = CoreRelationTypes.RequirementTrace_HigherLevelRequirement;
   }

   //not implemented  in the code
   @Ignore
   @Test
   public void loadDeletedRelationTest() {
      RelationManager.addRelation(type, left, right, "");
      left.persist(getClass().getSimpleName());
      RelationLink loaded = RelationManager.getLoadedRelation(type, left, right, SAW_Bld_2);
      GammaId oldGammaId = loaded.getGammaId();
      RelationManager.deleteRelation(type, left, right);
      left.persist(getClass().getSimpleName());
      RelationManager.addRelation(type, left, right, "");
      left.persist(getClass().getSimpleName());

      List<RelationLink> links = RelationManager.getRelationsAll(left, DeletionFlag.INCLUDE_DELETED);
      int linkCount = 0;
      for (RelationLink link : links) {
         if (link.getRelationType().equals(type)) {
            linkCount++;
         }
      }

      GammaId newGammaId = loaded.getGammaId();
      assertEquals("Deleted relation was not re-used by addRelation; see L3778", 1, linkCount);
      assertFalse(loaded.isDeleted());
      assertEquals("Gamma ID was changed;", oldGammaId.getId(), newGammaId.getId());
   }

   @After
   public void tearDown() {
      left.purgeFromBranch();
      right.purgeFromBranch();
   }
}
