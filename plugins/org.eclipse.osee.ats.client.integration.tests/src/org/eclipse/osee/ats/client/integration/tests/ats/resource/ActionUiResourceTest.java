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

import java.util.Collection;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link ActionUiResource}
 * 
 * @author Donald G. Dunne
 */
public class ActionUiResourceTest extends AbstractRestTest {

   @Test
   public void testGet() throws Exception {
      String results = getAndCheckStr("/ats/ui/action");
      Assert.assertTrue(results.contains("ATS UI Resource"));
   }

   @Test
   public void getActionError() throws Exception {
      String html = getAndCheck("/ats/ui/action/ASDF", MediaType.TEXT_HTML_TYPE);
      Assert.assertTrue(html.contains("Action with id(s) [ASDF] can not be found"));
   }

   @Test
   public void getAction() throws Exception {
      Collection<TeamWorkFlowArtifact> wfs = DemoUtil.getSawCommittedWfs();
      TeamWorkFlowArtifact teamWf = wfs.iterator().next();
      String html = getAndCheck("/ats/ui/action/" + teamWf.getAtsId(), MediaType.TEXT_HTML_TYPE);
      Assert.assertTrue(html.contains("Title: <b>" + teamWf.getName() + "</b>"));
   }

   @Test
   public void getActions() throws Exception {
      Collection<TeamWorkFlowArtifact> wfs = DemoUtil.getSawCommittedWfs();
      String atsIds = Collections.toString(",", AtsObjects.toAtsIds(wfs));
      String html = getAndCheck("/ats/ui/action/" + atsIds, MediaType.TEXT_HTML_TYPE);
      Assert.assertTrue(html.contains("$url='/ats/action/" + atsIds + "/details';"));
   }

   @Test
   public void getActionDetails() throws Exception {
      Collection<TeamWorkFlowArtifact> wfs = DemoUtil.getSawCommittedWfs();
      TeamWorkFlowArtifact teamWf = wfs.iterator().next();
      String html = getAndCheck("/ats/ui/action/" + teamWf.getAtsId() + "/details", MediaType.TEXT_HTML_TYPE);
      Assert.assertTrue(html.contains("Artifact Type: <b>" + teamWf.getArtifactTypeName() + "</b>"));
   }

   @Test
   public void getActionDetailsError() throws Exception {
      Collection<TeamWorkFlowArtifact> wfs = DemoUtil.getSawCommittedWfs();
      String atsIds = Collections.toString(",", AtsObjects.toAtsIds(wfs));
      String html = getAndCheck("/ats/ui/action/" + atsIds + "/details", MediaType.TEXT_HTML_TYPE);
      Assert.assertTrue(html.contains("can not be found"));
   }

   @Test
   public void getNewAction() throws Exception {
      String html = getAndCheck("/ats/ui/action/NewAction", MediaType.TEXT_HTML_TYPE);
      Assert.assertTrue(html.contains("ATS - Create new ATS Action"));
   }

   @Test
   public void getSearch() throws Exception {
      String html = getAndCheck("/ats/ui/action/Search", MediaType.TEXT_HTML_TYPE);
      Assert.assertTrue(html.contains("ATS - Search by Id"));
   }

}
