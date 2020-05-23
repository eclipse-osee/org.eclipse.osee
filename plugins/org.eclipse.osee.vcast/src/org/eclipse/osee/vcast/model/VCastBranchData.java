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
public class VCastBranchData {

   private final int id;
   private final Long branchId;
   private final int resultId;
   private final int resultLine;
   private final Boolean taken;

   public VCastBranchData(int id, Long branchId, int resultId, int resultLine, Boolean taken) {
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

   public Long getBranchId() {
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
