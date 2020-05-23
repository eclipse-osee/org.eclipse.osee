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
public class VCastMcdcData {

   private final int id;
   private final int mcdcId;
   private final int resultId;
   private final int resultLine;
   private final int pairValue;
   private final int usedValue;

   public VCastMcdcData(int id, int mcdcId, int resultId, int resultLine, int pairValue, int usedValue) {
      super();
      this.id = id;
      this.mcdcId = mcdcId;
      this.resultId = resultId;
      this.resultLine = resultLine;
      this.pairValue = pairValue;
      this.usedValue = usedValue;
   }

   public int getId() {
      return id;
   }

   public int getMcdcId() {
      return mcdcId;
   }

   public int getResultId() {
      return resultId;
   }

   public int getResultLine() {
      return resultLine;
   }

   public int getPairValue() {
      return pairValue;
   }

   public int getUsedValue() {
      return usedValue;
   }

}
