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

package org.eclipse.osee.define.rest.synchronization.forest.denizens;

import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * Collection of {@link ArttributeTypeToken} implementations used to represent synthetic native OSEE data within the
 * Synchronization Artifact OSGI bundle.
 *
 * @author Loren K. Ashley
 */

public class AttributeTypeTokens {

   /**
    * A "fake" {@link ArtifactTypeToken} to represent the name of OSEE relationships side A as an attribute value.
    */

   public static AttributeTypeGeneric<?> relationshipSideAAttributeTypeToken =
      osee.createString(0L, "Side A", MediaType.TEXT_PLAIN, "Source to Target Relationship Description");

   /**
    * A "fake" {@link ArtifactTypeToken} to represent the name of OSEE relationships side B as an attribute value.
    */

   public static AttributeTypeGeneric<?> relationshipSideBAttributeTypeToken =
      osee.createString(1L, "Side B", MediaType.TEXT_PLAIN, "Target to Source Relationship Description");

   /**
    * A "fake" {@link ArtifactTypeToken} to represent the OSEE relationship multiplicity as an enumerated attribute
    * value.
    */

   public static AttributeTypeGeneric<?> relationshipMultiplicityAttributeTypeToken =
      osee.createEnum(new AttributeTypeEnum<EnumToken>(2L, NamespaceToken.OSEE, "RelationTypeMultiplicity",
         MediaType.TEXT_PLAIN, "Allowed multiplicity of the relationship.", TaggerTypeToken.PlainTextTagger, 4) {
         {
            this.addEnum(new EnumToken(0, "ONE_TO_ONE"));
            this.addEnum(new EnumToken(1, "ONE_TO_MANY"));
            this.addEnum(new EnumToken(2, "MANY_TO_ONE"));
            this.addEnum(new EnumToken(3, "MANY_TO_MANY"));
         }
      });

   /**
    * A "fake" {@link ArtifactTypeToken} to represent the OSEE artifact identifier of the OSEE artifact represented by a
    * Specter Spec Object.
    */
   public static AttributeTypeGeneric<?> specterSpecObjectArtifactIdentifierAttributeTypeToken =
      osee.createArtifactId(3L, "Artifact Identifier", MediaType.TEXT_PLAIN, "Specter Spec Object Artifact Identifier");
}

/* EOF */
