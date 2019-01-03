/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.WorkItemsJsonReader;
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
         AtsClientService.getActionEndpoint().getActionDetails(sawCodeCommittedWf.getIdString());
      Assert.assertTrue(workItems.size() == 1);
   }

   @Test
   public void testGetMultiple() throws Exception {
      String ids = AtsObjects.toIdsString(",", DemoUtil.getSawCommittedTeamWfs());

      Collection<IAtsWorkItem> workItems = AtsClientService.getActionEndpoint().getActionDetails(ids);
      Assert.assertTrue(workItems.size() == 3);
   }

}
