/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.search.ds;

import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class RelationTypeSideCriteria extends Criteria {

   private RelationTypeSide relationTypeSide;

   public RelationTypeSideCriteria(RelationTypeSide relationTypeSide) {
      this.relationTypeSide = relationTypeSide;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNull(relationTypeSide, "relation type side");
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + " [relationTypeSide=" + relationTypeSide + "]";
   }

   public RelationTypeSide getRelationTypeSide() {
      return relationTypeSide;
   }

   public void setRelationTypeSide(RelationTypeSide relationTypeSide) {
      this.relationTypeSide = relationTypeSide;
   }

}