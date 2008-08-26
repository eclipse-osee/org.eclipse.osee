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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.util.widgets.XActionableItemsDam;
import org.eclipse.osee.ats.util.widgets.defect.DefectManager;
import org.eclipse.osee.ats.util.widgets.role.UserRoleManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public abstract class ReviewSMArtifact extends TaskableStateMachineArtifact {

   protected DefectManager defectManager;
   protected UserRoleManager userRoleManager;
   private XActionableItemsDam actionableItemsDam;

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public ReviewSMArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   @Override
   public void onInitializationComplete() {
      super.onInitializationComplete();
      initializeSMA();
   };

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#initialize()
    */
   @Override
   protected void initializeSMA() {
      super.initializeSMA();
      defectManager = new DefectManager(this);
      userRoleManager = new UserRoleManager(this);
      actionableItemsDam = new XActionableItemsDam(this);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getArtifactSuperTypeName()
    */
   @Override
   public String getArtifactSuperTypeName() {
      return "Review";
   }

   public boolean isBlocking() throws OseeCoreException, SQLException {
      return getSoleAttributeValue(ATSAttributes.BLOCKING_REVIEW_ATTRIBUTE.getStoreName(), false);
   }

   public DefectManager getDefectManager() {
      return defectManager;
   }

   public UserRoleManager getUserRoleManager() {
      return userRoleManager;
   }

   public Result isUserRoleValid() throws OseeCoreException, SQLException {
      return Result.TrueResult;
   }

   public Set<TeamDefinitionArtifact> getCorrespondingTeamDefinitionArtifact() throws OseeCoreException, SQLException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      if (getParentTeamWorkflow() != null) teamDefs.add(getParentTeamWorkflow().getTeamDefinition());
      if (actionableItemsDam.getActionableItems().size() > 0) {
         teamDefs.addAll(ActionableItemArtifact.getImpactedTeamDefs(actionableItemsDam.getActionableItems()));
      }
      return teamDefs;
   }

   /**
    * @return the actionableItemsDam
    */
   public XActionableItemsDam getActionableItemsDam() {
      return actionableItemsDam;
   }

   @Override
   public ActionArtifact getParentActionArtifact() throws SQLException {
      if (getParentSMA() != null) {
         return ((TeamWorkFlowArtifact) getParentSMA()).getParentActionArtifact();
      }
      return null;
   }

}
