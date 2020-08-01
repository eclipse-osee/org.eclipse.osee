/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.config.copy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.config.copy.ConfigData;
import org.eclipse.osee.ats.ide.config.copy.CopyAtsConfigurationOperation;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CopyAtsConfigurationOperationTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest("CISv2");
   }

   @Test
   public void testDoWork() {
      ConfigData data = CopyAtsValidationTest.getConfigData();

      XResultData rd = new XResultData(false);
      CopyAtsConfigurationOperation operation = new CopyAtsConfigurationOperation(data, rd);
      Operations.executeWork(operation);
      Assert.assertFalse(rd.isErrors());

      Collection<IAtsTeamDefinition> teamDefs =
         AtsClientService.get().getTeamDefinitionService().getTeamDefinitionsNameStartsWith("CISv2");
      Assert.assertEquals(5, teamDefs.size());

      Collection<IAtsActionableItem> ais = getActionableItemsNameStartsWith("CISv2");
      Assert.assertEquals(5, ais.size());

   }

   public static Set<IAtsActionableItem> getActionableItemsNameStartsWith(String prefix) {
      Set<IAtsActionableItem> artifacts = new HashSet<>();
      for (IAtsActionableItem aia : AtsClientService.get().getQueryService().createQuery(
         AtsArtifactTypes.ActionableItem).getItems(IAtsActionableItem.class)) {
         if (aia.getName().startsWith(prefix)) {
            artifacts.add(aia);
         }
      }
      return artifacts;
   }

}
