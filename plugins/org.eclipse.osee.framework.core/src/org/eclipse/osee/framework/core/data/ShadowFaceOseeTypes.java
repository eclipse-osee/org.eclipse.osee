/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.data;

import static org.eclipse.osee.framework.core.data.FaceOseeTypes.*;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.*;
import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.*;
import static org.eclipse.osee.framework.core.enums.RelationSide.*;
import static org.eclipse.osee.framework.core.enums.RelationSorter.*;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.*;

public interface ShadowFaceOseeTypes {

   RelationTypeToken AbstractSpecRequirementToUnitOfConformanceRel =
      face.addNewRelationType(4400405161969600892L, "Abstract Spec Requirement", MANY_TO_MANY, LEXICOGRAPHICAL_ASC,
         AbstractSpecRequirement, "Abstract Spec Requirement", UnitOfConformance, "Unit Of Conformance");
   RelationTypeSide AbstractSpecRequirementToUnitOfConformanceRel_AbstractSpecRequirement =
      RelationTypeSide.create(AbstractSpecRequirementToUnitOfConformanceRel, SIDE_A);
   RelationTypeSide AbstractSpecRequirementToUnitOfConformanceRel_UnitOfConformance =
      RelationTypeSide.create(AbstractSpecRequirementToUnitOfConformanceRel, SIDE_B);
   // @formatter:on

}