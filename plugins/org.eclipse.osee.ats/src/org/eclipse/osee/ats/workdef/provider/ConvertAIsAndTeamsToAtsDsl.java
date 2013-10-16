/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workdef.provider;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.dsl.atsDsl.ActionableItemDef;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.eclipse.osee.ats.dsl.atsDsl.TeamDef;
import org.eclipse.osee.ats.dsl.atsDsl.UserByName;
import org.eclipse.osee.ats.dsl.atsDsl.VersionDef;
import org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslFactoryImpl;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Take existing AIs, TeamDefs and Versions and create AtsDsl
 * 
 * @author Donald G. Dunne
 */
public class ConvertAIsAndTeamsToAtsDsl {

   private final XResultData resultData;
   private AtsDsl atsDsl;
   private final Map<String, TeamDef> dslTeamDefs = new HashMap<String, TeamDef>();
   private final Map<String, ActionableItemDef> dslAIDefs = new HashMap<String, ActionableItemDef>();

   public ConvertAIsAndTeamsToAtsDsl(XResultData resultData) {
      this.resultData = resultData;
   }

   public AtsDsl convert(String definitionName) {
      resultData.log("Converting AIs and Teams to ATS DSL");
      atsDsl = AtsDslFactoryImpl.init().createAtsDsl();

      try {
         // Add all TeamDef definitions
         TeamDef topTeam = convertTeamDef(TeamDefinitions.getTopTeamDefinition(), null);
         atsDsl.getTeamDef().add(topTeam);

         // Add all AI definitions
         ActionableItemDef topAi = convertAIDef(ActionableItems.getTopActionableItem(), null);
         atsDsl.getActionableItemDef().add(topAi);

      } catch (OseeCoreException ex) {
         resultData.logError("Exception: " + ex.getLocalizedMessage());
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return atsDsl;
   }

   private ActionableItemDef convertAIDef(IAtsActionableItem aiArt, ActionableItemDef dslParentAIDef) throws OseeCoreException {
      ActionableItemDef dslAIDef = AtsDslFactoryImpl.init().createActionableItemDef();
      if (dslParentAIDef != null) {
         dslParentAIDef.getChildren().add(dslAIDef);
      }
      dslAIDef.setName(aiArt.getName());
      dslAIDefs.put(aiArt.getName(), dslAIDef);
      if (aiArt.isActive()) {
         dslAIDef.setActive(BooleanDef.TRUE);
      }
      if (aiArt.isActionable()) {
         dslAIDef.setActionable(BooleanDef.TRUE);
      }
      for (String staticId : aiArt.getStaticIds()) {
         dslAIDef.getStaticId().add(staticId);
      }
      for (IAtsUser user : aiArt.getLeads()) {
         dslAIDef.getLead().add(getUserByName(user));
      }
      IAtsTeamDefinition teamDef = aiArt.getTeamDefinition();
      if (teamDef != null) {
         dslAIDef.setTeamDef(teamDef.getName());
      }
      // process children
      for (IAtsActionableItem childAiArt : aiArt.getChildrenActionableItems()) {
         convertAIDef(childAiArt, dslAIDef);
      }
      return dslAIDef;
   }

   private TeamDef convertTeamDef(IAtsTeamDefinition teamDef, TeamDef dslParentTeamDef) throws OseeCoreException {
      TeamDef dslTeamDef = AtsDslFactoryImpl.init().createTeamDef();
      if (dslParentTeamDef != null) {
         dslParentTeamDef.getChildren().add(dslTeamDef);
      }

      dslTeamDef.setName(teamDef.getName());
      dslTeamDefs.put(teamDef.getName(), dslTeamDef);
      if (teamDef.isActive()) {
         dslTeamDef.setActive(BooleanDef.TRUE);
      }
      for (String staticId : teamDef.getStaticIds()) {
         dslTeamDef.getStaticId().add(staticId);
      }
      for (IAtsUser user : teamDef.getLeads()) {
         dslTeamDef.getLead().add(getUserByName(user));
      }
      for (IAtsUser user : teamDef.getMembers()) {
         dslTeamDef.getMember().add(getUserByName(user));
      }
      for (IAtsUser user : teamDef.getPrivilegedMembers()) {
         dslTeamDef.getPrivileged().add(getUserByName(user));
      }
      for (IAtsVersion verArt : teamDef.getVersions()) {
         convertVersionArtifact(dslTeamDef, verArt, teamDef);
      }
      // process children
      for (IAtsTeamDefinition childAiArt : teamDef.getChildrenTeamDefinitions()) {
         convertTeamDef(childAiArt, dslTeamDef);
      }
      return dslTeamDef;
   }

   private void convertVersionArtifact(TeamDef dslTeamDef, IAtsVersion verArt, IAtsTeamDefinition teamDef) {
      VersionDef dslVerDef = AtsDslFactoryImpl.init().createVersionDef();
      dslVerDef.setName(verArt.getName());
      if (verArt.isNextVersion()) {
         dslVerDef.setNext(BooleanDef.TRUE);
      }
      for (String staticId : teamDef.getStaticIds()) {
         dslVerDef.getStaticId().add(staticId);
      }
      if (verArt.isReleased()) {
         dslVerDef.setReleased(BooleanDef.TRUE);
      }
      if (verArt.isAllowCommitBranchInherited().isTrue()) {
         dslVerDef.setAllowCommitBranch(BooleanDef.TRUE);
      }
      if (verArt.isAllowCreateBranchInherited().isTrue()) {
         dslVerDef.setAllowCreateBranch(BooleanDef.TRUE);
      }
      if (verArt.getBaselineBranchGuidInherited() != null) {
         dslVerDef.setBaselineBranchGuid(verArt.getBaselineBranchGuidInherited());
      }
      dslTeamDef.getVersion().add(dslVerDef);
   }

   private UserByName getUserByName(IAtsUser user) {
      UserByName userByName = AtsDslFactoryImpl.init().createUserByName();
      userByName.setUserName(user.getName());
      return userByName;
   }
}
