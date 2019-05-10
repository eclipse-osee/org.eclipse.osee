/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.search.artifact.predicate;

import java.util.Collection;
import java.util.LinkedHashSet;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class PredicateHandlerUtil {

   public static Collection<AttributeTypeId> getAttributeTypes(Collection<String> types) {
      Collection<AttributeTypeId> attrTypes = new LinkedHashSet<>();
      for (String value : types) {
         long uuid = parseUuid(value);
         if (uuid != -1L) {
            attrTypes.add(AttributeTypeId.valueOf(uuid));
         }
      }
      return attrTypes;
   }

   public static Collection<ArtifactTypeToken> getArtifactTypeTokens(Collection<String> types) {
      Collection<ArtifactTypeToken> artTypes = new LinkedHashSet<>();
      for (String value : types) {
         long uuid = parseUuid(value);
         if (uuid != -1L) {
            artTypes.add(ArtifactTypeToken.valueOf(uuid, "SearchArtifactType"));
         }
      }
      return artTypes;
   }

   public static Collection<IRelationType> getIRelationTypes(Collection<String> rels) {
      Collection<IRelationType> types = new LinkedHashSet<>();
      for (String value : rels) {
         long longUuid = parseUuid(value);
         if (longUuid != -1L) {
            types.add(RelationTypeToken.create(longUuid, "SearchRelationType"));
         }
      }
      return types;
   }

   public static Collection<RelationTypeSide> getRelationTypeSides(Collection<String> rels) {
      Collection<RelationTypeSide> relSides = new LinkedHashSet<>();
      for (String value : rels) {
         char sideChar = value.charAt(0);
         String uuid = value.substring(1);
         RelationSide side = RelationSide.SIDE_A;
         if (sideChar == 'B') {
            side = RelationSide.SIDE_B;
         }
         long longUuid = parseUuid(uuid);
         if (longUuid != -1L) {
            relSides.add(RelationTypeSide.create(side, longUuid, "SearchRelationTypeSide"));
         }
      }
      return relSides;
   }

   private static long parseUuid(String uuid) {
      if (uuid.matches("-?\\d+")) {
         return Long.parseLong(uuid);
      }
      return -1L;
   }
}
