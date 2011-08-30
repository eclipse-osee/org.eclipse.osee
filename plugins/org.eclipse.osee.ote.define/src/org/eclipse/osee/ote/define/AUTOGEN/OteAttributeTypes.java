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

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.TokenFactory;

public final class OteAttributeTypes {

   // @formatter:off
   public static final IAttributeType BUILD_ID = TokenFactory.createAttributeType(0x100000000000014AL, "Build Id");
   public static final IAttributeType CHECKSUM = TokenFactory.createAttributeType(0x100000000000014BL, "Checksum");
   public static final IAttributeType ELAPSED_DATE = TokenFactory.createAttributeType(0x1000000000000140L, "Elapsed Date");
   public static final IAttributeType END_DATE = TokenFactory.createAttributeType(0x100000000000013FL, "End Date");
   public static final IAttributeType EXTENSION = TokenFactory.createAttributeType(0x1000000000000058L, "Extension");
   public static final IAttributeType FAILED = TokenFactory.createAttributeType(0x1000000000000142L, "Failed");
   public static final IAttributeType IS_BATCH_MODE_ALLOWED = TokenFactory.createAttributeType(0x1000000000000147L, "Is Batch Mode Allowed");
   public static final IAttributeType LAST_AUTHOR = TokenFactory.createAttributeType(0x1000000000000135L, "Last Author");
   public static final IAttributeType LAST_DATE_UPLOADED = TokenFactory.createAttributeType(0x1000000000000148L, "Last Date Uploaded");
   public static final IAttributeType LAST_MODIFIED_DATE = TokenFactory.createAttributeType(0x1000000000000136L, "Last Modified Date");
   public static final IAttributeType MODIFIED_FLAG = TokenFactory.createAttributeType(0x1000000000000134L, "Modified Flag");
   public static final IAttributeType OSEE_SERVER_JAR_VERSION = TokenFactory.createAttributeType(0x100000000000013CL, "OSEE Server Jar Version");
   public static final IAttributeType OSEE_SERVER_TITLE = TokenFactory.createAttributeType(0x100000000000013BL, "OSEE Server Title");
   public static final IAttributeType OSEE_VERSION = TokenFactory.createAttributeType(0x100000000000013AL, "OSEE Version");
   public static final IAttributeType OS_ARCHITECTURE = TokenFactory.createAttributeType(0x1000000000000137L, "OS Architecture");
   public static final IAttributeType OS_NAME = TokenFactory.createAttributeType(0x1000000000000138L, "OS Name");
   public static final IAttributeType OS_VERSION = TokenFactory.createAttributeType(0x1000000000000139L, "OS Version");
   public static final IAttributeType OUTFILE_URL = TokenFactory.createAttributeType(0x1000000000000131L, "Outfile URL");
   public static final IAttributeType PASSED = TokenFactory.createAttributeType(0x1000000000000141L, "Passed");
   public static final IAttributeType PROCESSOR_ID = TokenFactory.createAttributeType(0x100000000000013DL, "Processor ID");
   public static final IAttributeType QUALIFICATION_LEVEL = TokenFactory.createAttributeType(0x1000000000000149L, "Qualification Level");
   public static final IAttributeType RAN_IN_BATCH_MODE = TokenFactory.createAttributeType(0x1000000000000146L, "Ran In Batch Mode");
   public static final IAttributeType REVISION = TokenFactory.createAttributeType(0x1000000000000133L, "Revision");
   public static final IAttributeType SCRIPT_ABORTED = TokenFactory.createAttributeType(0x1000000000000144L, "Script Aborted");
   public static final IAttributeType START_DATE = TokenFactory.createAttributeType(0x100000000000013EL, "Start Date");
   public static final IAttributeType TEST_SCRIPT_URL = TokenFactory.createAttributeType(0x1000000000000132L, "Test Script URL");
   public static final IAttributeType TOTAL_TEST_POINTS = TokenFactory.createAttributeType(0x1000000000000143L, "Total Test Points");
   public static final IAttributeType TestDisposition = TokenFactory.createAttributeType(0x100000000000014CL, "Disposition");
   // @formatter:on

   private OteAttributeTypes() {
      // Constants
   }
}