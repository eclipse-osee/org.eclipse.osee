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
public class VCastMcdcCoverage {

   private final int id;
   private final int functionId;
   private final int line;
   private final int sourceLine;
   private final int numConditions;
   private final String actualExpr;
   private final String simplifiedExpr;

   public VCastMcdcCoverage(int id, int functionId, int line, int sourceLine, int numConditions, String actualExpr, String simplifiedExpr) {
      super();
      this.id = id;
      this.functionId = functionId;
      this.line = line;
      this.sourceLine = sourceLine;
      this.numConditions = numConditions;
      this.actualExpr = actualExpr;
      this.simplifiedExpr = simplifiedExpr;
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

   public int getSourceLine() {
      return sourceLine;
   }

   public int getNumConditions() {
      return numConditions;
   }

   public String getActualExpr() {
      return actualExpr;
   }

   public String getSimplifiedExpr() {
      return simplifiedExpr;
   }

}
