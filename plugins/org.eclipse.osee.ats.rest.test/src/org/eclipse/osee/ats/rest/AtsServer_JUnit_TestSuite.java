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

package org.eclipse.osee.ats.rest;

import org.eclipse.osee.ats.rest.internal.agile.AtsRest_Agile_Suite;
import org.eclipse.osee.ats.rest.internal.workitem.task.track.AtsRest_WorkItem_Task_Track_Suite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({AtsRest_Agile_Suite.class, AtsRest_WorkItem_Task_Track_Suite.class})
public class AtsServer_JUnit_TestSuite {
   // Test Suite
}
