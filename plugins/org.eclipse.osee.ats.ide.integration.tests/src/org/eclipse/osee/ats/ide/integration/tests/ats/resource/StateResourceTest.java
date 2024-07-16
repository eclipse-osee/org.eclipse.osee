/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.ats.api.demo.DemoActionableItems;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(StateResourceTest.class.getName());
      ActionResult result = AtsApiService.get().getActionService().createAction(null, StateResourceTest.class.getName(),
         "description", ChangeTypes.Improvement, "1", false, null,
         AtsApiService.get().getActionableItemService().getActionableItems(
            Arrays.asList(DemoActionableItems.SAW_Code.getName())),
         new Date(), AtsApiService.get().getUserService().getUserByToken(DemoUsers.Joe_Smith), null, changes);
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
      WebTarget target = AtsApiService.get().jaxRsApi().newTargetNoRedirect("ats/action/state");
      return target.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(Entity.form(form));
   }

   private void validateResponse(Response response, Status status, String errorMessage) throws IOException {
      Assert.assertEquals(status.getStatusCode(), response.getStatus());
      Assert.assertEquals(errorMessage, Lib.inputStreamToString((InputStream) response.getEntity()));
   }
}