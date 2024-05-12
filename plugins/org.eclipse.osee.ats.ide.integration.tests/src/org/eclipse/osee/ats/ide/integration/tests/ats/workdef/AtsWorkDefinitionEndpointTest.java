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

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionEndpointTest extends AbstractRestTest {

   @Test
   public void testAllWorkDefs() {
      String url = "ats/workdef";
      JsonNode jsonNode = getJsonNode(url);
      Assert.assertEquals(21, jsonNode.size());
   }

   @Test
   public void testWorkDefByWorkflow() {
      String url = "ats/workdef/" + DemoUtil.getSawCodeCommittedWf().getIdString();
      JsonNode jsonNode = getJsonNode(url);
      Assert.assertEquals(jsonNode.get("name").asText(), DemoWorkDefinitions.WorkDef_Team_Demo_Code.getName());
   }

   @Test
   public void testWorkDefByTeamDef() {
      String url = "ats/workdef/teamdef/" + DemoArtifactToken.SAW_Code.getIdString();
      JsonNode workDefNode = getJsonNode(url);
      Assert.assertEquals(workDefNode.get("name").asText(), DemoWorkDefinitions.WorkDef_Team_Demo_Code.getName());
   }

}
