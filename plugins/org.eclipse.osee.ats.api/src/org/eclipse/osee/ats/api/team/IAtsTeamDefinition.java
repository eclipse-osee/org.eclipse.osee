/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.team;

import java.util.Collection;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.rule.IAtsRules;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTeamDefinition extends IAtsConfigObject, IAtsRules, ICommitConfigItem {

   Boolean isActionable();

   Collection<IAtsActionableItem> getActionableItems();

   IAtsTeamDefinition getParentTeamDef();

   Collection<IAtsTeamDefinition> getChildrenTeamDefinitions();

   /*****************************************************
    * Team Leads, Members, Priviledged Members
    ******************************************************/

   Collection<IAtsUser> getLeads();

   Collection<IAtsUser> getLeads(Collection<IAtsActionableItem> actionableItems);

   Collection<IAtsUser> getMembers();

   Collection<IAtsUser> getMembersAndLeads();

   Collection<IAtsUser> getSubscribed();

   /*****************************
    * Branching Data
    ******************************/

   boolean isAllowCommitBranch();

   @Override
   Result isAllowCommitBranchInherited();

   boolean isAllowCreateBranch();

   @Override
   Result isAllowCreateBranchInherited();

   @Override
   BranchId getBaselineBranchId();

   BranchId getTeamBranchId();

   /*****************************
    * Versions
    ******************************/

   boolean isTeamUsesVersions();

   IAtsVersion getNextReleaseVersion();

   IAtsTeamDefinition getTeamDefinitionHoldingVersions();

   IAtsVersion getVersion(String name);

   Set<IAtsVersion> getVersions();

   Collection<IAtsVersion> getVersions(VersionReleaseType releaseType, VersionLockedType lockedType);

   Collection<IAtsVersion> getVersionsFromTeamDefHoldingVersions(VersionReleaseType releaseType, VersionLockedType lockedType);

   Collection<IAtsVersion> getVersionsLocked(VersionLockedType lockType);

   Collection<IAtsVersion> getVersionsReleased(VersionReleaseType releaseType);

   IAtsTeamDefinition SENTINEL = createSentinel();

   public static IAtsTeamDefinition createSentinel() {
      final class IAtsTeamDefinitionSentinel extends NamedIdBase implements IAtsTeamDefinition {

         @Override
         public boolean isActive() {
            return false;
         }

         @Override
         public ArtifactTypeId getArtifactType() {
            return null;
         }

         @Override
         public Collection<String> getRules() {
            return null;
         }

         @Override
         public boolean hasRule(String rule) {
            return false;
         }

         @Override
         public String getCommitFullDisplayName() {
            return null;
         }

         @Override
         public String getTypeName() {
            return null;
         }

         @Override
         public Boolean isActionable() {
            return false;
         }

         @Override
         public Collection<IAtsActionableItem> getActionableItems() {
            return null;
         }

         @Override
         public IAtsTeamDefinition getParentTeamDef() {
            return null;
         }

         @Override
         public Collection<IAtsTeamDefinition> getChildrenTeamDefinitions() {
            return null;
         }

         @Override
         public Collection<IAtsUser> getLeads() {
            return null;
         }

         @Override
         public Collection<IAtsUser> getLeads(Collection<IAtsActionableItem> actionableItems) {
            return null;
         }

         @Override
         public Collection<IAtsUser> getMembers() {
            return null;
         }

         @Override
         public Collection<IAtsUser> getMembersAndLeads() {
            return null;
         }

         @Override
         public Collection<IAtsUser> getSubscribed() {
            return null;
         }

         @Override
         public boolean isAllowCommitBranch() {
            return false;
         }

         @Override
         public Result isAllowCommitBranchInherited() {
            return null;
         }

         @Override
         public boolean isAllowCreateBranch() {
            return false;
         }

         @Override
         public Result isAllowCreateBranchInherited() {
            return null;
         }

         @Override
         public BranchId getBaselineBranchId() {
            return null;
         }

         @Override
         public BranchId getTeamBranchId() {
            return null;
         }

         @Override
         public boolean isTeamUsesVersions() {
            return false;
         }

         @Override
         public IAtsVersion getNextReleaseVersion() {
            return null;
         }

         @Override
         public IAtsTeamDefinition getTeamDefinitionHoldingVersions() {
            return null;
         }

         @Override
         public IAtsVersion getVersion(String name) {
            return null;
         }

         @Override
         public Set<IAtsVersion> getVersions() {
            return null;
         }

         @Override
         public Collection<IAtsVersion> getVersions(VersionReleaseType releaseType, VersionLockedType lockedType) {
            return null;
         }

         @Override
         public Collection<IAtsVersion> getVersionsFromTeamDefHoldingVersions(VersionReleaseType releaseType, VersionLockedType lockedType) {
            return null;
         }

         @Override
         public Collection<IAtsVersion> getVersionsLocked(VersionLockedType lockType) {
            return null;
         }

         @Override
         public Collection<IAtsVersion> getVersionsReleased(VersionReleaseType releaseType) {
            return null;
         }

      }
      return new IAtsTeamDefinitionSentinel();
   }

}
