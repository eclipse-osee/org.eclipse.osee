/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats;

import org.eclipse.osee.ats.ide.integration.tests.ats.access.AtsTest_Access_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.agile.AtsTest_Agile_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.branch.AtsTest_Branch_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.column.AtsTest_Column_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.config.AtsTest_Config_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.dialog.AtsTest_Dialog_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.editor.AtsTest_Editor_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.ev.AtsTest_EV_TestSuite;
import org.eclipse.osee.ats.ide.integration.tests.ats.export.AtsTest_Export_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.health.AtsTest_Health_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.navigate.AtsTest_Navigate_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.operation.AtsTest_Operation_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.render.AtsTest_Renderer_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AtsTest_Resource_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.users.AtsTest_Users_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.util.AtsTest_Util_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.version.AtsTest_Version_Search_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.workdef.AtsTest_WorkDef_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTest_Workflow_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.world.AtsTest_World_Suite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsTest_Workflow_Suite.class,
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
   AtsTest_Users_Suite.class,
   AtsTest_Util_Suite.class,
   AtsTest_WorkDef_Suite.class,
   AtsTest_World_Suite.class,
   // AtsTest_World_Search_Suite.class, Moved to AtsTest_Query_Suite to run first
   AtsTest_Version_Search_Suite.class,})
public class AtsTest_Ats_Suite {
   // do nothing
}
