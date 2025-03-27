/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.dispo;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DispoOseeTypes;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stephen Molaro
 */
public class DispoEndpointTest {

   private static BranchToken dispoWorkingBranch;

   private static final String Resolution_Methods =
      "RESOLUTION_METHODS=[\r\n" + "    {\r\n" + "        \"text\": \"\",\r\n" + "        \"value\": \"\",\r\n" + "        \"isDefault\": false\r\n" + "    },\r\n" + "   {\r\n" + "        \"text\": \"Test Script\",\r\n" + "        \"value\": \"Test_Script\",\r\n" + "        \"isDefault\": false\r\n" + "    },\r\n" + "    {\r\n" + "        \"text\": \"Exception_Handling\",\r\n" + "        \"value\": \"Exception_Handling\",\r\n" + "        \"isDefault\": true\r\n" + "    },\r\n" + "    {\r\n" + "        \"text\": \"Analysis\",\r\n" + "        \"value\": \"Analysis\",\r\n" + "        \"isDefault\": false\r\n" + "    },\r\n" + "    {\r\n" + "        \"text\": \"Defensive_Programming\",\r\n" + "        \"value\": \"Defensive_Programming\",\r\n" + "        \"isDefault\": false\r\n" + "    },\r\n" + "    {\r\n" + "        \"text\": \"Modify_Tooling\",\r\n" + "        \"value\": \"Modify_Tooling\",\r\n" + "        \"isDefault\": false\r\n" + "    }\r\n" + "]";

   @Before
   public void setup() {
      BranchToken dispoParentBranch = AtsApiService.get().getBranchService().getBranch(DemoBranches.Dispo_Parent);
      SkynetTransaction txParent = TransactionManager.createTransaction(dispoParentBranch, "Initialize Program Config");

      Artifact programConfigParent =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, dispoParentBranch, "Program Config");
      programConfigParent.addAttributeFromString(CoreAttributeTypes.GeneralStringData, Resolution_Methods);

      programConfigParent.persist(txParent);
      txParent.execute();

      dispoWorkingBranch = AtsApiService.get().getBranchService().getBranch(DemoBranches.Dispo_Demo);
   }

   @Test
   public void testDispoArtifactCreation() {

      SkynetTransaction tx = TransactionManager.createTransaction(dispoWorkingBranch, "Initialize Demo Set and Item");

      List<DispoAnnotationData> annotations = new ArrayList<>();
      DispoAnnotationData annotation1 = new DispoAnnotationData();
      annotation1.setLocationRefs("1");
      annotation1.setResolutionType("Defensive_Programming");
      annotation1.setResolution("Resolution_1");
      annotation1.setIsResolutionValid(true);
      annotation1.setIndex(0);
      annotation1.setCustomerNotes("int i = 1");
      annotations.add(annotation1);

      DispoAnnotationData annotation2 = new DispoAnnotationData();
      annotation2.setLocationRefs("2");
      annotation2.setResolutionType("Defensive_Programming");
      annotation2.setResolution("Resolution_2");
      annotation2.setIsResolutionValid(true);
      annotation2.setIndex(1);
      annotation2.setCustomerNotes("int i = 2");
      annotations.add(annotation2);

      Artifact programConfig =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, dispoWorkingBranch, "Program Config");
      programConfig.addAttributeFromString(CoreAttributeTypes.GeneralStringData, Resolution_Methods);

      Artifact setArt =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.DispositionSet, dispoWorkingBranch, "Dispo_Demo_Set");

      setArt.addAttributeFromString(DispoOseeTypes.CoverageConfig, "codeCoverage");
      setArt.setSoleAttributeFromString(CoreAttributeTypes.CoverageImportPath, "//path/to/vcast");

      Artifact itemArt =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.DispositionableItem, dispoWorkingBranch, "Dispo_Demo_Item");

      itemArt.setSoleAttributeFromString(CoreAttributeTypes.CoverageStatus, "INCOMPLETE");
      itemArt.setSoleAttributeFromString(CoreAttributeTypes.CoverageAnnotationsJson, JsonUtil.toJson(annotations));
      itemArt.setSoleAttributeFromString(DispoOseeTypes.DispoItemVersion, "VER1");
      itemArt.addAttribute(CoreAttributeTypes.CoverageAssignee, "Sentinel");

      setArt.addChild(itemArt);

      programConfig.persist(tx);
      setArt.persist(tx);
      itemArt.persist(tx);

      tx.execute();
   }
}