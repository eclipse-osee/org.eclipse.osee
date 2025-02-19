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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.ds.FollowRelation;
import org.eclipse.osee.testscript.DashboardApi;
import org.eclipse.osee.testscript.ScriptDefApi;
import org.eclipse.osee.testscript.ScriptDefEndpoint;
import org.eclipse.osee.testscript.ScriptDefToken;
import org.eclipse.osee.testscript.ScriptTeamToken;

/**
 * @author Stephen J. Molaro
 */
public class ScriptDefEndpointImpl implements ScriptDefEndpoint {

   private final ScriptDefApi scriptDefApi;
   private final DashboardApi dashboardApi;
   private final BranchId branch;
   public ScriptDefEndpointImpl(BranchId branch, ScriptDefApi scriptDefTypeApi, DashboardApi dashboardApi) {
      this.scriptDefApi = scriptDefTypeApi;
      this.dashboardApi = dashboardApi;
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
   public Collection<ScriptDefToken> getScriptDefBySet(ArtifactId scriptSetId, long pageNum, long pageSize) {
      try {
         String filter = scriptSetId.getIdString();
         if (scriptSetId.isValid()) {
            Collection<ScriptDefToken> scripts = scriptDefApi.getAllByFilter(branch, filter,
               Arrays.asList(
                  FollowRelation.follow(CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptResults)),
               pageNum, pageSize, CoreAttributeTypes.Name, Arrays.asList(CoreAttributeTypes.SetId));
            populateScriptTeams(scripts);
            return scripts;
         }
         return Collections.emptyList();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private void populateScriptTeams(Collection<ScriptDefToken> scripts) {
      Map<ArtifactId, ScriptTeamToken> teams = new HashMap<>();
      for (ScriptDefToken script : scripts) {
         ArtifactId teamId = script.getTeam().getArtifactId();
         if (teamId.isInvalid()) {
            continue;
         }
         ScriptTeamToken team = teams.get(teamId);
         if (team == null) {
            team = this.dashboardApi.getTeam(branch, teamId);
            if (team.getArtifactId().isValid()) {
               teams.put(team.getArtifactId(), team);
            }
         }
         if (team.getArtifactId().isValid()) {
            script.setTeam(team);
         }
      }
   }

   @Override
   public int getCountForSet(ArtifactId scriptSetId, ArtifactId viewId) {
      try {
         String filter = scriptSetId.getIdString();
         if (scriptSetId.isValid()) {
            return scriptDefApi.getAllByFilterAndCount(branch, filter,
               FollowRelation.followList(CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptResults),
               Arrays.asList(CoreAttributeTypes.SetId), viewId);
         }
         return 0;
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
      return scriptDefApi.getCountWithFilter(branch, viewId, filter, Arrays.asList(CoreAttributeTypes.Name));
   }
}
