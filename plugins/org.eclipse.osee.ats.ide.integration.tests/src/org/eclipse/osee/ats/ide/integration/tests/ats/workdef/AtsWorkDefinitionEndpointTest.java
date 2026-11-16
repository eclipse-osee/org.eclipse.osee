/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workdef;

import java.util.Collection;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionEndpointTest extends AbstractRestTest {

   @Test
   public void testAllWorkDefs() {
      Collection<WorkDefinition> workDefinitions = AtsApiService.get().getServerEndpoints().getWorkDefEp().get();
      Assert.assertEquals(31, workDefinitions.size());
   }

   @Test
   public void testWorkDefByWorkflow() {
      WorkDefinition workDefinition = AtsApiService.get().getServerEndpoints().getWorkDefEp().getWorkDef(
         DemoUtil.getSawCodeCommittedWf().getIdString());
      Assert.assertEquals(DemoWorkDefinitions.WorkDefTeamDemoCode.getName(), workDefinition.getName());
   }

   @Test
   public void testWorkDefByTeamDef() {
      WorkDefinition workDefinition = AtsApiService.get().getServerEndpoints().getWorkDefEp().getWorkDefForTeam(
         DemoArtifactToken.SAW_Code.getIdString());
      Assert.assertEquals(DemoWorkDefinitions.WorkDefTeamDemoCode.getName(), workDefinition.getName());
   }

}
