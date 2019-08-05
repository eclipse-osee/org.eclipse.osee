/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author John Misinco
 */
public final class CriteriaRelationTypeSideNotExists extends Criteria {
   private final RelationTypeId relationType;
   private final RelationSide side;

   public CriteriaRelationTypeSideNotExists(RelationTypeId relationType, RelationSide side) {
      this.relationType = relationType;
      this.side = side;
   }

   public RelationTypeId getType() {
      return relationType;
   }

   public RelationSide getSide() {
      return side;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkValid(relationType, "relation type");
   }

   @Override
   public String toString() {
      return "CriteriaRelationTypeSideNotExists [relationType=" + relationType + " side=" + side + "]";
   }
}