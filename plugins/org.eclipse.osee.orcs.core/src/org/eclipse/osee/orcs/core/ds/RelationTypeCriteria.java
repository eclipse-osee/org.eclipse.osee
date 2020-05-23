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

package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Ryan D. Brooks
 */
public class RelationTypeCriteria<R extends RelationTypeToken> extends Criteria {

   private final R relationType;

   public RelationTypeCriteria(R relationType) {
      this.relationType = relationType;
   }

   public R getType() {
      return relationType;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkValid(relationType, "relation type");
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + " [relationType=" + relationType + "]";
   }
}