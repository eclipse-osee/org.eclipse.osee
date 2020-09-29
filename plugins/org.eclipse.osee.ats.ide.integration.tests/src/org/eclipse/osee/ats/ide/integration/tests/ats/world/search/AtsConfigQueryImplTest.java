/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.world.search;

import static org.junit.Assert.assertEquals;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.Csci;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.query.IAtsConfigQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for AtsConfigQueryImpl
 *
 * @author Donald G. Dunne
 */
public class AtsConfigQueryImplTest {

   private static IAtsQueryService queryService;

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();

      AtsApiIde client = AtsApiService.get();
      queryService = client.getQueryService();

   }

   @Test
   public void test() {
      IAtsConfigQuery query = queryService.createQuery(AtsArtifactTypes.TeamDefinition);
      ResultSet<IAtsTeamDefinition> teamDefs = query.getConfigObjectResultSet();
      assertEquals(27, teamDefs.size());

      Collection<ArtifactToken> resultArtifacts = query.getArtifacts();
      assertEquals(27, resultArtifacts.size());

      query.andAttr(CoreAttributeTypes.Name, DemoArtifactToken.SAW_Code.getName());
      IAtsTeamDefinition teamDef =
         (IAtsTeamDefinition) query.getConfigObjectResultSet().getOneOrDefault(IAtsTeamDefinition.SENTINEL);

      Conditions.assertNotSentinel(teamDef);
      assertEquals(DemoArtifactToken.SAW_Code.getName(), teamDef.getName());

      query = queryService.createQuery(AtsArtifactTypes.ActionableItem);
      ResultSet<IAtsActionableItem> ais = query.getConfigObjectResultSet();

      assertEquals(70, ais.size());
   }

   @Test
   public void testWorkType() {
      IAtsConfigQuery query = queryService.createQuery(AtsArtifactTypes.TeamDefinition).andWorkType(WorkType.Code);
      ResultSet<IAtsTeamDefinition> teamDefs = query.getConfigObjectResultSet();
      assertEquals(2, teamDefs.size());

      query = queryService.createQuery(AtsArtifactTypes.TeamDefinition).andWorkType(WorkType.Code, WorkType.Test);
      teamDefs = query.getConfigObjectResultSet();
      assertEquals(4, teamDefs.size());
   }

   @Test
   public void testCsci() {
      IAtsConfigQuery query =
         queryService.createQuery(AtsArtifactTypes.TeamDefinition).andWorkType(WorkType.Code, WorkType.Test).andCsci(
            Collections.singleton(Csci.SP.name()));
      ResultSet<IAtsTeamDefinition> teamDefs = query.getConfigObjectResultSet();
      assertEquals(2, teamDefs.size());

      query = queryService.createQuery(AtsArtifactTypes.TeamDefinition).andCsci(Collections.singleton(Csci.SP.name()));
      teamDefs = query.getConfigObjectResultSet();
      assertEquals(3, teamDefs.size());
   }

   @Test
   public void testProgram() {
      IAtsConfigQuery query =
         queryService.createQuery(AtsArtifactTypes.TeamDefinition).andProgram(DemoArtifactToken.SAW_Program.getId());
      ResultSet<IAtsTeamDefinition> teamDefs = query.getConfigObjectResultSet();
      assertEquals(8, teamDefs.size());

   }

}
