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
package org.eclipse.osee.ats.api.workflow.clone;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class CloneData {

   String title = "";
   String desc = "";
   boolean createNewAction = true;
   AtsUser originator;
   Collection<AtsUser> assignees = new HashSet<>();
   String changeType = ChangeTypes.Improvement.name();
   String priority = "";
   String points = "";
   IAtsVersion targetedVersion;
   Collection<String> features = new HashSet<>();
   IAgileSprint sprint;
   XResultData results = new XResultData();
   private ArtifactId newTeamWf;

   public CloneData() {
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDesc() {
      return desc;
   }

   public void setDesc(String desc) {
      this.desc = desc;
   }

   public boolean isCreateNewAction() {
      return createNewAction;
   }

   public void setCreateNewAction(boolean createNewAction) {
      this.createNewAction = createNewAction;
   }

   public AtsUser getOriginator() {
      return originator;
   }

   public void setOriginator(AtsUser originator) {
      this.originator = originator;
   }

   public Collection<AtsUser> getAssignees() {
      return assignees;
   }

   public void setAssignees(Collection<AtsUser> assignees) {
      this.assignees = assignees;
   }

   public String getPriority() {
      return priority;
   }

   public void setPriority(String priority) {
      this.priority = priority;
   }

   public String getPoints() {
      return points;
   }

   public void setPoints(String points) {
      this.points = points;
   }

   public IAtsVersion getTargetedVersion() {
      return targetedVersion;
   }

   public void setTargetedVersion(IAtsVersion targetedVersion) {
      this.targetedVersion = targetedVersion;
   }

   public String getChangeType() {
      return changeType;
   }

   public void setChangeType(String changeType) {
      this.changeType = changeType;
   }

   public Collection<String> getFeatures() {
      return features;
   }

   public void setFeatures(Collection<String> features) {
      this.features = features;
   }

   public IAgileSprint getSprint() {
      return sprint;
   }

   public void setSprint(IAgileSprint sprint) {
      this.sprint = sprint;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public ArtifactId getNewTeamWf() {
      return newTeamWf;
   }

   public void setNewTeamWf(ArtifactId newTeamWf) {
      this.newTeamWf = newTeamWf;
   }

}
