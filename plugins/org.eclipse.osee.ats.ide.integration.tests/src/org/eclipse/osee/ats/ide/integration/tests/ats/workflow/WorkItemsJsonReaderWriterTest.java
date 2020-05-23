/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workflow.util.WorkItemsJsonReader;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link WorkItemsJsonReader and @link WorkItemsJsonWriter}
 *
 * @author Donald G. Dunne
 */
public class WorkItemsJsonReaderWriterTest {

   @Test
   public void testGetSingle() throws Exception {
      IAtsTeamWorkflow sawCodeCommittedWf = DemoUtil.getSawCodeCommittedWf();

      Collection<IAtsWorkItem> workItems =
         AtsClientService.get().getServerEndpoints().getActionEndpoint().getActionDetails(
            sawCodeCommittedWf.getIdString());
      Assert.assertTrue(workItems.size() == 1);
   }

   @Test
   public void testGetMultiple() throws Exception {
      String ids = AtsObjects.toIdsString(",", DemoUtil.getSawCommittedTeamWfs());

      Collection<IAtsWorkItem> workItems =
         AtsClientService.get().getServerEndpoints().getActionEndpoint().getActionDetails(ids);
      Assert.assertTrue(workItems.size() == 3);
   }

}
