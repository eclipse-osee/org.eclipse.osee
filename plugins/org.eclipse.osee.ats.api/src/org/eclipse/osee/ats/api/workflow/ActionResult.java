/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ActionResult {

   private IAtsAction action;
   private final Collection<IAtsTeamWorkflow> teamWfs;
   private final XResultData results = new XResultData();

   public ActionResult(IAtsAction action, List<IAtsTeamWorkflow> teamWfs) {
      this.action = action;
      this.teamWfs = teamWfs;
   }

   public IAtsAction getAction() {
      return action;
   }

   public void setAction(IAtsAction action) {
      this.action = action;
   }

   public ArtifactId getActionArt() {
      return action.getStoreObject();
   }

   public Collection<IAtsTeamWorkflow> getTeamWfs() {
      return teamWfs;
   }

   public Collection<ArtifactId> getTeamWfArts() {
      List<ArtifactId> arts = new LinkedList<>();
      for (IAtsTeamWorkflow team : teamWfs) {
         arts.add(team.getStoreObject());
      }
      return arts;
   }

   public Collection<IAtsTeamWorkflow> getTeams() {
      return this.teamWfs;
   }

   public IAtsTeamWorkflow getFirstTeam() {
      return teamWfs.iterator().next();
   }

   public XResultData getResults() {
      return results;
   }
}
