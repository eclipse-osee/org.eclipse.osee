/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data.conditions;

import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * Note: It enables the widget when at least one value matches the current attribute value.
 *
 * @author Donald G. Dunne
 */
public class RequiredIfInRelationCondition extends ConditionalRule {

   private RelationTypeSide relationSide;

   public RequiredIfInRelationCondition(RelationTypeSide relationSide) {
      this.relationSide = relationSide;
   }

   public RelationTypeSide getRelationSide() {
      return relationSide;
   }

   public void setRelationSide(RelationTypeSide relationSide) {
      this.relationSide = relationSide;
   }

}
