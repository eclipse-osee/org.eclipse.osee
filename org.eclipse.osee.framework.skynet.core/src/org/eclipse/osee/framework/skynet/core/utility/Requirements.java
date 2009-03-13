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

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.OseeInfo;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class Requirements {
   public static String HARDWARE_REQUIREMENT = "Hardware Requirement";
   public static String HARDWARE_REQUIREMENTS = "Hardware Requirements";
   public static String SOFTWARE_REQUIREMENT = "Software Requirement";
   public static String SOFTWARE_REQUIREMENTS = "Software Requirements";
   public static String INDIRECT_SOFTWARE_REQUIREMENT = "Indirect Software Requirement";
   public static String SYSTEM_REQUIREMENT = "System Requirement";
   public static String SYSTEM_REQUIREMENTS = "System Requirements";
   public static String SYSTEM_DESIGN = "System Design";
   public static String SYSTEM_FUNCTION = "System Function";

   public static String ABSTRACT_SOFTWARE_REQUIREMENT = "Abstract Software Requirement";

   public static String SUBSYSTEM_REQUIREMENT = "Subsystem Requirement";
   public static String SUBSYSTEM_REQUIREMENTS = "Subsystem Requirements";
   public static String SUBSYSTEM_DESIGN = "Subsystem Design";
   public static String SUBSYSTEM_FUNCTION = "Subsystem Function";

   public static String SOFTWARE_DESIGN = "Software Design";
   public static String SOFTWARE_FUNCTION = "Software Function";

   public static String INTERFACE_REQUIREMENT = "Interface Requirement";
   public static String COMPONENT = "Component";

   public static String TEST_INFORMATION_SHEET = "Test Information Sheet";
   public static String OSEE_INFO_TEST_CASE_KEY = "Test Case Type Name";
   public static String TEST_CASE = getTestCaseString();
   public static String TEST_CASES = "Test Cases";
   public static String TEST_PROCEDURE = "Test Procedure";

   public static String SUBSYSTEM = "Subsystem";
   public static String PARTITION = "Partition";
   public static String CSCI = "CSCI";

   public static String getTestCaseString() {
      try {
         if (OseeInfo.getValue(OSEE_INFO_TEST_CASE_KEY) == null) {
            return "Test Case";
         }
         return OseeInfo.getValue(OSEE_INFO_TEST_CASE_KEY);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetActivator.class, OseeLevel.SEVERE, ex);
      }
      return "Test Case";
   }

   public static boolean isSoftwareRequirement(Artifact artifact) {
      return Requirements.Software_RequirementTypes.contains(artifact.getArtifactTypeName());
   }

   public static List<String> Software_RequirementTypes =
         Arrays.asList(SOFTWARE_REQUIREMENT, INDIRECT_SOFTWARE_REQUIREMENT, "Button Requirement", "UIG Artifact",
               "Stand Alone Local Data");
}
