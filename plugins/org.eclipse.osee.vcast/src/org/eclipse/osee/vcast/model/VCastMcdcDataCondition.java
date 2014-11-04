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
