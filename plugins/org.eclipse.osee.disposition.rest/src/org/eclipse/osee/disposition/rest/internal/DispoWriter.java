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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;

/**
 * @author Angel Avila
 */
public interface DispoWriter {

   Long createDispoProgram(String name);

   ArtifactId createDispoSet(BranchId branch, DispoSet descriptor);

   void updateDispoSet(BranchId branch, String dispoSetId, DispoSet data);

   boolean deleteDispoSet(BranchId branch, String setId);

   void createDispoItem(BranchId branch, DispoSet parentSet, DispoItem data);

   void createDispoItems(BranchId branch, DispoSet parentSet, List<DispoItem> data);

   boolean deleteDispoItem(BranchId branch, String itemId);

   void updateDispoItem(BranchId branch, String dispoItemId, DispoItem data);

   void updateDispoItems(BranchId branch, Collection<DispoItem> data, boolean resetRerunFlag, String operation);

   void updateOperationSummary(BranchId branch, String setId, OperationReport summary);

   String createDispoReport(BranchId branch, String contens, String operationTitle);

}