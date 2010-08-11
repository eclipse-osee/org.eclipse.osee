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

   // @formatter:off
   public static final IAttributeType BUILD_ID = new OteAttributeTypes("AAMFEXG6_W9diA9nUXAA", "Build Id");
   public static final IAttributeType CHECKSUM = new OteAttributeTypes("AAMFEXJbaHt5uKG9kogA", "Checksum");
   public static final IAttributeType ELAPSED_DATE = new OteAttributeTypes("AAMFEWuD6yH04y89M3wA", "Elapsed Date");
   public static final IAttributeType END_DATE = new OteAttributeTypes("AAMFEWryxym0P9FFckgA", "End Date");
   public static final IAttributeType EXTENSION = new OteAttributeTypes("AAMFEcUbJEERZTnwJzAA", "Extension");
   public static final IAttributeType FAILED = new OteAttributeTypes("AAMFEWynSU+XeRG7nRAA", "Failed");
   public static final IAttributeType IS_BATCH_MODE_ALLOWED = new OteAttributeTypes("AAMFEW+CcA6F5GEjsSgA", "Is Batch Mode Allowed");
   public static final IAttributeType LAST_AUTHOR = new OteAttributeTypes("AAMFEWE83iPq3+2DGrQA", "Last Author");
   public static final IAttributeType LAST_DATE_UPLOADED = new OteAttributeTypes("AAMFEXCm5ju5gvq142QA", "Last Date Uploaded");
   public static final IAttributeType LAST_MODIFIED_DATE = new OteAttributeTypes("AAMFEWHw7V1uWv4IKcQA", "Last Modified Date");
   public static final IAttributeType MODIFIED_FLAG = new OteAttributeTypes("AAMFEWCruiS26nCN68wA", "Modified Flag");
   public static final IAttributeType OSEE_SERVER_JAR_VERSION = new OteAttributeTypes("AAMFEWV1OQtXL67OfOQA", "OSEE Server Jar Version");
   public static final IAttributeType OSEE_SERVER_TITLE = new OteAttributeTypes("AAMFEWTnXGYRfdzY3gAA", "OSEE Server Title");
   public static final IAttributeType OSEE_VERSION = new OteAttributeTypes("AAMFEWQ_TTkstJvjnGQA", "OSEE Version");
   public static final IAttributeType OS_ARCHITECTURE = new OteAttributeTypes("AAMFEWKJtG+Jc8OkRYgA", "OS Architecture");
   public static final IAttributeType OS_NAME = new OteAttributeTypes("AAMFEWMdBmP9aCgsysgA", "OS Name");
   public static final IAttributeType OS_VERSION = new OteAttributeTypes("AAMFEWOvQWV6JJvh9NQA", "OS Version");
   public static final IAttributeType OUTFILE_URL = new OteAttributeTypes("AAMFEVlyBndUvySg+gwA", "Outfile URL");
   public static final IAttributeType PASSED = new OteAttributeTypes("AAMFEWwT92IzQp6Dh3gA", "Passed");
   public static final IAttributeType PROCESSOR_ID = new OteAttributeTypes("AAMFEWYOAGkplo4RjTQA", "Processor ID");
   public static final IAttributeType QUALIFICATION_LEVEL = new OteAttributeTypes("AAMFEXEvTztOWwoGyRAA", "Qualification Level");
   public static final IAttributeType RAN_IN_BATCH_MODE = new OteAttributeTypes("AAMFEW7uBlg0KLp8mhQA", "Ran In Batch Mode");
   public static final IAttributeType REVISION = new OteAttributeTypes("AAMFEWAal2I3j7EJligA", "Revision");
   public static final IAttributeType SCRIPT_ABORTED = new OteAttributeTypes("AAMFEW3M12AqHsCve4AA", "Script Aborted");
   public static final IAttributeType START_DATE = new OteAttributeTypes("AAMFEWpeXjfr2W5ZT5QA", "Start Date");
   public static final IAttributeType TEST_SCRIPT_URL = new OteAttributeTypes("AAMFEVn0+mhxtCuACgAA", "Test Script URL");
   public static final IAttributeType TOTAL_TEST_POINTS = new OteAttributeTypes("AAMFEW05hBfECFGfZkgA", "Total Test Points");
   public static final IAttributeType TestDisposition = new OteAttributeTypes("AAMFEXfoPWRZHNiOR3gA", "Disposition");
   // @formatter:on

   private OteAttributeTypes(String guid, String name) {
      super(guid, name);
   }
}