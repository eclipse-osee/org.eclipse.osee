/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.rest.internal.search.artifact.predicate;

import java.util.Collection;
import java.util.LinkedHashSet;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class PredicateHandlerUtil {

   public static Collection<AttributeTypeToken> getAttributeTypes(Collection<String> types) {
      Collection<AttributeTypeToken> attrTypes = new LinkedHashSet<>();
      for (String value : types) {
         long uuid = parseUuid(value);
         if (uuid != -1L) {
            attrTypes.add(AttributeTypeToken.valueOf(uuid, ""));
         }
      }
      return attrTypes;
   }

   public static Collection<RelationTypeToken> getIRelationTypes(Collection<String> rels,
      OrcsTokenService tokenService) {
      Collection<RelationTypeToken> types = new LinkedHashSet<>();
      for (String value : rels) {
         long longUuid = parseUuid(value);
         if (longUuid != -1L) {
            types.add(tokenService.getRelationType(longUuid));
         }
      }
      return types;
   }

   public static Collection<RelationTypeSide> getRelationTypeSides(Collection<String> rels,
      OrcsTokenService orcsTokenService) {
      Collection<RelationTypeSide> relSides = new LinkedHashSet<>();
      for (String value : rels) {
         char sideChar = value.charAt(0);
         String uuid = value.substring(1);
         RelationSide side = RelationSide.SIDE_A;
         if (sideChar == 'B') {
            side = RelationSide.SIDE_B;
         }
         long relationTypeId = parseUuid(uuid);
         if (relationTypeId != -1L) {
            relSides.add(RelationTypeSide.create(orcsTokenService.getRelationType(relationTypeId), side));
         }
      }
      return relSides;
   }

   public static long parseUuid(String uuid) {
      if (uuid.matches("-?\\d+")) {
         return Long.parseLong(uuid);
      }
      return -1L;
   }
}
