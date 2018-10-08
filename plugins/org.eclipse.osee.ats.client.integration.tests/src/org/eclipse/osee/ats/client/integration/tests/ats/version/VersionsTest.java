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
package org.eclipse.osee.ats.client.integration.tests.ats.version;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.config.Versions;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link Versions}
 *
 * @author Donald G. Dunne
 */
public class VersionsTest {

   @Test
   public void testGetParallelVersionsSingle() {
      IAtsVersion sawBld1Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_1);
      IAtsVersion sawBld2Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_2);
      IAtsVersion sawBld3Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_3);

      List<IAtsVersion> parallelVersions = Versions.getParallelVersions(sawBld1Ver, AtsClientService.get());
      Assert.assertTrue(parallelVersions.size() == 1);
      Assert.assertTrue(parallelVersions.contains(sawBld2Ver));

      parallelVersions = Versions.getParallelVersions(sawBld2Ver, AtsClientService.get());
      Assert.assertTrue(parallelVersions.size() == 2);
      Assert.assertTrue(parallelVersions.contains(sawBld1Ver));
      Assert.assertTrue(parallelVersions.contains(sawBld3Ver));

      parallelVersions = Versions.getParallelVersions(sawBld3Ver, AtsClientService.get());
      Assert.assertTrue(parallelVersions.isEmpty());
   }

   @Test
   public void testGetParallelVersionsRecursive() {
      IAtsVersion sawBld1Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_1);
      IAtsVersion sawBld2Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_2);
      IAtsVersion sawBld3Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_3);

      Set<ICommitConfigItem> configArts = new HashSet<>();
      Versions.getParallelVersions(sawBld1Ver, configArts, AtsClientService.get());
      Assert.assertEquals(3, configArts.size());
      Assert.assertTrue(configArts.contains(sawBld1Ver));
      Assert.assertTrue(configArts.contains(sawBld2Ver));
      Assert.assertTrue(configArts.contains(sawBld3Ver));

      configArts.clear();
      Versions.getParallelVersions(sawBld2Ver, configArts, AtsClientService.get());
      Assert.assertEquals(3, configArts.size());
      Assert.assertTrue(configArts.contains(sawBld1Ver));
      Assert.assertTrue(configArts.contains(sawBld2Ver));
      Assert.assertTrue(configArts.contains(sawBld3Ver));

      configArts.clear();
      Versions.getParallelVersions(sawBld3Ver, configArts, AtsClientService.get());
      Assert.assertEquals(1, configArts.size());
   }

}
