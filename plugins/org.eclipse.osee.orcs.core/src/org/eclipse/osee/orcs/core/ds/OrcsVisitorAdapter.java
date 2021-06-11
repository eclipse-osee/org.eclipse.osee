/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.jdbc.SqlTable;

/**
 * @author Roberto E. Escobar
 */
public class OrcsVisitorAdapter implements OrcsVisitor {

   @Override
   public void visit(ArtifactData data) {
      //
   }

   @Override
   public <T> void visit(AttributeData<T> data) {
      //
   }

   @Override
   public void visit(RelationData data) {
      //
   }

   @Override
   public void visit(TupleData data) {
      //
   }

   @Override
   public void deleteTuple(BranchId branch, SqlTable tupleTable, GammaId gammaId) {
      //
   }

   @Override
   public void visit(BranchCategoryData data) {
      //
   }

   @Override
   public void deleteBranchCategory(BranchId branch, GammaId gammaId) {
      //
   }
}