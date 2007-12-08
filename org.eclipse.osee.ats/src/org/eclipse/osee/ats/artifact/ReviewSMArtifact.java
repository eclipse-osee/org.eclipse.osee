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
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public abstract class ReviewSMArtifact extends StateMachineArtifact {

   public DefectManager defectManager;
   public UserRoleManager userRoleManager;
   private XActionableItemsDam actionableItemsDam;

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public ReviewSMArtifact(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch);
   }

   @Override
   public void onInitializationComplete() {
      super.onInitializationComplete();
      defectManager = new DefectManager(this);
      userRoleManager = new UserRoleManager(this);
      actionableItemsDam = new XActionableItemsDam(this);
   };

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getArtifactSuperTypeName()
    */
   @Override
   public String getArtifactSuperTypeName() {
      return "Review";
   }

   public boolean isBlocking() throws IllegalStateException, SQLException {
      return getSoleBooleanAttributeValue(ATSAttributes.BLOCKING_REVIEW_ATTRIBUTE.getStoreName());
   }

   public DefectManager getDefectManager() {
      return defectManager;
   }

   public UserRoleManager getUserRoleManager() {
      return userRoleManager;
   }

   public Result isUserRoleValid() {
      return Result.TrueResult;
   }

   public Set<TeamDefinitionArtifact> getCorrespondingTeamDefinitionArtifact() throws Exception {
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
}
