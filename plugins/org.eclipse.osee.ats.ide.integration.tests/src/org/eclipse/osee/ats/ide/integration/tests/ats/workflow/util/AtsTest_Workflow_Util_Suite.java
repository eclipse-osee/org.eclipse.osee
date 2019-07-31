/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   DuplicateWorkflowAtStartStateOperationTest.class, //
   DuplicateWorkflowAsIsOperationTest.class //
})
public class AtsTest_Workflow_Util_Suite {
}
