/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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