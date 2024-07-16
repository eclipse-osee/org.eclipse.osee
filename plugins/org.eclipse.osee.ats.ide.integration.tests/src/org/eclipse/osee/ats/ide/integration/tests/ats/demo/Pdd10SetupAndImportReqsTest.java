/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.demo;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd10SetupAndImportReqs;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd10SetupAndImportReqsTest implements IPopulateDemoDatabaseTest {

   @Test
   public void setupAndImportRequirements() {
      DemoUtil.setPopulateDbSuccessful(false);

      // Test Demo Users
      List<Artifact> userArts = ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User, CoreBranches.COMMON);
      Assert.assertEquals(DemoUsers.values().size() + 3, userArts.size());

      Collection<AtsUser> users = AtsApiService.get().getUserService().getUsers();
      Assert.assertEquals(DemoUsers.values().size() + 3, users.size());

      Collection<AtsUser> users2 = AtsApiService.get().getUserService().getUsers(Active.Active);
      Assert.assertEquals(DemoUsers.values().size(), users2.size());

      new Pdd10SetupAndImportReqs().run();
      DemoUtil.setPopulateDbSuccessful(true);
   }
}