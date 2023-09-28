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
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.testscript.ScriptDefApi;
import org.eclipse.osee.testscript.ScriptDefEndpoint;

/**
 * @author Stephen J. Molaro
 */
public class ScriptDefEndpointImpl implements ScriptDefEndpoint {

   private final ScriptDefApi scriptDefTypeApi;
   private final BranchId branch;
   public ScriptDefEndpointImpl(BranchId branch, ScriptDefApi scriptDefTypeApi) {
      this.scriptDefTypeApi = scriptDefTypeApi;
      this.branch = branch;
   }

   @Override
   public Collection<ScriptDefToken> getAllScriptDefTypes(String filter, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      if (Strings.isValid(filter)) {
         return scriptDefTypeApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return scriptDefTypeApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public ArtifactAccessorResult getScriptDefType(ArtifactId scriptDefTypeId) {
      return scriptDefTypeApi.get(branch, scriptDefTypeId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return scriptDefTypeApi.getCountWithFilter(branch, viewId, filter);
   }

}
