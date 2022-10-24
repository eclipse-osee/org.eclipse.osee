/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.operations.synchronization.forest.denizens;

import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
import java.util.Objects;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Class of static factory methods used to generate synthetic {@link ArtifactTypeToken} implementations used to
 * represent synthetic native OSEE data with in the Synchronization Artifact OSGI bundle.
 *
 * @author Loren K. Ashley
 */

public class ArtifactTypeTokens {

   /**
    * Saves an instance of {@link ArtifactTypeToken} to define the attributes for Specter Spec Objects.
    */

   private static ArtifactTypeToken specterSpecObjectArtifactTypeToken = null;

   /**
    * Creates a identifier that is not currently used by an ArtifactTypeToken.
    *
    * @param orcsTokenService handle to the token service.
    * @return a {@link Long} identifier.
    * @throws RuntimeException when a unique identifier cannot be found in 50 tries.
    */

   private static Long createArtifactTypeTokenId(OrcsTokenService orcsTokenService) {
      for (int safety = 0; safety < 50; safety++) {
         Long id = Lib.generateId();
         if (orcsTokenService.getArtifactTypeOrSentinel(id) == ArtifactTypeToken.SENTINEL) {
            return id;
         }
      }

      throw new RuntimeException("Failed to generate unique identifier for Specter Spec Object ArtifactTypeToken.");
   }

   /**
    * Creates an {@link ArtifactTypeToken} to represent the specified relationship type. The created
    * {@link ArtifactTypeToken} will have the same {@link Long} identifier and name as the provided
    * {@link RelationTypeToken}. The {@link ArtifactTypeToken} will be defined with the following attributes:
    * <dl>
    * <dt>Relationship Side A Name:</dt>
    * <dd>A string attribute to hold the relationship type side A name.</dd>
    * <dt>Relationship Side B Name:</dt>
    * <dd>A string attribute to hold the relationship type side B name.</dd>
    * <dt>Relationship Multiplicity:</dt>
    * <dd>A string attribute to hold the string representation of the relationship type multiplicity enumeration
    * value.</dd>
    * </dl>
    *
    * @param nativeRelationTypeToken the {@link RelationTypeToken} representing the relationship type to create the
    * {@link ArtifactTypeToken} for.
    * @return an {@link ArtifactTypeToken} implementation to represent the specified relationship type.
    */

   public static ArtifactTypeToken createSpecRelationTypeArtifactTypeToken(RelationTypeToken nativeRelationTypeToken) {
      //@formatter:off
      return
         osee
            .artifactType
               (
                  nativeRelationTypeToken.getId(),
                  nativeRelationTypeToken.getName(),
                  false
               )
            .exactlyOne( AttributeTypeTokens.relationshipSideAAttributeTypeToken        )
            .exactlyOne( AttributeTypeTokens.relationshipSideBAttributeTypeToken        )
            .exactlyOne( AttributeTypeTokens.relationshipMultiplicityAttributeTypeToken )
            .get();
      //@formatter:on
   }

   /**
    * Creates an {@link ArtifactTypeToken} to represent the attribute definitions for Specter Spec Objects. The
    * {@link ArtifactTypeToken} will be defined with the following attributes:
    * <dl>
    * <dt>Spec Object Artifact Identifier</dt>
    * <dd>An Artifact Identifier attribute to hold the Artifact Identifier of the OSEE Artifact represented by the
    * Specter.</dd>
    * </dl>
    *
    * @return an {@link ArtifactTypeToken} implementation to represent the attribute definition for Specter Spec
    * Objects.
    */

   public static ArtifactTypeToken createSpecterSpecObjectArtifactTypeToken(OrcsTokenService orcsTokenService) {

      /*
       * If cached, return cached value
       */

      if (Objects.nonNull(ArtifactTypeTokens.specterSpecObjectArtifactTypeToken)) {
         return ArtifactTypeTokens.specterSpecObjectArtifactTypeToken;
      }

      /*
       * Create and save the artifact type token.
       */

      //@formatter:off
      ArtifactTypeTokens.specterSpecObjectArtifactTypeToken =
         osee
            .artifactType
               (
                  ArtifactTypeTokens.createArtifactTypeTokenId( orcsTokenService ),
                  "Synchronization Artifact Specter Spec Object",
                  false
               )
            .exactlyOne( AttributeTypeTokens.specterSpecObjectArtifactIdentifierAttributeTypeToken )
            .get();
      //@formatter:on

      return ArtifactTypeTokens.specterSpecObjectArtifactTypeToken;
   }
}

/* EOF */
