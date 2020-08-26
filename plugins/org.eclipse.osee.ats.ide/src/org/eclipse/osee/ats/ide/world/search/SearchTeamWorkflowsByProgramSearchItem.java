/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.world.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.config.editor.AtsConfigLabelProvider;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;

/**
 * @author Donald G. Dunne
 */
public class SearchTeamWorkflowsByProgramSearchItem extends WorldUISearchItem {

   private final boolean includeCompletedCancelled;
   private IAtsProgram program;

   public SearchTeamWorkflowsByProgramSearchItem(String name, IAtsProgram program, boolean includeCompletedCancelled) {
      super(name, AtsImage.GLOBE);
      this.program = program;
      this.includeCompletedCancelled = includeCompletedCancelled;
   }

   public SearchTeamWorkflowsByProgramSearchItem(SearchTeamWorkflowsByProgramSearchItem searchItem) {
      super(searchItem.getName(), AtsImage.GLOBE);
      this.includeCompletedCancelled = searchItem.includeCompletedCancelled;
      this.program = searchItem.program;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      if (isCancelled()) {
         return EMPTY_SET;
      }
      List<Artifact> arts = new ArrayList<>();

      AtsSearchData data = new AtsSearchData(getName());
      data.getWorkItemTypes().add(WorkItemType.TeamWorkflow);
      for (IAtsTeamDefinition teamDef : AtsApiService.get().getProgramService().getTeamDefs(program)) {
         data.getTeamDefIds().add(teamDef.getId());
      }
      if (!includeCompletedCancelled) {
         data.setStateTypes(Arrays.asList(StateType.Working));
      }
      arts.addAll(Collections.castAll(AtsApiService.get().getQueryService().getArtifacts(data, null)));

      return arts;
   }

   @Override
   public void performUI(SearchType searchType) {
      super.performUI(searchType);
      if (program == null) {
         FilteredTreeDialog dialog = new FilteredTreeDialog("Select Program", "Select Program",
            new ArrayTreeContentProvider(), new AtsConfigLabelProvider(null));
         dialog.setInput(AtsApiService.get().getProgramService().getPrograms());
         dialog.setMultiSelect(false);
         if (dialog.open() == 0) {
            program = dialog.getSelectedFirst();
         } else {
            cancelled = true;
         }
      }
   }

   @Override
   public WorldUISearchItem copy() {
      return new SearchTeamWorkflowsByProgramSearchItem(this);
   }

}
