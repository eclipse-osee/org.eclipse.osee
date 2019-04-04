/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.transaction;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonas Khan
 */

public class SkynetTransactionTest {

   public static final ArtifactToken token =
      ArtifactToken.valueOf(77, "JonasTestBot", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirement);

   public static long bIDval = 786;
   public static final BranchId bID = BranchId.valueOf(bIDval);

   public static final Artifact artifactA = new Artifact(54L, COMMON);
   public static final Artifact artifactB = new Artifact(55L, COMMON);

   public static final RelationType type =
      new RelationType(0x00L, "type name", artifactA.getName(), artifactB.getName(), CoreArtifactTypes.Artifact,
         CoreArtifactTypes.Artifact, RelationTypeMultiplicity.MANY_TO_MANY, null);

   public static final RelationLink link = new RelationLink(artifactA, artifactB, COMMON, type, 77, GammaId.valueOf(88),
      "", ModificationType.MODIFIED, ApplicabilityId.BASE);

   @Test
   public void getCheckAccessErrorTest() {
      SkynetTransaction test = new SkynetTransaction(null, DemoBranches.SAW_Bld_1, "");
      String errorMsg = test.getCheckAccessError(token, bID, DemoBranches.SAW_Bld_1);
      Assert.assertTrue(errorMsg, errorMsg.contains(token.getGuid()));
      Assert.assertTrue(errorMsg, errorMsg.contains(bID.getIdString()));
   }

   @Test
   public void getCheckBranchArtifactErrorTest() {
      SkynetTransaction test = new SkynetTransaction(null, DemoBranches.SAW_Bld_1, "");
      String errorMsg = test.getCheckBranchError(token, DemoBranches.SAW_Bld_1);
      Assert.assertTrue(errorMsg, errorMsg.contains(token.getIdString()));
      Assert.assertTrue(errorMsg, errorMsg.contains(token.getName()));
      Assert.assertTrue(errorMsg, errorMsg.contains(DemoBranches.SAW_Bld_1.getIdString()));
      Assert.assertTrue(errorMsg, errorMsg.contains(DemoBranches.SAW_Bld_1.getName()));
   }

   @Test
   public void getCheckBranchRelationErrorTest() {
      SkynetTransaction test = new SkynetTransaction(null, DemoBranches.SAW_Bld_1, "");
      String errorMsg = test.getCheckBranchError(link, DemoBranches.SAW_Bld_1);
      Assert.assertTrue(errorMsg, errorMsg.contains(artifactA.getIdString()));
      Assert.assertTrue(errorMsg, errorMsg.contains(artifactB.getIdString()));
      Assert.assertTrue(errorMsg, errorMsg.contains(DemoBranches.SAW_Bld_1.getIdString()));
      Assert.assertTrue(errorMsg, errorMsg.contains(DemoBranches.SAW_Bld_1.getName()));
   }

   @Test
   public void getCheckNotHistoricalErrorTest() {
      SkynetTransaction test = new SkynetTransaction(null, DemoBranches.SAW_Bld_1, "");
      String errorMsg = test.getCheckNotHistoricalError(artifactA);
      Assert.assertTrue(errorMsg, errorMsg.contains(artifactA.getGuid()));
   }
}
