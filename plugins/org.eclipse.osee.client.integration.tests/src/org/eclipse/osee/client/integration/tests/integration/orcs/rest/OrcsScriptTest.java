/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.orcs.rest;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ryan D. Brooks
 */
public class OrcsScriptTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   private static OseeClient oseeClient;

   @BeforeClass
   public static void testSetup() {
      oseeClient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);
   }

   @Test
   public void testOrcsScriptFollowRelation() {
      runScriptCheckContains("attributes",
         "start from branch %s find artifacts where art-id = %s follow relation type = 'Default Hierarchical' to side-B collect artifacts {*};",
         COMMON, CoreArtifactTokens.OseeConfiguration);
   }

   public void testBranchQuery() {
      runScriptCheckContains(COMMON.getName(), "start from branch %s collect branches {*};", COMMON);
   }

   @Ignore
   public void testTxQuery() {
      runScriptCheckContains("MUST REPLACE WITH MEANINGFUL CHECK", "start from tx %s collect txs {*};", COMMON);
   }

   @Test
   public void testArtifactQuery() {
      runScriptCheckContains("Add Common branch artifacts",
         "start from branch %s find artifacts where art-id = %s collect artifacts {id, attributes {*}, relations {*} };",
         COMMON, CoreArtifactTokens.OseeConfiguration);
   }

   private void runScriptCheckContains(String expected, String script, Object... data) {
      String result = oseeClient.runOrcsScript(script, data);
      Assert.assertTrue(result.contains(expected));
   }
}