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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.ds.FollowRelation;
import org.eclipse.osee.testscript.ScriptDefApi;
import org.eclipse.osee.testscript.ScriptDefEndpoint;

/**
 * @author Stephen J. Molaro
 */
public class ScriptDefEndpointImpl implements ScriptDefEndpoint {

   private final ScriptDefApi scriptDefApi;
   private final BranchId branch;
   public ScriptDefEndpointImpl(BranchId branch, ScriptDefApi scriptDefTypeApi) {
      this.scriptDefApi = scriptDefTypeApi;
      this.branch = branch;
   }

   @Override
   public Collection<ScriptDefToken> getAllScriptDefs(String filter, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      if (Strings.isValid(filter)) {
         return scriptDefApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return scriptDefApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public Collection<ScriptDefToken> getScriptDefBySet(ArtifactId scriptSetId) {
      try {
         String filter = scriptSetId.getIdString();
         if (scriptSetId.isValid()) {
            return scriptDefApi.getAllByFilter(branch, filter,
               FollowRelation.followList(CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptResults), 0L, 0L,
               AttributeTypeId.SENTINEL, Arrays.asList(CoreAttributeTypes.SetId));
         }
         return List.of();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public ScriptDefToken getScriptDefType(ArtifactId scriptDefTypeId) {
      return scriptDefApi.get(branch, scriptDefTypeId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return scriptDefApi.getCountWithFilter(branch, viewId, filter);
   }
}
