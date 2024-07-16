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
package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.workflow.AtsActionUiEndpointApi;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link AtsActionUiEndpoint}
 *
 * @author Donald G. Dunne
 */
public class AtsActionUiEndpointTest extends AbstractRestTest {

   @Test
   public void testGet() throws Exception {
      String results = getHtml("/ats/ui/action");
      Assert.assertTrue(results.contains("ATS UI Endpoint"));
   }

   @Test
   public void testGetViaEndpoint() throws Exception {
      AtsActionUiEndpointApi actionUiEndpoint = AtsApiService.get().getServerEndpoints().getActionUiEndpoint();
      String html = actionUiEndpoint.get();
      Assert.assertTrue(html.contains("ATS UI Endpoint"));
   }

   @Test
   public void getActionError() throws Exception {
      String html = getHtml("/ats/ui/action/ASDF");
      Assert.assertTrue(html.contains("Action with id(s) [ASDF] can not be found"));
   }

   @Test
   public void getAction() throws Exception {
      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      String html = getHtml("/ats/ui/action/" + teamWf.getAtsId());
      Assert.assertTrue(html.contains("Title: <b>" + teamWf.getName() + "</b>"));
   }

   @Test
   public void getActions() throws Exception {
      Collection<TeamWorkFlowArtifact> wfs =
         Arrays.asList(DemoUtil.getSawCodeCommittedWf(), DemoUtil.getSawTestCommittedWf());
      String atsIds = Collections.toString(",", AtsObjects.toAtsIds(wfs));
      String html = getHtml("/ats/ui/action/" + atsIds);
      Assert.assertTrue(html.contains("$url='/ats/action/" + atsIds + "/details';"));
   }

   @Test
   public void getActionDetails() throws Exception {
      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      String html = getHtml("/ats/ui/action/" + teamWf.getAtsId() + "/details");
      Assert.assertTrue(html.contains("Artifact Type: <b>" + teamWf.getArtifactTypeName() + "</b>"));
   }

   @Test
   public void getActionDetailsError() throws Exception {
      String html = getHtml("/ats/ui/action/" + DemoUtil.getSawAtsIdsStr() + "/details");
      Assert.assertTrue(html.contains("can not be found"));
   }

   @Test
   public void getNewAction() throws Exception {
      String html = getHtml("/ats/ui/action/NewAction");
      Assert.assertTrue(html.contains("ATS - Create new ATS Action"));
   }

   @Test
   public void getSearch() throws Exception {
      String html = getHtml("/ats/ui/action/Search");
      Assert.assertTrue(html.contains("ATS - Search by Id"));
   }

   @Test
   public void getTransition() throws Exception {
      String html = getHtml("/ats/ui/action/" + DemoUtil.getSawCodeCommittedWf().getAtsId() + "/Transition");
      Assert.assertTrue(html.contains(
         "<input type=\"hidden\" name=\"atsId\" value=\"" + DemoUtil.getSawCodeCommittedWf().getAtsId() + "\">"));
      Matcher m = Pattern.compile(".*form action=\"\\/ats\\/action\\/state\" method=\"post\">").matcher(html);
      Assert.assertTrue(m.find());
      for (String stateName : Arrays.asList("Cancelled", "Completed", "Analyze", "Authorize")) {
         Assert.assertTrue("html should contain state name " + stateName,
            html.contains("<option value=\"" + stateName + "\" id=\"" + stateName + "\">"));
      }
   }

}
