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

package org.eclipse.osee.ats.ide.integration.tests.ats.version;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
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
      IAtsVersion sawBld1Ver = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_1);
      IAtsVersion sawBld2Ver = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_2);
      IAtsVersion sawBld3Ver = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_3);

      List<IAtsVersion> parallelVersions = AtsApiService.get().getVersionService().getParallelVersions(sawBld1Ver);
      Assert.assertTrue(parallelVersions.size() == 1);
      Assert.assertTrue(parallelVersions.contains(sawBld2Ver));

      parallelVersions = AtsApiService.get().getVersionService().getParallelVersions(sawBld2Ver);
      Assert.assertTrue(parallelVersions.size() == 2);
      Assert.assertTrue(parallelVersions.contains(sawBld1Ver));
      Assert.assertTrue(parallelVersions.contains(sawBld3Ver));

      parallelVersions = AtsApiService.get().getVersionService().getParallelVersions(sawBld3Ver);
      Assert.assertTrue(parallelVersions.isEmpty());
   }

   @Test
   public void testGetParallelVersionsRecursive() {
      IAtsVersion sawBld1Ver = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_1);
      IAtsVersion sawBld2Ver = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_2);
      IAtsVersion sawBld3Ver = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_3);

      Set<CommitConfigItem> configItems = new HashSet<>();
      AtsApiService.get().getVersionService().getParallelVersions(sawBld1Ver, configItems);
      Assert.assertEquals(3, configItems.size());
      Assert.assertTrue(configItems.contains(new CommitConfigItem(sawBld1Ver, AtsApiService.get())));
      Assert.assertTrue(configItems.contains(new CommitConfigItem(sawBld2Ver, AtsApiService.get())));
      Assert.assertTrue(configItems.contains(new CommitConfigItem(sawBld3Ver, AtsApiService.get())));

      configItems.clear();
      AtsApiService.get().getVersionService().getParallelVersions(sawBld2Ver, configItems);
      Assert.assertEquals(3, configItems.size());
      Assert.assertTrue(configItems.contains(new CommitConfigItem(sawBld1Ver, AtsApiService.get())));
      Assert.assertTrue(configItems.contains(new CommitConfigItem(sawBld2Ver, AtsApiService.get())));
      Assert.assertTrue(configItems.contains(new CommitConfigItem(sawBld3Ver, AtsApiService.get())));

      configItems.clear();
      AtsApiService.get().getVersionService().getParallelVersions(sawBld3Ver, configItems);
      Assert.assertEquals(1, configItems.size());
   }

}
