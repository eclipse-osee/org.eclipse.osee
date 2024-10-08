/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.artifact.search;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Robert A. Fisher
 */
public class NotInRelationSearch implements ISearchPrimitive {
   private final static String TOKEN = ";";
   private final RelationTypeToken relationType;
   private final Boolean sideA;

   public NotInRelationSearch(RelationTypeToken relationType, Boolean sideA) {
      this.relationType = relationType;
      this.sideA = sideA;
   }

   @Override
   public String toString() {
      return "Not In Relation: " + relationType + " from";
   }

   @Override
   public String getStorageString() {
      return sideA + TOKEN + relationType.getId();
   }

   public static NotInRelationSearch getPrimitive(String storageString, OrcsTokenService tokenService) {
      String[] values = storageString.split(TOKEN);
      if (values.length < 2) {
         throw new IllegalStateException("Value for " + InRelationSearch.class.getSimpleName() + " not parsable");
      }

      RelationTypeToken type = tokenService.getRelationType(Long.valueOf(values[1]));
      return new NotInRelationSearch(type, Boolean.parseBoolean(values[0]));
   }

   @Override
   public void addToQuery(QueryBuilderArtifact builder) {
      if (sideA == null) {
         builder.andNotExists(relationType);
      } else {
         RelationSide side = sideA.booleanValue() ? RelationSide.SIDE_A : RelationSide.SIDE_B;
         RelationTypeSide rts = RelationTypeSide.create(relationType, side);
         builder.andNotExists(rts);
      }
   }
}