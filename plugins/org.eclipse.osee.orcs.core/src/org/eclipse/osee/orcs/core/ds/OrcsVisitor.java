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

package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.jdbc.SqlTable;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsVisitor {

   void visit(ArtifactData data);

   <T> void visit(AttributeData<T> data);

   void visit(RelationData data);

   void visit(TupleData data);

   void visit(BranchCategoryData data);

   void deleteTuple(BranchId branch, SqlTable tupleTable, GammaId gammaId);

   void deleteBranchCategory(BranchId branch, GammaId gammaId);

}