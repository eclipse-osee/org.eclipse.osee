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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.testscript.ScriptSetApi;
import org.eclipse.osee.testscript.ScriptSetEndpoint;

/**
 * @author Stephen J. Molaro
 */
public class ScriptSetEndpointImpl implements ScriptSetEndpoint {

   private final ScriptSetApi scriptSetApi;
   private final BranchId branch;
   public ScriptSetEndpointImpl(BranchId branch, ScriptSetApi scriptProgramApi) {
      this.scriptSetApi = scriptProgramApi;
      this.branch = branch;
   }

   @Override
   public Collection<ScriptSetToken> getAllScriptSets(String filter, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType, boolean activeOnly) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      if (Strings.isValid(filter)) {
         return scriptSetApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType,
            activeOnly);
      }
      return scriptSetApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType, activeOnly);
   }

   @Override
   public ScriptSetToken getScriptProgram(ArtifactId ScriptProgramId) {
      return scriptSetApi.get(branch, ScriptProgramId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return scriptSetApi.getCountWithFilter(branch, viewId, filter);
   }

}
