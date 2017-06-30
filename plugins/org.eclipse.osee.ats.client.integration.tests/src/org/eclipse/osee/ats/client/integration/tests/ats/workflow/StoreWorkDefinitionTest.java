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
package org.eclipse.osee.ats.client.integration.tests.ats.workflow;

import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.JaxAtsWorkDef;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test unit for {@link AtsConfigEndpointApi.storeWorkDef}
 *
 * @author Mark Joy
 */

public class StoreWorkDefinitionTest {

   @Before
   @After
   public void cleanup() throws Exception {
      Artifact workDefArt = (Artifact) AtsClientService.get().getArtifactByName(AtsArtifactTypes.WorkDefinition,
         AtsTestUtil.WORK_DEF_NAME);
      if (workDefArt != null) {
         workDefArt.deleteAndPersist();
      }
   }

   @Test
   public void test() throws Exception {
      JaxAtsWorkDef jaxWorkDef = new JaxAtsWorkDef();
      jaxWorkDef.setName(AtsTestUtil.WORK_DEF_NAME);
      String atsDsl =
         AWorkspace.getOseeInfResource("support/" + AtsTestUtil.WORK_DEF_NAME + ".ats", StoreWorkDefinitionTest.class);
      jaxWorkDef.setWorkDefDsl(atsDsl);
      AtsClientService.getConfigEndpoint().storeWorkDef(jaxWorkDef);

      XResultData resultData = new XResultData();
      IAtsWorkDefinition workDef =
         AtsClientService.get().getWorkDefinitionService().getWorkDefinition(AtsTestUtil.WORK_DEF_NAME, resultData);
      Assert.assertNotNull(workDef);
   }

}
