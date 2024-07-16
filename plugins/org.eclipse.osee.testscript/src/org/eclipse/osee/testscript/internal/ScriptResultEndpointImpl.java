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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.testscript.ScriptResultApi;
import org.eclipse.osee.testscript.ScriptResultEndpoint;

/**
 * @author Stephen J. Molaro
 */
public class ScriptResultEndpointImpl implements ScriptResultEndpoint {

   private final ScriptResultApi scriptResultTypeApi;
   private final BranchId branch;
   public ScriptResultEndpointImpl(BranchId branch, ScriptResultApi scriptResultTypeApi) {
      this.scriptResultTypeApi = scriptResultTypeApi;
      this.branch = branch;
   }

   @Override
   public Collection<ScriptResultToken> getAllScriptResultTypes(String filter, ArtifactId viewId, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeType) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      if (Strings.isValid(filter)) {
         return scriptResultTypeApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return scriptResultTypeApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public ScriptResultToken getScriptResultType(ArtifactId scriptResultTypeId) {
      return scriptResultTypeApi.get(branch, scriptResultTypeId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return scriptResultTypeApi.getCountWithFilter(branch, viewId, filter);
   }

   @Override
   public List<ScriptResultToken> getScriptResultsByDef(ArtifactId scriptDefId) {
      try {
         List<ScriptResultToken> scriptResults = new ArrayList<>(this.scriptResultTypeApi.getAllByRelation(branch,
            CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptDef, scriptDefId));
         Collections.sort(scriptResults, Comparator.comparing(ScriptResultToken::getExecutionDate));
         return scriptResults;
      } catch (Exception ex) {
         return new LinkedList<ScriptResultToken>();
      }
   }

   @Override
   public Collection<ScriptResultToken> getAllForBatch(ArtifactId batchId, String filter, ArtifactId viewId,
      long pageNum, long pageSize, AttributeTypeToken orderByAttributeType) {
      return this.scriptResultTypeApi.getAllForBatch(branch, viewId, batchId, filter, pageNum, pageSize,
         orderByAttributeType);
   }

   @Override
   public int getAllForBatchCount(ArtifactId batchId, String filter, ArtifactId viewId) {
      return this.scriptResultTypeApi.getAllForBatchCount(branch, viewId, batchId, filter);
   }

   @Override
   public ScriptResultToken getScriptResultWithDetails(ArtifactId scriptResultId, String filter, int pageNum,
      int count) {
      return scriptResultTypeApi.getWithTestPointsAndFilter(branch, scriptResultId, filter, pageNum, count);
   }

}
