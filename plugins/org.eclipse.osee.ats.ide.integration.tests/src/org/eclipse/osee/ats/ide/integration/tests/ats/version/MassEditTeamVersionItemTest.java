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

package org.eclipse.osee.ats.ide.integration.tests.ats.version;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.config.version.MassEditTeamVersionItem;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link MassEditTeamVersionItem}
 *
 * @author Donald G. Dunne
 */
public class MassEditTeamVersionItemTest {

   @Test
   public void test() {
      IAtsTeamDefinition teamDef =
         AtsApiService.get().getTeamDefinitionService().getTeamDefinitions(Arrays.asList("SAW SW")).iterator().next();

      MassEditTeamVersionItem search = new MassEditTeamVersionItem("Search", AtsImage.ACTION);
      search.setSelectedTeamDef(teamDef);
      Collection<Artifact> results = search.getResults();
      Assert.assertEquals(3, results.size());
      List<String> versionNames = Arrays.asList(DemoBranches.SAW_Bld_1.getName(), DemoBranches.SAW_Bld_2.getName(),
         DemoBranches.SAW_Bld_3.getName());
      for (Artifact verArt : results) {
         if (!verArt.isOfType(AtsArtifactTypes.Version)) {
            Assert.fail(String.format("Result Artifact [%s] is not a Version artifact", verArt.toStringWithId()));
         }
         Assert.assertTrue(String.format("Unexpected version name %s", verArt.getName()),
            versionNames.contains(verArt.getName()));
      }
   }

}
