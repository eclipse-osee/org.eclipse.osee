/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.orcs.rest.applic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.rest.model.CommandParameter;
import org.eclipse.osee.orcs.rest.model.CommandsRelatedToContext;
import org.eclipse.osee.orcs.rest.model.GridCommanderEndpoint;
import org.eclipse.osee.orcs.rest.model.UserContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Christopher G. Rebuck
 */
public class GridCommanderEndpointTest {

   private final JaxRsApi jaxRsApi = AtsApiService.get().jaxRsApi();

   private static GridCommanderEndpoint gridCommanderEndpoint;

   @BeforeClass
   public static void testSetup() {
      gridCommanderEndpoint = ServiceUtil.getOseeClient().getGridCommanderEndpoint(CoreBranches.COMMON);
   }

   @Test
   public void testCreateUserContext() {
      TransactionToken testTxToken = gridCommanderEndpoint.createDefaultContext();
      Assert.assertTrue(testTxToken.isValid());
   }

   @Test
   public void testGetUserCommands() throws IOException {
      gridCommanderEndpoint.createDefaultContext();
      String branchId = CoreBranches.COMMON.getIdString();

      String json = jaxRsApi.newTarget("orcs/branch/" + branchId + "/gc/user/commands").request(
         MediaType.APPLICATION_JSON_TYPE).get().readEntity(String.class);

      ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      List<UserContext> contexts = mapper.readValue(json, new TypeReference<List<UserContext>>() {
         //
      });
      Assert.assertEquals(contexts.get(0).getName(), "Default User Context");

      List<CommandsRelatedToContext> commandsRelatedToContext = contexts.get(0).getCommands();
      Assert.assertEquals(commandsRelatedToContext.size(), 5);

      List<CommandsRelatedToContext> testFilteredCommandArray =
         commandsRelatedToContext.stream().filter(command -> command.getName().matches("Open URL")).collect(
            Collectors.toList());
      Assert.assertEquals(testFilteredCommandArray.size(), 1);
      Assert.assertEquals(testFilteredCommandArray.get(0).getName(), "Open URL");

      CommandParameter testParam = testFilteredCommandArray.get(0).getParameter();
      Assert.assertEquals(testParam.getName(), "URL");
      Assert.assertEquals(testParam.getTypeAsString(), "ParameterString");

      CommandsRelatedToContext testCommand =
         commandsRelatedToContext.stream().filter(command -> command.isValid()).filter(
            command -> command.getName().matches("Help")).collect(Collectors.toList()).get(0);
      Assert.assertEquals(testCommand.getParameter().getName(), "Sentinel");

      CommandsRelatedToContext testCommand2 =
         commandsRelatedToContext.stream().filter(command -> command.isValid()).filter(
            command -> command.getName().matches("Find Artifact")).collect(Collectors.toList()).get(0);
      Assert.assertEquals(testCommand2.getAttributes().get("http method"), "GET");

      CommandsRelatedToContext testCommand3 =
         commandsRelatedToContext.stream().filter(command -> command.isValid()).filter(
            command -> command.getName().matches("Hide Column")).collect(Collectors.toList()).get(0);
      CommandParameter testParam2 = testCommand3.getParameter();
      Assert.assertEquals(testParam2.getTypeAsString(), "ParameterMultipleSelect");

   }

}
