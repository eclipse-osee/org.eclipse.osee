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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionTreeWithChildrenDialog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
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
                  MemberType.Both) ? "Leads / Members" : (Arrays.asList(memberType).contains(MemberType.Leads) ? "Leads" : "Members")));
      memberTypes = Arrays.asList(memberType);
      this.teamDef = teamDef;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException, SQLException {
      Collection<TeamDefinitionArtifact> teamDefs = getTeamDefinitions();
      if (teamDefs == null || teamDefs.size() == 0) return;
      Set<String> emails = new HashSet<String>();
      for (TeamDefinitionArtifact teamDef : teamDefs) {
         if (memberTypes.contains(MemberType.Members) || memberTypes.contains(MemberType.Both)) {
            for (User user : teamDef.getArtifacts(AtsRelation.TeamMember_Member, User.class))
               if (!user.getEmail().equals("")) emails.add(user.getEmail());
         }
         if (memberTypes.contains(MemberType.Leads) || memberTypes.contains(MemberType.Both)) {
            for (User user : teamDef.getArtifacts(AtsRelation.TeamLead_Lead, User.class))
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

   public Collection<TeamDefinitionArtifact> getTeamDefinitions() throws OseeCoreException, SQLException {
      if (teamDef != null) {
         return Artifacts.getChildrenOfTypeSet(teamDef, TeamDefinitionArtifact.class, true);
      }
      TeamDefinitionTreeWithChildrenDialog ld = new TeamDefinitionTreeWithChildrenDialog(Active.Active);
      int result = ld.open();
      if (result == 0) {
         Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
         for (Object obj : ld.getResult()) {
            teamDefs.add((TeamDefinitionArtifact) obj);
            teamDefs.addAll(Artifacts.getChildrenOfTypeSet((TeamDefinitionArtifact) obj, TeamDefinitionArtifact.class,
                  true));
         }
         return teamDefs;
      }
      return null;
   }
}
