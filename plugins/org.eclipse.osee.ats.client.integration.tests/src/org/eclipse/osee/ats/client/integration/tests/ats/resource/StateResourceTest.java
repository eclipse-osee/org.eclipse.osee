/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.demo.api.DemoActionableItems;
import org.eclipse.osee.ats.demo.api.DemoUsers;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link StateResource}
 *
 * @author Donald G. Dunne
 */
public class StateResourceTest extends AbstractRestTest {

   @After
   public void tearDown() throws Exception {
      AtsTestUtil.cleanupSimpleTest(getClass().getSimpleName());
   }

   @Test
   public void testCreateAction() throws Exception {

      Form form = new Form();
      postAndValidateResponse("id is not valid", Status.BAD_REQUEST, form);

      form.param("atsId", "InvalidId");
      postAndValidateResponse("operation is not valid", Status.BAD_REQUEST, form);

      form.param("operation", "transition");
      postAndValidateResponse("toState is not valid", Status.BAD_REQUEST, form);

      form.param("toState", "Analyze");
      postAndValidateResponse("asUserId is not valid", Status.BAD_REQUEST, form);

      form.param("asUserId", "Joe Wrong");
      postAndValidateResponse("User by id [Joe Wrong] does not exist", Status.BAD_REQUEST, form);

      form.asMap().remove("asUserId");
      form.param("asUserId", "3333");
      postAndValidateResponse("Action by id [InvalidId] does not exist", Status.BAD_REQUEST, form);

      form.asMap().remove("operation");
      form.param("operation", "wrongOperation");
      postAndValidateResponse("Unhandled operation [wrongOperation]", Status.BAD_REQUEST, form);
      form.asMap().remove("operation");
      form.param("operation", "transition");

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(StateResourceTest.class.getName());
      ActionResult result = AtsClientService.get().getActionFactory().createAction(null,
         StateResourceTest.class.getName(), "description", ChangeType.Improvement, "1", false, null,
         ActionableItems.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Code.getName()),
            AtsClientService.get()),
         new Date(), AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith), null,
         changes);
      TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) result.getFirstTeam().getStoreObject();
      changes.execute();
      Assert.assertEquals("Endorse", teamWf.getCurrentStateName());

      form.asMap().remove("atsId");
      form.param("atsId", teamWf.getAtsId());

      Response response = post(form);

      Assert.assertEquals(Status.SEE_OTHER.getStatusCode(), response.getStatus());
      String urlStr = response.getLocation().toString();
      URL url = new URL(urlStr);
      String path = url.getPath();
      Assert.assertTrue(String.format("Invalid url [%s]", url), path.endsWith("/ats/ui/action/" + teamWf.getAtsId()));

      teamWf.reloadAttributesAndRelations();
      Assert.assertEquals("Analyze", teamWf.getCurrentStateName());
   }

   private void postAndValidateResponse(String errorMessage, Status status, Form form) throws IOException {
      Response response = post(form);
      validateResponse(response, status, errorMessage);
   }

   private Response post(Form form) {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri = UriBuilder.fromUri(appServer).path("/ats/action/state").build();
      Response response = JaxRsClient.newBuilder().followRedirects(false).build().target(uri).request(
         MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(Entity.form(form));
      return response;
   }

   private void validateResponse(Response response, Status status, String errorMessage) throws IOException {
      Assert.assertEquals(status.getStatusCode(), response.getStatus());
      Assert.assertEquals(errorMessage, Lib.inputStreamToString((InputStream) response.getEntity()));
   }
}
