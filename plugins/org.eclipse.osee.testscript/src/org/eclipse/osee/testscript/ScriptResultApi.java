/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.testscript.internal.ScriptResultToken;

/**
 * @author Stephen J. Molaro
 */
public interface ScriptResultApi {

   ScriptResultToken get(BranchId branch, ArtifactId scriptDefTypeId);

   ScriptResultToken getWithTestPointsAndFilter(BranchId branch, ArtifactId resultId, String filter, int pageNum,
      int count);

   Collection<ScriptResultToken> getAll(BranchId branch);

   Collection<ScriptResultToken> getAllForSet(BranchId branch, ArtifactId view, ArtifactId setId);

   Collection<ScriptResultToken> getAllForBatch(BranchId branch, ArtifactId viewId, ArtifactId batchId, String filter,
      long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   int getAllForBatchCount(BranchId branch, ArtifactId viewId, ArtifactId batchId, String filter);

   Collection<ScriptResultToken> getAll(BranchId branch, ArtifactId viewId);

   Collection<ScriptResultToken> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   Collection<ScriptResultToken> getAll(BranchId branch, ArtifactId viewId, AttributeTypeId orderByAttribute);

   Collection<ScriptResultToken> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<ScriptResultToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize);

   Collection<ScriptResultToken> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   Collection<ScriptResultToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<ScriptResultToken> getAllByFilter(BranchId branch, String filter);

   Collection<ScriptResultToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter);

   Collection<ScriptResultToken> getAllByFilter(BranchId branch, String filter, AttributeTypeId orderByAttribute);

   Collection<ScriptResultToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      AttributeTypeId orderByAttribute);

   Collection<ScriptResultToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize);

   Collection<ScriptResultToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize);

   Collection<ScriptResultToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<ScriptResultToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   int getCountWithFilter(BranchId branch, ArtifactId viewId, String filter);
}
