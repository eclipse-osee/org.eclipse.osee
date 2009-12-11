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

public enum OteAttributeTypes implements IAttributeType {
   BUILD_ID("Build Id", "AAMFEXG6_W9diA9nUXAA"),
   CHECKSUM("Checksum", "AAMFEXJbaHt5uKG9kogA"),
   ELAPSED_DATE("Elapsed Date", "AAMFEWuD6yH04y89M3wA"),
   END_DATE("End Date", "AAMFEWryxym0P9FFckgA"),
   EXTENSION("Extension", "AAMFEcUbJEERZTnwJzAA"),
   FAILED("Failed", "AAMFEWynSU+XeRG7nRAA"),
   IS_BATCH_MODE_ALLOWED("Is Batch Mode Allowed", "AAMFEW+CcA6F5GEjsSgA"),
   LAST_AUTHOR("Last Author", "AAMFEWE83iPq3+2DGrQA"),
   LAST_DATE_UPLOADED("Last Date Uploaded", "AAMFEXCm5ju5gvq142QA"),
   LAST_MODIFIED_DATE("Last Modified Date", "AAMFEWHw7V1uWv4IKcQA"),
   MODIFIED_FLAG("Modified Flag", "AAMFEWCruiS26nCN68wA"),
   OSEE_SERVER_JAR_VERSION("OSEE Server Jar Version", "AAMFEWV1OQtXL67OfOQA"),
   OSEE_SERVER_TITLE("OSEE Server Title", "AAMFEWTnXGYRfdzY3gAA"),
   OSEE_VERSION("OSEE Version", "AAMFEWQ_TTkstJvjnGQA"),
   OS_ARCHITECTURE("OS Architecture", "AAMFEWKJtG+Jc8OkRYgA"),
   OS_NAME("OS Name", "AAMFEWMdBmP9aCgsysgA"),
   OS_VERSION("OS Version", "AAMFEWOvQWV6JJvh9NQA"),
   OUTFILE_URL("Outfile URL", "AAMFEVlyBndUvySg+gwA"),
   PASSED("Passed", "AAMFEWwT92IzQp6Dh3gA"),
   PROCESSOR_ID("Processor ID", "AAMFEWYOAGkplo4RjTQA"),
   QUALIFICATION_LEVEL("Qualification Level", "AAMFEXEvTztOWwoGyRAA"),
   RAN_IN_BATCH_MODE("Ran In Batch Mode", "AAMFEW7uBlg0KLp8mhQA"),
   REVISION("Revision", "AAMFEWAal2I3j7EJligA"),
   SCRIPT_ABORTED("Script Aborted", "AAMFEW3M12AqHsCve4AA"),
   START_DATE("Start Date", "AAMFEWpeXjfr2W5ZT5QA"),
   TEST_SCRIPT_GUID("Test Script GUID", "AAMFEW5fEhinpcjeZ0wA"),
   TEST_SCRIPT_URL("Test Script URL", "AAMFEVn0+mhxtCuACgAA"),
   TOTAL_TEST_POINTS("Total Test Points", "AAMFEW05hBfECFGfZkgA");

   private final String name;
   private final String guid;

   private OteAttributeTypes(String name, String guid) {
      this.name = name;
      this.guid = guid;
   }

   public String getName() {
      return this.name;
   }

   public String getGuid() {
      return guid;
   }
}