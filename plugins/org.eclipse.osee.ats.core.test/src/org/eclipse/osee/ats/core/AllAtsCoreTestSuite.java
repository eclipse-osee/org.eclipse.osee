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
package org.eclipse.osee.ats.core;

import org.eclipse.osee.ats.core.ai.AtsCore_AI_Suite;
import org.eclipse.osee.ats.core.column.AtsCore_Column_JT_Suite;
import org.eclipse.osee.ats.core.commit.operations.AtsCore_Commit_JT_Suite;
import org.eclipse.osee.ats.core.config.AtsCore_Config_JT_Suite;
import org.eclipse.osee.ats.core.internal.column.ev.AtsCore_InternalColumnEv_JT_Suite;
import org.eclipse.osee.ats.core.internal.log.AtsTest_Internal_Log_Suite;
import org.eclipse.osee.ats.core.internal.state.AtsCore_Internal_State_Suite;
import org.eclipse.osee.ats.core.internal.util.AtsCore_Internal_Util_Suite;
import org.eclipse.osee.ats.core.model.impl.AtsCore_ModelImpl_JT_Suite;
import org.eclipse.osee.ats.core.query.AtsCore_Query_JT_Suite;
import org.eclipse.osee.ats.core.transition.AtsCore_Transition_JT_Suite;
import org.eclipse.osee.ats.core.users.AtsCore_Users_JT_Suite;
import org.eclipse.osee.ats.core.util.AtsCore_Util_JT_Suite;
import org.eclipse.osee.ats.core.validator.AtsCore_Validator_JT_Suite;
import org.eclipse.osee.ats.core.workdef.AtsCore_WorkDef_JT_Suite;
import org.eclipse.osee.ats.core.workflow.AtsCore_Workflow_JT_Suite;
import org.eclipse.osee.ats.core.workflow.note.AtsCore_Workflow_Note_Suite;
import org.eclipse.osee.ats.core.workflow.transition.AtsCore_Workflow_Transition_Suite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsCore_AI_Suite.class,
   AtsCore_Column_JT_Suite.class,
   AtsCore_Commit_JT_Suite.class,
   AtsCore_Config_JT_Suite.class,
   AtsTest_Internal_Log_Suite.class,
   AtsCore_Internal_State_Suite.class,
   AtsCore_Internal_Util_Suite.class,
   AtsCore_Workflow_Transition_Suite.class,
   AtsCore_Workflow_Note_Suite.class,
   AtsCore_Query_JT_Suite.class,
   AtsCore_InternalColumnEv_JT_Suite.class,
   AtsCore_ModelImpl_JT_Suite.class,
   AtsCore_Transition_JT_Suite.class,
   AtsCore_Util_JT_Suite.class,
   AtsCore_Users_JT_Suite.class,
   AtsCore_Validator_JT_Suite.class,
   AtsCore_WorkDef_JT_Suite.class,
   AtsCore_Workflow_JT_Suite.class})
/**
 * This test suite contains tests that can be run as stand-alone JUnit tests (JT)
 *
 * @author Donald G. Dunne
 */
public class AllAtsCoreTestSuite {
   // Test Suite
}
