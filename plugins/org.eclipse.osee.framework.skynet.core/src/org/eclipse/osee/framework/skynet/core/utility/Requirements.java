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

package org.eclipse.osee.framework.skynet.core.utility;

/**
 * @author Donald G. Dunne
 */
public interface Requirements {
   public final static String HARDWARE_REQUIREMENT = "Hardware Requirement";
   public final static String HARDWARE_REQUIREMENTS = "Hardware Requirements";
   public final static String SOFTWARE_REQUIREMENTS = "Software Requirements";
   public final static String SYSTEM_REQUIREMENTS = "System Requirements";
   public final static String SYSTEM_DESIGN = "System Design";
   public final static String SYSTEM_FUNCTION = "System Function";

   public final static String SUBSYSTEM_REQUIREMENTS = "Subsystem Requirements";
   public final static String SUBSYSTEM_DESIGN = "Subsystem Design";
   public final static String SUBSYSTEM_FUNCTION = "Subsystem Function";

   public final static String ABSTRACT_TEST_UNIT = "Abstract Test Unit";

   public final static String TEST_CASE = "Test Case";
   public final static String TEST_RUN = "Test Run";
   public final static String TEST_SUPPORT = "Test Support";

   public final static String TEST_SUPPORT_UNITS = TEST_SUPPORT + " Units";
   public final static String TEST_PROCEDURE = "Test Procedure";

   public final static String SUBSYSTEM = "Subsystem";
   public final static String PARTITION = "Partition";
   public final static String CSCI = "CSCI";
}
