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
package org.eclipse.osee.orcs.rest.internal.search.predicate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.HexUtil;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class PredicateHandlerUtil {

   public static Collection<IAttributeType> getIAttributeTypes(List<String> types) throws OseeCoreException {
      Collection<IAttributeType> attrTypes = new HashSet<IAttributeType>();
      for (String value : types) {
         long uuid = parseUuid(value);
         if (uuid != -1L) {
            attrTypes.add(TokenFactory.createAttributeType(uuid, "SearchAttributeType"));
         }
      }
      return attrTypes;
   }

   public static Collection<IArtifactType> getIArtifactTypes(List<String> types) throws OseeCoreException {
      Collection<IArtifactType> artTypes = new HashSet<IArtifactType>();
      for (String value : types) {
         long uuid = parseUuid(value);
         if (uuid != -1L) {
            artTypes.add(TokenFactory.createArtifactType(uuid, "SearchArtifactType"));
         }
      }
      return artTypes;
   }

   public static Collection<IRelationTypeSide> getIRelationTypeSides(List<String> rels) throws OseeCoreException {
      Collection<IRelationTypeSide> relSides = new HashSet<IRelationTypeSide>();
      for (String value : rels) {
         char sideChar = value.charAt(0);
         String uuid = value.substring(1);
         RelationSide side = RelationSide.SIDE_A;
         if (sideChar == 'B') {
            side = RelationSide.SIDE_B;
         }
         long longUuid = parseUuid(uuid);
         if (longUuid != -1L) {
            relSides.add(TokenFactory.createRelationTypeSide(side, longUuid, "SearchRelationTypeSide"));
         }
      }
      return relSides;
   }

   private static long parseUuid(String uuid) throws OseeCoreException {
      if (uuid.matches("\\d+")) {
         return Long.parseLong(uuid);
      } else if (HexUtil.isHexString(uuid)) {
         return HexUtil.toLong(uuid);
      }
      return -1L;
   }
}
