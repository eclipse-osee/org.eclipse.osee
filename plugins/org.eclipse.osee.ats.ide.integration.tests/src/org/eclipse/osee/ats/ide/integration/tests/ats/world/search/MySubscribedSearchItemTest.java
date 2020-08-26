/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.world.search;

import java.util.Collection;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.world.search.MySubscribedSearchItem;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Test;

/**
 * Test case for {@link MySubscribedSearchItem}
 *
 * @author Donald G. Dunne
 */
public class MySubscribedSearchItemTest {

   @Test
   public void search() {
      MySubscribedSearchItem search =
         new MySubscribedSearchItem("Search", AtsApiService.get().getUserService().getCurrentUser());
      Collection<Artifact> results = search.performSearchGetResults();
      DemoTestUtil.assertTypes(results, 1, IAtsTeamWorkflow.class);
   }

}
