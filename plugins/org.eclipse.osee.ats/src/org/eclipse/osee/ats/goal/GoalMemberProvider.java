/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.goal;

import java.util.List;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.ats.AtsArtifactImageProvider;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.agile.BacklogXViewerFactory;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.artifact.GoalManager;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class GoalMemberProvider extends AbstractMemberProvider {

   private IAtsGoal goal;

   public GoalMemberProvider(IAtsGoal goal) {
      this.goal = goal;
   }

   @Override
   public String getCollectorName() {
      if (isBacklog()) {
         return "Backlog";
      }
      return "Goal";
   }

   @Override
   public String getMembersName() {
      if (isBacklog()) {
         return "Items";
      }
      return "Members";
   }

   @Override
   public KeyedImage getImageKey() {
      if (isBacklog()) {
         return AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_BACKLOG);
      }
      return AtsImage.GOAL;
   }

   @Override
   public List<Artifact> getMembers() {
      return getArtifact().getMembers();
   }

   @Override
   public GoalArtifact getArtifact() {
      return (GoalArtifact) goal.getStoreObject();
   }

   @Override
   public Long getId() {
      return getArtifact().getId();
   }

   @Override
   public void addMember(Artifact artifact) {
      getArtifact().addMember(artifact);
   }

   @Override
   public IXViewerFactory getXViewerFactory(Artifact awa) {
      if (isBacklog()) {
         return new BacklogXViewerFactory((GoalArtifact) awa, this);
      }
      return new GoalXViewerFactory((GoalArtifact) awa, this);
   }

   @Override
   public String getColumnName() {
      if (isBacklog()) {
         return "ats.column.backlogOrder";
      }
      return "ats.column.goalOrder";
   }

   @Override
   public RelationTypeSide getMemberRelationTypeSide() {
      return AtsRelationTypes.Goal_Member;
   }

   @Override
   public void promptChangeOrder(Artifact goalArt, List<Artifact> selectedAtsArtifacts) {
      new GoalManager().promptChangeMemberOrder((GoalArtifact) goalArt, selectedAtsArtifacts);
   }

   @Override
   public Result isAddValid(List<Artifact> artifacts) {
      return Result.TrueResult;
   }

   @Override
   public boolean isBacklog() {
      return AtsClientService.get().getAgileService().isBacklog(getArtifact());
   }

   @Override
   public boolean isSprint() {
      return false;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      goal = (GoalArtifact) artifact;
   }

}
