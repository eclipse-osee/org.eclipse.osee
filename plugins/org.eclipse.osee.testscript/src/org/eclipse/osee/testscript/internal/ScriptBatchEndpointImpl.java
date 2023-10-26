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

package org.eclipse.osee.testscript.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.testscript.ScriptBatchApi;
import org.eclipse.osee.testscript.ScriptBatchEndpoint;

/**
 * @author Ryan T. Baldwin
 */
public class ScriptBatchEndpointImpl implements ScriptBatchEndpoint {

   private final ScriptBatchApi scriptBatchApi;
   private final BranchId branch;
   public ScriptBatchEndpointImpl(BranchId branch, ScriptBatchApi scriptBatchApi) {
      this.scriptBatchApi = scriptBatchApi;
      this.branch = branch;
   }

   @Override
   public Collection<ScriptBatchToken> getAll(String filter, ArtifactId viewId, long pageNum, long pageSize) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      if (Strings.isValid(filter)) {
         return scriptBatchApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize,
            CoreAttributeTypes.ExecutionDate);
      }
      return scriptBatchApi.getAll(branch, viewId, pageNum, pageSize, CoreAttributeTypes.ExecutionDate);
   }

   @Override
   public Collection<ScriptBatchToken> getAllForSet(ArtifactId setId, String filter, ArtifactId viewId, long pageNum,
      long pageSize) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      return scriptBatchApi.getAllFromScriptSet(branch, setId, viewId, filter, pageNum, pageSize,
         CoreAttributeTypes.ExecutionDate, SortOrder.DESCENDING);
   }

   @Override
   public int getAllForSetCount(ArtifactId setId, String filter, ArtifactId viewId) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      return scriptBatchApi.getAllFromScriptSetCount(branch, setId, viewId, filter);
   }

   @Override
   public ScriptBatchToken getBatch(ArtifactId batchId) {
      return scriptBatchApi.get(branch, batchId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return scriptBatchApi.getCountWithFilter(branch, viewId, filter);
   }
}
