/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.skynet.core.transaction;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.WorkItem;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonas Khan
 */
public class SkynetTransactionTest {

   public static final ArtifactToken token =
      ArtifactToken.valueOf(77, "JonasTestBot", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirementMsWord);

   public static long bIDval = 786;
   public static final BranchId bID = BranchId.valueOf(bIDval);

   public static final Artifact artifactA = new Artifact(54L, COMMON);
   public static final Artifact artifactB = new Artifact(55L, COMMON);
   public static final RelationLink link = new RelationLink(artifactA, artifactB, COMMON, WorkItem,
      RelationId.valueOf(77L), GammaId.valueOf(88), "", ModificationType.MODIFIED, ApplicabilityId.BASE);

   @Test
   public void getCheckAccessErrorTest() {
      SkynetTransaction test = new SkynetTransaction(null, DemoBranches.SAW_Bld_1, "");
      String errorMsg = test.getCheckAccessError(token, bID, DemoBranches.SAW_Bld_1);
      Assert.assertTrue(errorMsg, errorMsg.contains(token.getGuid()));
      Assert.assertTrue(errorMsg, errorMsg.contains(bID.getIdString()));
   }

   @Test
   public void getCheckNotHistoricalErrorTest() {
      SkynetTransaction test = new SkynetTransaction(null, DemoBranches.SAW_Bld_1, "");
      String errorMsg = test.getCheckNotHistoricalError(artifactA);
      Assert.assertTrue(errorMsg, errorMsg.contains(artifactA.getGuid()));
   }
}
