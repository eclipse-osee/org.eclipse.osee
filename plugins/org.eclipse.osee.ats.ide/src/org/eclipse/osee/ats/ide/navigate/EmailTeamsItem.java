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

package org.eclipse.osee.ats.ide.navigate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TeamDefinitionTreeWithChildrenDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class EmailTeamsItem extends XNavigateItemAction {

   private final IAtsTeamDefinition teamDef;
   private final Collection<MemberType> memberTypes;
   public static enum MemberType {
      Leads,
      Members,
      Both
   };

   /**
    * @param teamDefHoldingVersions Team Definition Artifact that is related to versions or null for popup selection
    */
   public EmailTeamsItem(XNavigateItem parent, IAtsTeamDefinition teamDef, MemberType... memberType) {
      super(parent,
         "Email " + (teamDef == null ? "Team " : "\"" + teamDef + "\" Team ") + (Arrays.asList(memberType).contains(
            MemberType.Both) ? "Leads / Members" : Arrays.asList(memberType).contains(
               MemberType.Leads) ? "Leads" : "Members"),
         FrameworkImage.EMAIL);
      memberTypes = Arrays.asList(memberType);
      this.teamDef = teamDef;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      Collection<IAtsTeamDefinition> teamDefs = getTeamDefinitions();
      if (teamDefs.isEmpty()) {
         return;
      }
      Set<String> emails = new HashSet<>();
      for (IAtsTeamDefinition teamDef : teamDefs) {
         if (memberTypes.contains(MemberType.Members) || memberTypes.contains(MemberType.Both)) {
            for (IAtsUser user : teamDef.getMembers()) {
               if (Strings.isValid(user.getEmail())) {
                  emails.add(user.getEmail());
               }
            }
         }
         if (memberTypes.contains(MemberType.Leads) || memberTypes.contains(MemberType.Both)) {
            for (IAtsUser user : teamDef.getLeads()) {
               if (Strings.isValid(user.getEmail())) {
                  emails.add(user.getEmail());
               }
            }
         }
      }
      if (emails.isEmpty()) {
         AWorkbench.popup("Error", "No emails configured.");
         return;
      }
      Program.launch("mailto:" + org.eclipse.osee.framework.jdk.core.util.Collections.toString(";", emails));
      AWorkbench.popup("Complete", "Configured emails openened in local email client.");
   }

   public Collection<IAtsTeamDefinition> getTeamDefinitions() {
      if (teamDef != null) {
         Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
         teamDefs.add(teamDef);
         teamDefs.addAll(TeamDefinitions.getChildren(teamDef, true));
         return teamDefs;
      }
      TeamDefinitionTreeWithChildrenDialog ld = new TeamDefinitionTreeWithChildrenDialog(Active.Active);
      int result = ld.open();
      if (result == 0) {
         return TeamDefinitions.getTeamDefs(ld.getResultAndRecursedTeamDefs(), AtsClientService.get());
      }
      return java.util.Collections.emptyList();
   }
}
