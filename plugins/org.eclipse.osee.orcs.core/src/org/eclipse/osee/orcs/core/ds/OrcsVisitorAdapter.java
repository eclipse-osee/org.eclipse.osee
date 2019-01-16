/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.TableEnum;

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
   public void deleteTuple(BranchId branch, TableEnum tupleTable, GammaId gammaId) {
      //
   }
}