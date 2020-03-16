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
package org.eclipse.osee.ats.ide.integration.tests.ats.version;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
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
      IAtsVersion sawBld1Ver = AtsClientService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_1);
      IAtsVersion sawBld2Ver = AtsClientService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_2);
      IAtsVersion sawBld3Ver = AtsClientService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_3);

      List<IAtsVersion> parallelVersions = AtsClientService.get().getVersionService().getParallelVersions(sawBld1Ver);
      Assert.assertTrue(parallelVersions.size() == 1);
      Assert.assertTrue(parallelVersions.contains(sawBld2Ver));

      parallelVersions = AtsClientService.get().getVersionService().getParallelVersions(sawBld2Ver);
      Assert.assertTrue(parallelVersions.size() == 2);
      Assert.assertTrue(parallelVersions.contains(sawBld1Ver));
      Assert.assertTrue(parallelVersions.contains(sawBld3Ver));

      parallelVersions = AtsClientService.get().getVersionService().getParallelVersions(sawBld3Ver);
      Assert.assertTrue(parallelVersions.isEmpty());
   }

   @Test
   public void testGetParallelVersionsRecursive() {
      IAtsVersion sawBld1Ver = AtsClientService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_1);
      IAtsVersion sawBld2Ver = AtsClientService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_2);
      IAtsVersion sawBld3Ver = AtsClientService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_3);

      Set<CommitConfigItem> configItems = new HashSet<>();
      AtsClientService.get().getVersionService().getParallelVersions(sawBld1Ver, configItems);
      Assert.assertEquals(3, configItems.size());
      Assert.assertTrue(configItems.contains(new CommitConfigItem(sawBld1Ver, AtsClientService.get())));
      Assert.assertTrue(configItems.contains(new CommitConfigItem(sawBld2Ver, AtsClientService.get())));
      Assert.assertTrue(configItems.contains(new CommitConfigItem(sawBld3Ver, AtsClientService.get())));

      configItems.clear();
      AtsClientService.get().getVersionService().getParallelVersions(sawBld2Ver, configItems);
      Assert.assertEquals(3, configItems.size());
      Assert.assertTrue(configItems.contains(new CommitConfigItem(sawBld1Ver, AtsClientService.get())));
      Assert.assertTrue(configItems.contains(new CommitConfigItem(sawBld2Ver, AtsClientService.get())));
      Assert.assertTrue(configItems.contains(new CommitConfigItem(sawBld3Ver, AtsClientService.get())));

      configItems.clear();
      AtsClientService.get().getVersionService().getParallelVersions(sawBld3Ver, configItems);
      Assert.assertEquals(1, configItems.size());
   }

}
