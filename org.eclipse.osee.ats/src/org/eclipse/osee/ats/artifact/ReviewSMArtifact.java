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
package org.eclipse.osee.ats.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.widgets.XActionableItemsDam;
import org.eclipse.osee.ats.util.widgets.defect.DefectManager;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.ats.util.widgets.role.UserRoleManager;
import org.eclipse.osee.ats.util.widgets.role.UserRole.Role;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public abstract class ReviewSMArtifact extends TaskableStateMachineArtifact {

   protected DefectManager defectManager;
   protected UserRoleManager userRoleManager;
   private XActionableItemsDam actionableItemsDam;
   private Collection<UserRole> preSaveReviewRoleComplete;
   public static enum ReviewBlockType {
      None, Transition, Commit
   };

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws OseeDataStoreException
    */
   public ReviewSMArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   @Override
   public void onInitializationComplete() {
      super.onInitializationComplete();
      initializeSMA();
   };

   @Override
   public void onAttributePersist(SkynetTransaction transaction) throws OseeCoreException {
      super.onAttributePersist(transaction);
      // Since multiple ways exist to change the assignees, notification is performed on the persist
      if (isDeleted()) {
         return;
      }
      try {
         notifyReviewersComplete();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void initalizePreSaveCache() {
      super.initalizePreSaveCache();
      try {
         preSaveReviewRoleComplete = getRoleUsersReviewComplete();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private Collection<UserRole> getRoleUsersReviewComplete() throws OseeCoreException {
      return this.getUserRoleManager().getRoleUsersReviewComplete();
   }

   public void notifyReviewersComplete() throws OseeCoreException {
      UserRoleManager userRoleManager = this.getUserRoleManager();
      if (!preSaveReviewRoleComplete.equals(userRoleManager.getRoleUsersReviewComplete())) {
         //all reviewers are complete; send notification to author/moderator
         if (userRoleManager.getUserRoles(Role.Reviewer).equals(userRoleManager.getRoleUsersReviewComplete())) {
            AtsNotifyUsers.notify(this, AtsNotifyUsers.NotifyType.Reviewed);
         }
      }
      preSaveReviewRoleComplete = userRoleManager.getRoleUsersReviewComplete();
   }

   /**
    * Reset managers for case where artifact is re-loaded/initialized
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#initialize()
    */
   @Override
   protected void initializeSMA() {
      super.initializeSMA();
      defectManager = new DefectManager(this);
      userRoleManager = new UserRoleManager(this);
      actionableItemsDam = new XActionableItemsDam(this);
   }

   @Override
   public String getArtifactSuperTypeName() {
      return "Review";
   }

   public boolean isBlocking() throws OseeCoreException {
      return getReviewBlockType() != ReviewBlockType.None;
   }

   public ReviewBlockType getReviewBlockType() throws OseeCoreException {
      String typeStr = getSoleAttributeValue(ATSAttributes.REVIEW_BLOCKS_ATTRIBUTE.getStoreName(), null);
      if (typeStr == null) {
         // Check old attribute value
         if (getSoleAttributeValue(ATSAttributes.BLOCKING_REVIEW_ATTRIBUTE.getStoreName(), false) == true) {
            return ReviewBlockType.Transition;
         }
         return ReviewBlockType.None;
      }
      return ReviewBlockType.valueOf(typeStr);
   }

   public DefectManager getDefectManager() {
      if (defectManager == null) {
         defectManager = new DefectManager(this);
      }
      return defectManager;
   }

   @Override
   public String getHyperTargetVersion() {
      return null;
   }

   public UserRoleManager getUserRoleManager() {
      if (userRoleManager == null) {
         return userRoleManager = new UserRoleManager(this);
      }
      return userRoleManager;
   }

   public IStatus isUserRoleValid(String namespace) throws OseeCoreException {
      // Need this cause it removes all error items of this namespace
      return new Status(IStatus.OK, namespace, "");
   }

   public Set<TeamDefinitionArtifact> getCorrespondingTeamDefinitionArtifact() throws OseeCoreException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      if (getParentTeamWorkflow() != null) {
         teamDefs.add(getParentTeamWorkflow().getTeamDefinition());
      }
      if (getActionableItemsDam().getActionableItems().size() > 0) {
         teamDefs.addAll(ActionableItemArtifact.getImpactedTeamDefs(getActionableItemsDam().getActionableItems()));
      }
      return teamDefs;
   }

   /**
    * @return the actionableItemsDam
    */
   public XActionableItemsDam getActionableItemsDam() {
      if (actionableItemsDam == null) {
         actionableItemsDam = new XActionableItemsDam(this);
      }
      return actionableItemsDam;
   }

   @Override
   public ActionArtifact getParentActionArtifact() throws OseeCoreException {
      if (getParentSMA() != null) {
         return ((TeamWorkFlowArtifact) getParentSMA()).getParentActionArtifact();
      }
      return null;
   }

   @Override
   public String getWorldViewParentID() throws OseeCoreException {
      return getParentTeamWorkflow().getHumanReadableId();
   }
}
