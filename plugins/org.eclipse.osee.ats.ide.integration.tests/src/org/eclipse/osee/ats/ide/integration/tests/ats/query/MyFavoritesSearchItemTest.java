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

package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import java.util.Collection;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.world.search.MyFavoritesSearchItem;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Test;

/**
 * Test case for {@link MyFavoritesSearchItem}
 *
 * @author Donald G. Dunne
 */
public class MyFavoritesSearchItemTest {

   @Test
   public void search() {
      MyFavoritesSearchItem search =
         new MyFavoritesSearchItem("Search", AtsApiService.get().getUserService().getCurrentUser());
      Collection<Artifact> results = search.performSearchGetResults();
      DemoTestUtil.assertTypes(results, 3, IAtsTeamWorkflow.class);
   }

}
