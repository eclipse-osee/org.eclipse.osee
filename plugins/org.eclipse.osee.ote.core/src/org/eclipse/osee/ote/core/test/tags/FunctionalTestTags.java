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
package org.eclipse.osee.ote.core.test.tags;


/**
 * @author Roberto E. Escobar
 */
public abstract interface FunctionalTestTags extends BaseTestTags{
   public static final String FUNCTIONAL_TEST_SCRIPT = "FunctionalTestScript";
   public static final String FUNCTIONAL_TEST_CASE = "FunctionalTestCase"; 
   public static final String FUNCTIONAL_TEST_POINT = "FunctionalTestPoint";
   public static final String FUNCTIONAL_UNIT_EXECUTION = "FunctionalUnitExecution";
   public static final String FUNCTIONAL_UNIT_HEADER = "FunctionalUnitHeader";
   
   public static final String EXECUTABLE_FIELD = "Executable";
   public static final String OPTIONS_FIELD = "Options";
   public static final String GUID_FIELD = "Guid";
  
   public static final String FUNCTIONAL_TEST_POINT_RESULT = "Result";
   public static final String RESULT_PASSED = "PASSED";
   public static final String RESULT_FAILED = "FAILED";
   public static final String RESULT_NOT_RUN = "NOT RUN";
   public static final String RESULT_MANUAL = "MANUAL";
   
   public static final String MESSAGE_CHECK = "MessageCheck";
   public static final String VALUE_FIELD = "Value";
   public static final String ACTION_CMD = "ActionCmd";
}
