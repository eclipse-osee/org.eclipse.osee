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
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.testscript.internal.ScriptBatchToken;

/**
 * @author Ryan T. Baldwin
 */
public interface ScriptBatchApi {

   ScriptBatchToken get(BranchId branch, ArtifactId batchId);

   Collection<ScriptBatchToken> getAllFromScriptSet(BranchId branch, ArtifactId setId, ArtifactId viewId, String filter,
      long pageNum, long pageSize, AttributeTypeId orderByAttributeType, SortOrder orderByAttributeDirection);

   int getAllFromScriptSetCount(BranchId branch, ArtifactId setId, ArtifactId viewId, String filter);

   Collection<ScriptBatchToken> getAll(BranchId branch);

   Collection<ScriptBatchToken> getAll(BranchId branch, ArtifactId viewId);

   Collection<ScriptBatchToken> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   Collection<ScriptBatchToken> getAll(BranchId branch, ArtifactId viewId, AttributeTypeId orderByAttribute);

   Collection<ScriptBatchToken> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<ScriptBatchToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize);

   Collection<ScriptBatchToken> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   Collection<ScriptBatchToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<ScriptBatchToken> getAllByFilter(BranchId branch, String filter);

   Collection<ScriptBatchToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter);

   Collection<ScriptBatchToken> getAllByFilter(BranchId branch, String filter, AttributeTypeId orderByAttribute);

   Collection<ScriptBatchToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      AttributeTypeId orderByAttribute);

   Collection<ScriptBatchToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize);

   Collection<ScriptBatchToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize);

   Collection<ScriptBatchToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<ScriptBatchToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   int getCountWithFilter(BranchId branch, ArtifactId viewId, String filter);

}
