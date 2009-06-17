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
package org.eclipse.osee.ote.define;

/**
 * @author Roberto E. Escobar
 */
public enum TestRunField {

   INVALID,
   USER_ID,
   USER_NAME,
   SCRIPT_NAME,

   SCRIPT_REVISION,
   SCRIPT_MODIFIED_FLAG,
   SCRIPT_LAST_AUTHOR,
   SCRIPT_LAST_MODIFIED,
   SCRIPT_URL,

   SYSTEM_OS_ARCH,
   SYSTEM_OS_NAME,
   SYSTEM_OS_VERSION,
   SYSTEM_OSEE_VERSION,
   SYSTEM_OSEE_SERVER_TITLE,
   SYSTEM_OSEE_SERVER_JAR_VERSIONS,

   PROCESSOR_ID,
   SCRIPT_START_DATE,
   SCRIPT_END_DATE,
   SCRIPT_ELAPSED_TIME,
   SCRIPT_EXECUTION_TIME,
   SCRIPT_EXECUTION_RESULTS,
   SCRIPT_EXECUTION_ERRORS,

   TEST_POINTS_PASSED,
   TEST_POINTS_FAILED,
   TOTAL_TEST_POINTS,
   TEST_ABORT_STATUS,

   QUALIFICATION_LEVEL,
   BUILD_ID,

   IS_BATCH_MODE_ALLOWED,
   RAN_IN_BATCH_MODE;
}
