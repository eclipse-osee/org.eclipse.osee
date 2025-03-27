/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.testscript;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;

/**
 * @author Ryan T. Baldwin
 */
public interface ScriptPurgeApi {

   TransactionResult purgeResults(BranchId branch, boolean deleteOnly);

   public Collection<ResultToPurge> getDeletedResults(BranchId branch);

}
