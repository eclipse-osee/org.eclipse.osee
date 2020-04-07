/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.task.related;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.task.ITaskEditorProvider;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditorProvider;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 * @author Megumi Telles
 */
public class TaskEditorRelatedTasksProvider extends TaskEditorProvider {

   private final static String BY_BUILD = "(By Build) ";
   private final static String BY_PROG = "(By Program) ";
   private final static String SHOW_ALL = "(Show All) ";
   private final boolean showAll;
   private final boolean byBuild;
   private final Collection<String> searchStrs;
   private final Collection<TaskArtifact> taskArts;
   private TaskArtifact taskArtifact;

   public TaskEditorRelatedTasksProvider(Collection<String> searchStrs, Collection<TaskArtifact> taskArts, boolean showAll, boolean byBuild) {
      super(null, TableLoadOption.None);
      this.searchStrs = searchStrs;
      this.taskArts = taskArts;
      this.showAll = showAll;
      this.byBuild = byBuild;
   }

   private String getSearchType() {
      return !showAll && !byBuild ? BY_PROG : !showAll ? BY_BUILD : SHOW_ALL;
   }

   @Override
   public String getTaskEditorLabel(SearchType searchType) {
      return Strings.truncate(getSearchType() + "Tasks for \"" + searchStrs + "\"", WorldEditor.TITLE_MAX_LENGTH, true);
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      Collection<Artifact> arts = new ArrayList<>();
      List<Artifact> allArtifacts = new ArrayList<>();

      for (String searchStr : searchStrs) {
         allArtifacts.addAll(ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.Task, searchStr,
            AtsClientService.get().getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS));
      }

      if (!showAll && !byBuild) {
         for (TaskArtifact taskArt : taskArts) {
            IAtsTeamDefinition teamDef = AtsClientService.get().getTeamDefinitionService().getTeamDefHoldingVersions(
               taskArt.getParentTeamWorkflow().getTeamDefinition());
            for (Artifact art : allArtifacts) {
               taskArtifact = (TaskArtifact) art;
               IAtsTeamDefinition teamDefinitionHoldingVersions =
                  AtsClientService.get().getTeamDefinitionService().getTeamDefHoldingVersions(
                     taskArtifact.getParentTeamWorkflow().getTeamDefinition());
               if (teamDefinitionHoldingVersions != null && teamDefinitionHoldingVersions.equals(teamDef)) {
                  arts.add(taskArtifact);
               }
            }
         }
         // return program related artifacts
         return arts;
      } else if (!showAll && byBuild) {
         for (TaskArtifact taskArt : taskArts) {
            String targetVer = AtsClientService.get().getVersionService().getTargetedVersionStr(taskArt,
               AtsClientService.get().getVersionService());
            for (Artifact art : allArtifacts) {
               TaskArtifact taskArtifact = (TaskArtifact) art;
               if (AtsClientService.get().getVersionService().getTargetedVersionStr(taskArtifact,
                  AtsClientService.get().getVersionService()).equals(targetVer)) {
                  arts.add(taskArtifact);
               }
            }
         }
         // return build related artifacts
         return arts;
      } else {
         // otherwise return all related artifacts
         return allArtifacts;
      }
   }

   @Override
   public String getName() {
      return getTaskEditorLabel(SearchType.Search);
   }

   @Override
   public ITaskEditorProvider copyProvider() {
      return new TaskEditorRelatedTasksProvider(searchStrs, taskArts, showAll, byBuild);
   }

}
