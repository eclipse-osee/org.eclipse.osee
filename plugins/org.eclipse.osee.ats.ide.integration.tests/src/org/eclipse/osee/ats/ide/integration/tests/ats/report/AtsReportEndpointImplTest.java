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

package org.eclipse.osee.ats.ide.integration.tests.ats.report;

import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.report.BuildMemoRequest;
import org.eclipse.osee.ats.api.report.BuildMemoRequest.BuildMemoCommit;
import org.eclipse.osee.ats.api.report.BuildMemoRequest.BuildMemoRepository;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link AtsReportEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsReportEndpointImplTest extends AbstractRestTest {

   @Test
   public void testGetBuildMemo() {
      AtsApiIde atsApi = AtsApiService.get();

      BuildMemoRequest request = new BuildMemoRequest();
      request.setTitle("Test Build Memo");
      request.setFromTag("v1.0.0");
      request.setToTag("v2.0.0");
      request.setAllowedCommitTypes(Arrays.asList("FEATURE", "BUG"));

      BuildMemoRepository repo = new BuildMemoRepository();
      repo.setName("test-repo");
      repo.setFromTag("v1.0.0");
      repo.setToTag("v2.0.0");
      request.setRepositories(Arrays.asList(repo));

      BuildMemoCommit featureCommit = new BuildMemoCommit();
      featureCommit.setType("FEATURE");
      featureCommit.setId("FEAT-001");
      featureCommit.setTitle("Add new feature");
      featureCommit.setMessage("Implemented new feature");
      featureCommit.setRepository("test-repo");

      BuildMemoCommit bugCommit = new BuildMemoCommit();
      bugCommit.setType("BUG");
      bugCommit.setId("BUG-042");
      bugCommit.setTitle("Fix null pointer");
      bugCommit.setMessage("Fixed NPE in handler");
      bugCommit.setRepository("test-repo");

      BuildMemoCommit filteredCommit = new BuildMemoCommit();
      filteredCommit.setType("REFACTOR");
      filteredCommit.setId("REF-099");
      filteredCommit.setTitle("Cleanup code");
      filteredCommit.setMessage("Removed dead code");
      filteredCommit.setRepository("test-repo");

      request.setCommits(Arrays.asList(featureCommit, bugCommit, filteredCommit));

      String html = atsApi.getServerEndpoints().getReportEp().getBuildMemo(request);

      Assert.assertTrue(html.contains("Test Build Memo"));
      Assert.assertTrue(html.contains("test-repo"));
      Assert.assertTrue(html.contains("v1.0.0"));
      Assert.assertTrue(html.contains("v2.0.0"));
      Assert.assertTrue(html.contains("FEAT-001"));
      Assert.assertTrue(html.contains("Add new feature"));
      Assert.assertTrue(html.contains("BUG-042"));
      Assert.assertTrue(html.contains("Fix null pointer"));
      Assert.assertFalse("REFACTOR commits should be filtered out", html.contains("REF-099"));
      Assert.assertFalse("REFACTOR commits should be filtered out", html.contains("Cleanup code"));
      Assert.assertTrue("Should contain Expand All button", html.contains("Expand All"));
      Assert.assertTrue("Should contain toggleAll function", html.contains("toggleAll"));
   }

   @Test
   public void testGetAttrDiffReport() throws Exception {
      AtsApiIde atsApi = AtsApiService.get();
      Date today = new Date();
      Date lastWeek = DateUtil.addWeeks(today, -1);
      // ats/report/AttrDiffReport?date=2025-07-16&artTypeId=80&attrTypeIds=4689644240272725681
      String attrDiffReportHtml =
         atsApi.getServerEndpoints().getReportEp().getAttrDiffReport(DateUtil.get(lastWeek, DateUtil.YYYY_MM_DD),
            AtsArtifactTypes.DemoReqTeamWorkflow.getIdString(), AtsAttributeTypes.CurrentStateName.getIdString());
      Assert.assertTrue(attrDiffReportHtml.contains("55313463"));
      Assert.assertTrue(attrDiffReportHtml.contains(DemoUsers.Joe_Smith.getName()));
   }

}