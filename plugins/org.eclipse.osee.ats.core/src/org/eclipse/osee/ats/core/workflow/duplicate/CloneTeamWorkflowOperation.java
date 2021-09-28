/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow.duplicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.clone.CloneData;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.workflow.util.DuplicateWorkflowAsIsOperation;
import org.eclipse.osee.ats.core.workflow.util.IDuplicateWorkflowListener;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CloneTeamWorkflowOperation implements IDuplicateWorkflowListener {

   private final CloneData data;
   private final AtsApi atsApi;
   private final IDuplicateWorkflowListener duplicateListener;
   private final IAtsTeamWorkflow teamWf;

   public CloneTeamWorkflowOperation(IAtsTeamWorkflow teamWf, IDuplicateWorkflowListener duplicateListener //
      , CloneData cloneData, AtsApi atsApi) {
      this.teamWf = teamWf;
      this.duplicateListener = duplicateListener;
      this.data = cloneData;
      this.atsApi = atsApi;
   }

   public CloneData run() {
      List<IDuplicateWorkflowListener> listeners = new ArrayList<IDuplicateWorkflowListener>();
      if (duplicateListener != null) {
         listeners.add(duplicateListener);
      }
      listeners.add(this);
      boolean newAction = data.isCreateNewAction();
      String newTitle = data.getTitle();

      // Duplicate action
      DuplicateWorkflowAsIsOperation dupOp = new DuplicateWorkflowAsIsOperation(Arrays.asList(teamWf), false, newTitle,
         AtsApiService.get().getUserService().getCurrentUser(), AtsApiService.get(),
         "Clone from " + teamWf.toStringWithId(), newAction, listeners);

      // Originator
      AtsUser orig = data.getOriginator();
      dupOp.setOriginator(orig);

      // Description
      String desc = data.getDesc();
      if (Strings.isInValid(desc)) {
         desc = teamWf.getDescription();
      }
      dupOp.setDescription(desc);

      dupOp.setAssignees(data.getAssignees());

      dupOp.setChangeType(data.getChangeType());

      dupOp.setPriority(data.getPriority());

      dupOp.setPoints(data.getPoints());

      dupOp.run();

      if (data.getResults().isSuccess()) {
         data.setNewTeamWf(dupOp.getResults().get(teamWf).getArtifactId());
      }

      return data;
   }

   @Override
   public boolean handleChanges(IAtsTeamWorkflow newTeamWf, IAtsChangeSet changes) {

      if (data.getTargetedVersion() == null) {
         atsApi.getVersionService().setTargetedVersion(newTeamWf, null, changes);
      } else {
         atsApi.getVersionService().setTargetedVersion(newTeamWf, data.getTargetedVersion(), changes);
      }

      if (data.getSprint() == null) {
         atsApi.getAgileService().setSprint(newTeamWf, null, changes);
      } else {
         atsApi.getAgileService().setSprint(newTeamWf, data.getSprint(), changes);
      }

      IAgileTeam aTeam = atsApi.getAgileService().getAgileTeam(teamWf);
      if (aTeam != null) {
         Collection<IAgileFeatureGroup> featureGroups = atsApi.getAgileService().getAgileFeatureGroups(aTeam);
         for (String featureStr : data.getFeatures()) {
            for (IAgileFeatureGroup grp : featureGroups) {
               if (grp.getName().equals(featureStr)) {
                  changes.relate(grp, AtsRelationTypes.AgileFeatureToItem_AtsItem, newTeamWf);
                  break;
               }
            }
         }
      }
      return true;
   }

}
