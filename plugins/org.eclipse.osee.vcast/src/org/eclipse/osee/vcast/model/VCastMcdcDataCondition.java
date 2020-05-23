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

package org.eclipse.osee.vcast.model;

/**
 * @author Shawn F. Cook
 */
public class VCastMcdcDataCondition {

   private final int id;
   private final int mcdcDataId;
   private final int condIndex;
   private final Boolean condValue;

   public VCastMcdcDataCondition(int id, int mcdcDataId, int condIndex, Boolean condValue) {
      super();
      this.id = id;
      this.mcdcDataId = mcdcDataId;
      this.condIndex = condIndex;
      this.condValue = condValue;
   }

   public int getId() {
      return id;
   }

   public int getMcdcDataId() {
      return mcdcDataId;
   }

   public int getCondIndex() {
      return condIndex;
   }

   public Boolean getCondValue() {
      return condValue;
   }

}
