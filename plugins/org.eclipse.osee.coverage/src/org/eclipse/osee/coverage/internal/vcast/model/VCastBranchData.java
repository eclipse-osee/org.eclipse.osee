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
package org.eclipse.osee.coverage.internal.vcast.model;

/**
 * @author Shawn F. Cook
 */
public class VCastBranchData {

   private final int id;
   private final long branchId;
   private final int resultId;
   private final int resultLine;
   private final Boolean taken;

   public VCastBranchData(int id, long branchId, int resultId, int resultLine, Boolean taken) {
      super();
      this.id = id;
      this.branchId = branchId;
      this.resultId = resultId;
      this.resultLine = resultLine;
      this.taken = taken;
   }

   public int getId() {
      return id;
   }

   public long getBranchId() {
      return branchId;
   }

   public int getResultId() {
      return resultId;
   }

   public int getResultLine() {
      return resultLine;
   }

   public Boolean getTaken() {
      return taken;
   }

}
