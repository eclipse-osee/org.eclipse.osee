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
import org.eclipse.osee.framework.core.data.NamedIdentity;

public class OteAttributeTypes extends NamedIdentity implements IAttributeType {
   public static final OteAttributeTypes BUILD_ID = new OteAttributeTypes("AAMFEXG6_W9diA9nUXAA", "Build Id");
   public static final OteAttributeTypes CHECKSUM = new OteAttributeTypes("AAMFEXJbaHt5uKG9kogA", "Checksum");
   public static final OteAttributeTypes ELAPSED_DATE = new OteAttributeTypes("AAMFEWuD6yH04y89M3wA", "Elapsed Date");
   public static final OteAttributeTypes END_DATE = new OteAttributeTypes("AAMFEWryxym0P9FFckgA", "End Date");
   public static final OteAttributeTypes EXTENSION = new OteAttributeTypes("AAMFEcUbJEERZTnwJzAA", "Extension");
   public static final OteAttributeTypes FAILED = new OteAttributeTypes("AAMFEWynSU+XeRG7nRAA", "Failed");
   public static final OteAttributeTypes IS_BATCH_MODE_ALLOWED = new OteAttributeTypes("AAMFEW+CcA6F5GEjsSgA",
      "Is Batch Mode Allowed");
   public static final OteAttributeTypes LAST_AUTHOR = new OteAttributeTypes("AAMFEWE83iPq3+2DGrQA", "Last Author");
   public static final OteAttributeTypes LAST_DATE_UPLOADED = new OteAttributeTypes("AAMFEXCm5ju5gvq142QA",
      "Last Date Uploaded");
   public static final OteAttributeTypes LAST_MODIFIED_DATE = new OteAttributeTypes("AAMFEWHw7V1uWv4IKcQA",
      "Last Modified Date");
   public static final OteAttributeTypes MODIFIED_FLAG = new OteAttributeTypes("AAMFEWCruiS26nCN68wA", "Modified Flag");
   public static final OteAttributeTypes OSEE_SERVER_JAR_VERSION = new OteAttributeTypes("AAMFEWV1OQtXL67OfOQA",
      "OSEE Server Jar Version");
   public static final OteAttributeTypes OSEE_SERVER_TITLE = new OteAttributeTypes("AAMFEWTnXGYRfdzY3gAA",
      "OSEE Server Title");
   public static final OteAttributeTypes OSEE_VERSION = new OteAttributeTypes("AAMFEWQ_TTkstJvjnGQA", "OSEE Version");
   public static final OteAttributeTypes OS_ARCHITECTURE = new OteAttributeTypes("AAMFEWKJtG+Jc8OkRYgA",
      "OS Architecture");
   public static final OteAttributeTypes OS_NAME = new OteAttributeTypes("AAMFEWMdBmP9aCgsysgA", "OS Name");
   public static final OteAttributeTypes OS_VERSION = new OteAttributeTypes("AAMFEWOvQWV6JJvh9NQA", "OS Version");
   public static final OteAttributeTypes OUTFILE_URL = new OteAttributeTypes("AAMFEVlyBndUvySg+gwA", "Outfile URL");
   public static final OteAttributeTypes PASSED = new OteAttributeTypes("AAMFEWwT92IzQp6Dh3gA", "Passed");
   public static final OteAttributeTypes PROCESSOR_ID = new OteAttributeTypes("AAMFEWYOAGkplo4RjTQA", "Processor ID");
   public static final OteAttributeTypes QUALIFICATION_LEVEL = new OteAttributeTypes("AAMFEXEvTztOWwoGyRAA",
      "Qualification Level");
   public static final OteAttributeTypes RAN_IN_BATCH_MODE = new OteAttributeTypes("AAMFEW7uBlg0KLp8mhQA",
      "Ran In Batch Mode");
   public static final OteAttributeTypes REVISION = new OteAttributeTypes("AAMFEWAal2I3j7EJligA", "Revision");
   public static final OteAttributeTypes SCRIPT_ABORTED = new OteAttributeTypes("AAMFEW3M12AqHsCve4AA",
      "Script Aborted");
   public static final OteAttributeTypes START_DATE = new OteAttributeTypes("AAMFEWpeXjfr2W5ZT5QA", "Start Date");
   public static final OteAttributeTypes TEST_SCRIPT_GUID = new OteAttributeTypes("AAMFEW5fEhinpcjeZ0wA",
      "Test Script GUID");
   public static final OteAttributeTypes TEST_SCRIPT_URL = new OteAttributeTypes("AAMFEVn0+mhxtCuACgAA",
      "Test Script URL");
   public static final OteAttributeTypes TOTAL_TEST_POINTS = new OteAttributeTypes("AAMFEW05hBfECFGfZkgA",
      "Total Test Points");

   private OteAttributeTypes(String guid, String name) {
      super(guid, name);
   }
}