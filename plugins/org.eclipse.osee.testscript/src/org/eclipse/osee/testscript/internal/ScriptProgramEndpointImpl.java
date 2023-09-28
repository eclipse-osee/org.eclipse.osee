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
import org.eclipse.osee.testscript.ScriptProgramApi;
import org.eclipse.osee.testscript.ScriptProgramEndpoint;

/**
 * @author Stephen J. Molaro
 */
public class ScriptProgramEndpointImpl implements ScriptProgramEndpoint {

   private final ScriptProgramApi scriptProgramApi;
   private final BranchId branch;
   public ScriptProgramEndpointImpl(BranchId branch, ScriptProgramApi scriptProgramApi) {
      this.scriptProgramApi = scriptProgramApi;
      this.branch = branch;
   }

   @Override
   public Collection<ScriptProgramToken> getAllScriptPrograms(String filter, ArtifactId viewId, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeType) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      if (Strings.isValid(filter)) {
         return scriptProgramApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return scriptProgramApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public ScriptProgramToken getScriptProgram(ArtifactId ScriptProgramId) {
      return scriptProgramApi.get(branch, ScriptProgramId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return scriptProgramApi.getCountWithFilter(branch, viewId, filter);
   }

}
