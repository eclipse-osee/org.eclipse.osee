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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class Requirements {
   public static String SOFTWARE_REQUIREMENT = "Software Requirement";
   public static String SOFTWARE_REQUIREMENTS = "Software Requirements";
   public static String INDIRECT_SOFTWARE_REQUIREMENT = "Indirect Software Requirement";
   public static String SYSTEM_REQUIREMENT = "System Requirement";
   public static String SYSTEM_REQUIREMENTS = "System Requirements";
   public static String SUBSYSTEM_REQUIREMENT = "Subsystem Requirement";
   public static String SUBSYSTEM_REQUIREMENTS = "Subsystem Requirements";
   public static String COMPONENT = "Component";
   public static String TEST_SCRIPT = "Test Script";
   public static String TEST_PROCEDURE = "Test Procedure";
   public static String SUBSYSTEM = "Subsystem";
   public static String PARTITION = "Partition";
   public static String CSCI = "CSCI";

   public static boolean isSoftwareRequirement(Artifact artifact) {
      return Requirements.Software_RequirementTypes.contains(artifact.getArtifactTypeName());
   }

   public static List<String> Software_RequirementTypes =
         Arrays.asList(SOFTWARE_REQUIREMENT, INDIRECT_SOFTWARE_REQUIREMENT);
}
