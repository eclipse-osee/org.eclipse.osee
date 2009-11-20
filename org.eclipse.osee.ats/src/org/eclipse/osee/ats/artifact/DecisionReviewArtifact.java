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
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.XDecisionOptions;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class DecisionReviewArtifact extends ReviewSMArtifact implements IReviewArtifact, IWorldViewArtifact, IATSStateMachineArtifact {

   public static String ARTIFACT_NAME = "Decision Review";
   public XDecisionOptions decisionOptions;
   public static enum DecisionReviewState {
      Prepare, Decision, Followup, Completed
   };

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws OseeDataStoreException
    */
   public DecisionReviewArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      decisionOptions = new XDecisionOptions(this);
   }

   @Override
   public String getHelpContext() {
      return "decisionReview";
   }

   @Override
   public Set<User> getPrivilegedUsers() throws OseeCoreException {
      Set<User> users = new HashSet<User>();
      if (getParentTeamWorkflow() != null)
         users.addAll(getParentTeamWorkflow().getPrivilegedUsers());
      else {
         if (AtsUtil.isAtsAdmin()) {
            users.add(UserManager.getUser());
         }
      }
      return users;
   }

   @Override
   public String getHyperName() {
      return getName();
   }

   @Override
   public String getWorldViewDecision() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.DECISION_ATTRIBUTE.getStoreName(), "");
   }

   @Override
   public String getWorldViewDescription() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
   }

   @Override
   public VersionArtifact getWorldViewTargetedVersion() throws OseeCoreException {
      if (getParentSMA() == null) return null;
      return getParentSMA().getWorldViewTargetedVersion();
   }

   @Override
   public Collection<User> getImplementers() throws OseeCoreException {
      return getImplementersByState(DecisionReviewState.Decision.name());
   }

   @Override
   public String getWorldViewReviewDecider() throws OseeCoreException {
      return Artifacts.toString("; ", smaMgr.getStateMgr().getAssignees(DecisionReviewState.Decision.name()));
   }

}