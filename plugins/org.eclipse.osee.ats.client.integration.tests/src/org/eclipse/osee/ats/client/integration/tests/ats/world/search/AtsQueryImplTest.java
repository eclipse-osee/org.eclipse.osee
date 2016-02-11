/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.world.search;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.query.QueryTest;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AtsQueryImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsQueryImplTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();

      IAtsClient client = AtsClientService.get();
      IAtsUser joeSmith = client.getUserService().getUserByName("Joe Smith");
      IAtsQueryService queryService = client.getQueryService();

      Artifact wpArt = (Artifact) AtsClientService.get().getArtifactByName(AtsArtifactTypes.WorkPackage, "Work Pkg 01");
      Conditions.checkNotNull(wpArt, "Work Package");
      IAtsWorkPackage wp = client.getEarnedValueService().getWorkPackage(wpArt);

      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andAssignee(joeSmith);
      query.andAttr(AtsAttributeTypes.WorkPackageGuid, wpArt.getGuid());
      ResultSet<IAtsWorkItem> workItems = query.getResults();

      if (!workItems.isEmpty()) {
         client.getEarnedValueService().removeWorkPackage(wp, workItems.getList());
      }
   }

   @Test
   public void test() {
      QueryTest test = new QueryTest(AtsClientService.get().getServices());
      test.run();
   }

   @Test
   public void testWorkPackage() {
      IAtsClient client = AtsClientService.get();

      IAtsWorkPackage workPackage =
         (IAtsWorkPackage) client.getQueryService().createQuery(AtsArtifactTypes.WorkPackage).andName(
            "Work Pkg 0A").getResults().getAtMostOneOrNull();
      Assert.assertNotNull(workPackage);
   }
}
