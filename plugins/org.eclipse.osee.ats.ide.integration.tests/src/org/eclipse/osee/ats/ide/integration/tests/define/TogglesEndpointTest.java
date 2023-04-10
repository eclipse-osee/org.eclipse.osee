/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.define;

import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.util.toggles.TogglesFactory;
import org.eclipse.osee.jdbc.JdbcService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

public class TogglesEndpointTest {

   private static String TOGGLE_A = "osee.togglesendpointtest.a";
   private static String TOGGLE_B = "osee.togglesendpointtest.b";

   /**
    * Class level testing rules are applied before the {@link #testSetup} method is invoked. These rules are used for
    * the following:
    * <dl>
    * <dt>Not Production Data Store Rule</dt>
    * <dd>This rule is used to prevent modification of a production database.</dd>
    * <dt>ExitDatabaseInitializationRule</dt>
    * <dd>This rule will exit database initialization mode and re-authenticate as the test user when necessary.</dd>
    * {@Link UserToken} cache has been flushed.</dd></dt>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( new NotProductionDataStoreRule() )
         .around( new ExitDatabaseInitializationRule() )
         ;
   //@formatter:on

   /**
    * Wrap the test methods with a check to prevent execution on a production database.
    */

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @BeforeClass
   public static void testSetup() {
      var jdbcService = OsgiUtil.getService(DemoChoice.class, JdbcService.class);
      var jdbcClient = jdbcService.getClient();

      OseeInfo.setValue(jdbcClient, TogglesEndpointTest.TOGGLE_A, "true");
      OseeInfo.setValue(jdbcClient, TogglesEndpointTest.TOGGLE_B, "false");
   }

   @Test
   public void test() {

      var toggles = TogglesFactory.getTogglesImpl();

      Assert.assertTrue(toggles.apply(TogglesEndpointTest.TOGGLE_A));
      Assert.assertFalse(toggles.apply(TogglesEndpointTest.TOGGLE_B));
   }
}

/* EOF */
