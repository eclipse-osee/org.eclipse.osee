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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class Requirements {
   public static String HARDWARE_REQUIREMENT = "Hardware Requirement";
   public static String HARDWARE_REQUIREMENTS = "Hardware Requirements";
   public static String SOFTWARE_REQUIREMENT = "Software Requirement";
   public static String SOFTWARE_REQUIREMENT_DRAWING = "Software Requirement Drawing";
   public static String SOFTWARE_REQUIREMENT_FUNCTION = "Software Requirement Function";
   public static String SOFTWARE_REQUIREMENT_PROCEDURE = "Software Requirement Procedure";
   public static String SOFTWARE_REQUIREMENTS = "Software Requirements";
   public static String INDIRECT_SOFTWARE_REQUIREMENT = "Indirect Software Requirement";
   public static String SYSTEM_REQUIREMENT = "System Requirement";
   public static String SYSTEM_REQUIREMENTS = "System Requirements";
   public static String SYSTEM_DESIGN = "System Design";
   public static String SYSTEM_FUNCTION = "System Function";
   public static String REQUIREMENT = "Requirement";
   public static String ABSTRACT_SOFTWARE_REQUIREMENT = "Abstract Software Requirement";

   public static String SUBSYSTEM_REQUIREMENT = "Subsystem Requirement";
   public static String SUBSYSTEM_REQUIREMENTS = "Subsystem Requirements";
   public static String SUBSYSTEM_DESIGN = "Subsystem Design";
   public static String SUBSYSTEM_FUNCTION = "Subsystem Function";

   public static String SOFTWARE_DESIGN = "Software Design";
   public static String SOFTWARE_FUNCTION = "Software Function";

   public static String INTERFACE_REQUIREMENT = "Interface Requirement";
   public static String COMPONENT = "Component";

   public static String ABSTRACT_TEST_UNIT = "Abstract Test Unit";

   public static String TEST_INFORMATION_SHEET = "Test Information Sheet";
   public static String TEST_CASE = "Test Case";
   public static String TEST_RUN = "Test Run";
   public static String TEST_SUPPORT = "Test Support";

   public static String TEST_SUPPORT_UNITS = TEST_SUPPORT + " Units";
   public static String TEST_CASES = "Test Cases";
   public static String TEST_PROCEDURE = "Test Procedure";

   public static String CODE_UNIT = "Code Unit";
   public static String SUBSYSTEM = "Subsystem";
   public static String PARTITION = "Partition";
   public static String CSCI = "CSCI";
   public static String FOLDER = "Folder";

   public static boolean isSoftwareRequirement(Artifact artifact) {
      return Requirements.ALL_SOFTWARES_REQUIREMENT_TYPES.contains(artifact.getArtifactTypeName());
   }

   public final static List<String> DIRECT_SOFTWARE_REQUIREMENT_TYPES =
         Arrays.asList(SOFTWARE_REQUIREMENT, SOFTWARE_REQUIREMENT_FUNCTION, SOFTWARE_REQUIREMENT_PROCEDURE,
               "Button Requirement", "UIG Artifact", "Stand Alone Local Data");

   public final static List<String> ALL_SOFTWARES_REQUIREMENT_TYPES = new ArrayList<String>();
   static {
      ALL_SOFTWARES_REQUIREMENT_TYPES.addAll(DIRECT_SOFTWARE_REQUIREMENT_TYPES);
      ALL_SOFTWARES_REQUIREMENT_TYPES.add(INDIRECT_SOFTWARE_REQUIREMENT);
      ALL_SOFTWARES_REQUIREMENT_TYPES.add(SOFTWARE_REQUIREMENT_DRAWING);
   }

   public static List<String> ALL_TEST_UNIT_TYPES = Arrays.asList(TEST_CASE, TEST_SUPPORT);
}
