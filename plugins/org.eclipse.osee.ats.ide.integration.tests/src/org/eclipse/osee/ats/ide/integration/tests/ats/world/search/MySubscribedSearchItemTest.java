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
package org.eclipse.osee.ats.ide.integration.tests.ats.world.search;

import java.util.Collection;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
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
         new MySubscribedSearchItem("Search", AtsClientService.get().getUserService().getCurrentUser());
      Collection<Artifact> results = search.performSearchGetResults();
      DemoTestUtil.assertTypes(results, 1, IAtsTeamWorkflow.class);
   }

}
