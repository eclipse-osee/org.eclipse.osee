/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.x.server.integration.tests.ats;

import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.x.server.integration.tests.util.IntegrationUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestName;

/**
 * @author Donald G. Dunne
 */
public class AtsActionEndpointTest {

   @Rule
   public MethodRule performanceRule = IntegrationUtil.createPerformanceRule();

   @Rule
   public TestName testName = new TestName();

   private AtsActionEndpointApi actionEndpoint;

   @Before
   public void setUp() {
      actionEndpoint = IntegrationUtil.createAtsClient().getAction();
   }

   @Test
   public void testEndpointAlive() throws Exception {
      Assert.assertTrue(actionEndpoint.get().contains("Action Resource"));
   }

}
