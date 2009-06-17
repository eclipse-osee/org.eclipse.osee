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
package org.eclipse.osee.ote.define.AUTOGEN;

import org.eclipse.osee.framework.skynet.core.ISkynetType;

public enum OTE_SKYNET_ATTRIBUTES implements ISkynetType {
   BUILD_ID("Build Id"),
   CHECKSUM("Checksum"),
   ELAPSED_DATE("Elapsed Date"),
   END_DATE("End Date"),
   EXTENSION("Extension"),
   FAILED("Failed"),
   IS_BATCH_MODE_ALLOWED("Is Batch Mode Allowed"),
   LAST_AUTHOR("Last Author"),
   LAST_DATE_UPLOADED("Last Date Uploaded"),
   LAST_MODIFIED_DATE("Last Modified Date"),
   MODIFIED_FLAG("Modified Flag"),
   OSEE_SERVER_JAR_VERSION("OSEE Server Jar Version"),
   OSEE_SERVER_TITLE("OSEE Server Title"),
   OSEE_VERSION("OSEE Version"),
   OS_ARCHITECTURE("OS Architecture"),
   OS_NAME("OS Name"),
   OS_VERSION("OS Version"),
   OUTFILE_URL("Outfile URL"),
   PASSED("Passed"),
   PROCESSOR_ID("Processor ID"),
   QUALIFICATION_LEVEL("Qualification Level"),
   RAN_IN_BATCH_MODE("Ran In Batch Mode"),
   REVISION("Revision"),
   SCRIPT_ABORTED("Script Aborted"),
   START_DATE("Start Date"),
   TEST_SCRIPT_GUID("Test Script GUID"),
   TEST_SCRIPT_URL("Test Script URL"),
   TOTAL_TEST_POINTS("Total Test Points"),
   USER_ID("User ID");

   private String name;

   private OTE_SKYNET_ATTRIBUTES(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}