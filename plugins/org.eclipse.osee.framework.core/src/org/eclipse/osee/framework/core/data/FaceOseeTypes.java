/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractSpecRequirement;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.face;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.MANY_TO_MANY;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.enums.OSS_ProfileAttributeType;
import org.eclipse.osee.framework.core.enums.SegmentAttributeType;
import org.eclipse.osee.framework.core.enums.TechStandardVersionAttributeType;

/**
 * @author David W. Miller
 */
public interface FaceOseeTypes {

   // @formatter:off
   TechStandardVersionAttributeType TechStandardVersion = face.createEnum(TechStandardVersionAttributeType::new, MediaType.TEXT_PLAIN);
   OSS_ProfileAttributeType OSS_Profile = face.createEnum(OSS_ProfileAttributeType::new, MediaType.TEXT_PLAIN);
   SegmentAttributeType Segment = face.createEnum(SegmentAttributeType::new, MediaType.TEXT_PLAIN);

   ArtifactTypeToken UnitOfConformance = face.add(face.artifactType(3993898326810521606L, "Unit of Conformance", false, Artifact)
      .exactlyOne(TechStandardVersion, "Unspecified")
      .exactlyOne(OSS_Profile, "Unspecified")
      .exactlyOne(Segment, "Unspecified"));

   RelationTypeToken AbstractSpecRequirementToUnitOfConformance = face.add(990220659578923911L, "Abstract Spec Requirement", MANY_TO_MANY, LEXICOGRAPHICAL_ASC, AbstractSpecRequirement, "Abstract Spec Requirement", UnitOfConformance, "Unit Of Conformance");
   RelationTypeSide AbstractSpecRequirementToUnitOfConformance_AbstractSpecRequirement = RelationTypeSide.create(AbstractSpecRequirementToUnitOfConformance, SIDE_A);
   RelationTypeSide AbstractSpecRequirementToUnitOfConformance_UnitOfConformance = RelationTypeSide.create(AbstractSpecRequirementToUnitOfConformance, SIDE_B);
   // @formatter:on

}