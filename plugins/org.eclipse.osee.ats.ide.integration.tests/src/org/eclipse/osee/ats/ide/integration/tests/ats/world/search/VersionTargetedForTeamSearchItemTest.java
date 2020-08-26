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
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Test;

/**
 * Test Case for {@link VersionTargetedForTeamSearchItem}
 *
 * @author Donald G. Dunne
 */
public class VersionTargetedForTeamSearchItemTest {

   @Test
   public void testByVersion() {
      IAtsVersion version = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_2);
      VersionTargetedForTeamSearchItem search =
         new VersionTargetedForTeamSearchItem(null, version, false, LoadView.WorldEditor);
      Collection<Artifact> results = search.performSearchGetResults();
      DemoTestUtil.assertTypes(results, 17, IAtsTeamWorkflow.class);
   }

}
