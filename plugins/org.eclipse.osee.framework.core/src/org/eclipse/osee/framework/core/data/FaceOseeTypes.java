/*********************************************************************
 * Copyright (c) 2020 Boeing
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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractSpecRequirement;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.face;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.MANY_TO_MANY;
import org.eclipse.osee.framework.core.enums.OSS_ProfileAttributeType;
import org.eclipse.osee.framework.core.enums.SegmentAttributeType;
import org.eclipse.osee.framework.core.enums.TechStandardVersionAttributeType;

/**
 * @author David W. Miller
 */
public interface FaceOseeTypes {

   // @formatter:off
   TechStandardVersionAttributeType TechStandardVersion = face.createEnum(new TechStandardVersionAttributeType());
   OSS_ProfileAttributeType OSS_Profile = face.createEnum(new OSS_ProfileAttributeType());
   SegmentAttributeType Segment = face.createEnum(new SegmentAttributeType());

   ArtifactTypeToken UnitOfConformance = face.add(face.artifactType(3993898326810521606L, "Unit of Conformance", false, Artifact)
      .exactlyOne(TechStandardVersion, TechStandardVersion.Unspecified)
      .exactlyOne(OSS_Profile, OSS_Profile.Unspecified)
      .exactlyOne(Segment, Segment.Unspecified));

   RelationTypeToken AbstractSpecRequirementToUnitOfConformance = face.add(990220659578923911L, "Abstract Spec Requirement", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, AbstractSpecRequirement, "Abstract Spec Requirement", UnitOfConformance, "Unit Of Conformance");
   RelationTypeSide AbstractSpecRequirementToUnitOfConformance_AbstractSpecRequirement = RelationTypeSide.create(AbstractSpecRequirementToUnitOfConformance, SIDE_A);
   RelationTypeSide AbstractSpecRequirementToUnitOfConformance_UnitOfConformance = RelationTypeSide.create(AbstractSpecRequirementToUnitOfConformance, SIDE_B);
   // @formatter:on

}