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
package org.eclipse.osee.ats.client.integration.tests.ats.version;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.config.version.MassEditTeamVersionItem;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
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
      IAtsTeamDefinition teamDef = TeamDefinitions.getTeamDefinitions(Arrays.asList("SAW SW"),
         AtsClientService.get().getQueryService()).iterator().next();

      MassEditTeamVersionItem search = new MassEditTeamVersionItem("Search", null, AtsImage.ACTION);
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
