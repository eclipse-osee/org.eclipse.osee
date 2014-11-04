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
public class VCastStatementCoverage {

   private final int id;
   private final int functionId;
   private final int line;
   private final int hitCount;
   private final int maxHitCount;

   public VCastStatementCoverage(int id, int functionId, int line, int hitCount, int maxHitCount) {
      this.id = id;
      this.functionId = functionId;
      this.line = line;
      this.hitCount = hitCount;
      this.maxHitCount = maxHitCount;
   }

   public int getId() {
      return id;
   }

   public int getFunctionId() {
      return functionId;
   }

   public int getLine() {
      return line;
   }

   public int getHitCount() {
      return hitCount;
   }

   public int getMaxHitCount() {
      return maxHitCount;
   }

}
