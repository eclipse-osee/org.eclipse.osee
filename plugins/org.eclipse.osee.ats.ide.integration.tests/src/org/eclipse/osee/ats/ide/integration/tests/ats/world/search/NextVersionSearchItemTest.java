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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Test;

/**
 * Test Case for {@link NextVersionSearchItem}
 *
 * @author Donald G. Dunne
 */
public class NextVersionSearchItemTest {

   @Test
   public void testByTeam() {
      IAtsTeamDefinition teamDef = TeamDefinitions.getTeamDefinitions(Arrays.asList("SAW SW"),
         AtsClientService.get().getQueryService()).iterator().next();
      NextVersionSearchItem search = new NextVersionSearchItem(teamDef, LoadView.WorldEditor);
      Collection<Artifact> results = search.performSearchGetResults();
      DemoTestUtil.assertTypes(results, 17, IAtsTeamWorkflow.class);
   }

}
