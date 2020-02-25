/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.demo;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd10SetupAndImportReqs;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
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
      Assert.assertEquals(23, userArts.size());

      Collection<AtsUser> users = AtsClientService.get().getUserService().getUsers();
      Assert.assertEquals(23, users.size());

      Collection<IAtsUser> users2 = AtsClientService.get().getUserService().getUsers(Active.Active);
      Assert.assertEquals(20, users2.size());

      Pdd10SetupAndImportReqs create = new Pdd10SetupAndImportReqs();
      create.run();

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
