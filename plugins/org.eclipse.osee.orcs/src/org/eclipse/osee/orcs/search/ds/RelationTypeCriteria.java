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

package org.eclipse.osee.orcs.search.ds;

import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Ryan D. Brooks
 */
public class RelationTypeCriteria extends Criteria {

   private RelationTypeToken relationType;

   public RelationTypeCriteria(RelationTypeToken relationType) {
      this.relationType = relationType;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNull(relationType, "relation type");
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + " [relationType=" + relationType + "]";
   }

   public RelationTypeToken getRelationType() {
      return relationType;
   }

   public void setRelationType(RelationTypeToken relationType) {
      this.relationType = relationType;
   }
}