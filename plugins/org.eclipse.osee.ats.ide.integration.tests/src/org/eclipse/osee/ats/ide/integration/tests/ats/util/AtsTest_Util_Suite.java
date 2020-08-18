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

package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsUsersTest.class,
   AtsUserTest.class,
   CopyActionDetailsTest.class,
   AtsProgramServiceTest.class,
   AtsNotifyEndpointImplTest.class,
   AtsChangeSetTest.class,
   AtsDeleteManagerTest.class,
   AtsImageTest.class,
   AtsXWidgetsExampleBlamTest.class,
   XResultDataTableExampleTest.class,
   CreateActionUsingAllActionableItemsTest.class,
   ImportActionsViaSpreadsheetTest.class,
   ImportTasksFromSpreadsheetTest.class})
/**
 * @author Donald G. Dunne
 */
public class AtsTest_Util_Suite {
   // do nothing
}
