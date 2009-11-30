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

package org.eclipse.osee.ats.navigate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionTreeWithChildrenDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class EmailTeamsItem extends XNavigateItemAction {

   private final TeamDefinitionArtifact teamDef;
   private final Collection<MemberType> memberTypes;
   public static enum MemberType {
      Leads, Members, Both
   };

   /**
    * @param parent
    * @param teamDefHoldingVersions Team Definition Artifact that is related to versions or null for popup selection
    */
   public EmailTeamsItem(XNavigateItem parent, TeamDefinitionArtifact teamDef, MemberType... memberType) {
      super(
            parent,
            "Email " + (teamDef == null ? "Team " : "\"" + teamDef + "\" Team ") + (Arrays.asList(memberType).contains(
                  MemberType.Both) ? "Leads / Members" : (Arrays.asList(memberType).contains(MemberType.Leads) ? "Leads" : "Members")),
            FrameworkImage.EMAIL);
      memberTypes = Arrays.asList(memberType);
      this.teamDef = teamDef;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      Collection<TeamDefinitionArtifact> teamDefs = getTeamDefinitions();
      if (teamDefs.size() == 0) return;
      Set<String> emails = new HashSet<String>();
      for (TeamDefinitionArtifact teamDef : teamDefs) {
         if (memberTypes.contains(MemberType.Members) || memberTypes.contains(MemberType.Both)) {
            for (User user : teamDef.getRelatedArtifacts(AtsRelationTypes.TeamMember_Member, User.class))
               if (!user.getEmail().equals("")) emails.add(user.getEmail());
         }
         if (memberTypes.contains(MemberType.Leads) || memberTypes.contains(MemberType.Both)) {
            for (User user : teamDef.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead, User.class))
               if (!user.getEmail().equals("")) emails.add(user.getEmail());
         }
      }
      if (emails.size() == 0) {
         AWorkbench.popup("Error", "No emails configured.");
         return;
      }
      Program.launch("mailto:" + org.eclipse.osee.framework.jdk.core.util.Collections.toString(";", emails));
      AWorkbench.popup("Complete", "Configured emails openened in local email client.");
   }

   public Collection<TeamDefinitionArtifact> getTeamDefinitions() throws OseeCoreException {
      if (teamDef != null) {
         Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
         teamDefs.add(teamDef);
         teamDefs.addAll(Artifacts.getChildrenOfTypeSet(teamDef, TeamDefinitionArtifact.class, true));
         return teamDefs;
      }
      TeamDefinitionTreeWithChildrenDialog ld = new TeamDefinitionTreeWithChildrenDialog(Active.Active);
      int result = ld.open();
      if (result == 0) {
         return ld.getResultAndRecursedTeamDefs();
      }
      return java.util.Collections.emptyList();
   }
}
