/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats;

import org.eclipse.osee.ats.client.integration.tests.ats.access.AtsTest_Access_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.agile.AtsTest_Agile_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.branch.AtsTest_Branch_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.column.AtsTest_Column_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.config.AtsTest_Config_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.dialog.AtsTest_Dialog_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.editor.AtsTest_Editor_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.ev.AtsTest_EV_TestSuite;
import org.eclipse.osee.ats.client.integration.tests.ats.export.AtsTest_Export_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.health.AtsTest_Health_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.navigate.AtsTest_Navigate_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.operation.AtsTest_Operation_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.render.AtsTest_Renderer_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.resource.AtsTest_Resource_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.util.AtsTest_Util_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.version.AtsTest_Version_Search_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.workflow.AtsTest_Workflow_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.world.search.AtsTest_World_Search_Suite;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsTest_Access_Suite.class,
   // Note: AtsTest_Action_Suite in parent suite
   AtsTest_Agile_Suite.class,
   AtsTest_Branch_Suite.class,
   AtsTest_Column_Suite.class,
   AtsTest_Config_Suite.class,
   AtsTest_Dialog_Suite.class,
   AtsTest_Editor_Suite.class,
   AtsTest_EV_TestSuite.class,
   AtsTest_Export_Suite.class,
   AtsTest_Health_Suite.class,
   AtsTest_Navigate_Suite.class,
   AtsTest_Operation_Suite.class,
   AtsTest_Renderer_Suite.class,
   AtsTest_Resource_Suite.class,
   AtsTest_Util_Suite.class,
   AtsTest_Workflow_Suite.class,
   AtsTest_World_Search_Suite.class,
   AtsTest_Version_Search_Suite.class,})
public class AtsTest_Ats_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      System.out.println("\n\nBegin " + AtsTest_Ats_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsTest_Ats_Suite.class.getSimpleName());
   }
}
