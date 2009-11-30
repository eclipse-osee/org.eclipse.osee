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

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;

/**
 * @author Donald G. Dunne
 */
public class Requirements {
   public final static String HARDWARE_REQUIREMENT = "Hardware Requirement";
   public final static String HARDWARE_REQUIREMENTS = "Hardware Requirements";
   public final static String SOFTWARE_REQUIREMENT = "Software Requirement";
   public final static String SOFTWARE_REQUIREMENT_DRAWING = "Software Requirement Drawing";
   public final static String SOFTWARE_REQUIREMENT_FUNCTION = "Software Requirement Function";
   public final static String SOFTWARE_REQUIREMENT_PROCEDURE = "Software Requirement Procedure";
   public final static String SOFTWARE_REQUIREMENTS = "Software Requirements";
   public final static String INDIRECT_SOFTWARE_REQUIREMENT = "Indirect Software Requirement";
   public final static String SYSTEM_REQUIREMENT = "System Requirement";
   public final static String SYSTEM_REQUIREMENTS = "System Requirements";
   public final static String SYSTEM_DESIGN = "System Design";
   public final static String SYSTEM_FUNCTION = "System Function";
   public final static String BUTTON_REQUIREMENT = "Button Requirement";
   public final static String REQUIREMENT = "Requirement";
   public final static String ABSTRACT_SOFTWARE_REQUIREMENT = "Abstract Software Requirement";

   public final static String SUBSYSTEM_REQUIREMENT = "Subsystem Requirement";
   public final static String SUBSYSTEM_REQUIREMENTS = "Subsystem Requirements";
   public final static String SUBSYSTEM_DESIGN = "Subsystem Design";
   public final static String SUBSYSTEM_FUNCTION = "Subsystem Function";

   public final static String SOFTWARE_DESIGN = "Software Design";
   public final static String SOFTWARE_FUNCTION = "Software Function";

   public final static String INTERFACE_REQUIREMENT = "Interface Requirement";
   public final static String COMPONENT = "Component";

   public final static String ABSTRACT_TEST_UNIT = "Abstract Test Unit";

   public final static String TEST_INFORMATION_SHEET = "Test Information Sheet";
   public final static String TEST_CASE = "Test Case";
   public final static String TEST_RUN = "Test Run";
   public final static String TEST_SUPPORT = "Test Support";

   public final static String TEST_SUPPORT_UNITS = TEST_SUPPORT + " Units";
   public final static String TEST_CASES = "Test Cases";
   public final static String TEST_PROCEDURE = "Test Procedure";

   public final static String SUBSYSTEM = "Subsystem";
   public final static String PARTITION = "Partition";
   public final static String CSCI = "CSCI";
   public final static String FOLDER = "Folder";

   private Requirements() {
   }

   public final static Collection<ArtifactType> getAllSoftwareRequirementTypes() throws OseeCoreException {
      ArtifactType abstractSoftwareReq = ArtifactTypeManager.getType(CoreArtifactTypes.AbstractSoftwareRequirement);
      Collection<ArtifactType> types = abstractSoftwareReq.getAllDescendantTypes();
      types.add(abstractSoftwareReq);
      return types;
   }

   public final static Collection<ArtifactType> getAllDirectSoftwareRequirementTypes() throws OseeCoreException {
      Collection<ArtifactType> types = getAllSoftwareRequirementTypes();
      Collection<ArtifactType> nonDirectRequirements = new HashSet<ArtifactType>();
      for (ArtifactType type : types) {
         if (type.inheritsFrom(CoreArtifactTypes.IndirectSoftwareRequirement)) {
            nonDirectRequirements.add(type);
         }
      }
      types.removeAll(nonDirectRequirements);
      return types;
   }
}
