/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow.state;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   StateManagerUtilityTest.class,
   TeamStateTest.class,
   SimpleTeamStateTest.class,
   StateTypeAdapterTest.class})
/**
 * @author Donald G. Dunne
 */
public class AtsCore_Workflow_State_Suite {
   // TestSuite
}
