/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.disposition.rest.internal;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;

/**
 * @author Angel Avila
 */
public interface DispoWriter {

   Long createDispoProgram(UserId author, String name);

   Long createDispoSet(UserId author, BranchId branch, DispoSet descriptor);

   void updateDispoSet(UserId author, BranchId branch, String dispoSetId, DispoSet data);

   boolean deleteDispoSet(UserId author, BranchId branch, String setId);

   void createDispoItem(UserId author, BranchId branch, DispoSet parentSet, DispoItem data);

   void createDispoItems(UserId author, BranchId branch, DispoSet parentSet, List<DispoItem> data);

   boolean deleteDispoItem(UserId author, BranchId branch, String itemId);

   void updateDispoItem(UserId author, BranchId branch, String dispoItemId, DispoItem data);

   void updateDispoItems(UserId author, BranchId branch, Collection<DispoItem> data, boolean resetRerunFlag, String operation);

   void updateOperationSummary(UserId author, BranchId branch, String setId, OperationReport summary);

   String createDispoReport(BranchId branch, UserId author, String contens, String operationTitle);

}