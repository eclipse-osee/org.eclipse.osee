/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.navigate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
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
   public EmailTeamsItem(IAtsTeamDefinition teamDef, MemberType... memberType) {
      super("Email " + (teamDef == null ? "Team " : "\"" + teamDef + "\" Team ") + (Arrays.asList(memberType).contains(
         MemberType.Both) ? "Leads / Members" : Arrays.asList(memberType).contains(
            MemberType.Leads) ? "Leads" : "Members"),
         FrameworkImage.EMAIL, XNavigateItem.EMAIL_NOTIFICATIONS);
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
            for (AtsUser user : AtsApiService.get().getTeamDefinitionService().getMembers(teamDef)) {
               if (Strings.isValid(user.getEmail()) && user.isActive()) {
                  emails.add(user.getEmail());
               }
            }
         }
         if (memberTypes.contains(MemberType.Leads) || memberTypes.contains(MemberType.Both)) {
            for (AtsUser user : AtsApiService.get().getTeamDefinitionService().getLeads(teamDef)) {
               if (Strings.isValid(user.getEmail()) && user.isActive()) {
                  emails.add(user.getEmail());
               }
            }
         }
      }
      if (emails.isEmpty()) {
         AWorkbench.popup("Error", "No emails or active users configured.");
         return;
      }
      Program.launch("mailto:" + org.eclipse.osee.framework.jdk.core.util.Collections.toString(";", emails));
      AWorkbench.popup("Complete", "Configured emails openened in local email client.");
   }

   public Collection<IAtsTeamDefinition> getTeamDefinitions() {
      if (teamDef != null) {
         Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
         teamDefs.add(teamDef);
         teamDefs.addAll(AtsApiService.get().getTeamDefinitionService().getChildren(teamDef, true));
         return teamDefs;
      }
      TeamDefinitionTreeWithChildrenDialog ld = new TeamDefinitionTreeWithChildrenDialog(Active.Active);
      int result = ld.open();
      if (result == 0) {
         return AtsApiService.get().getTeamDefinitionService().getTeamDefs(ld.getResultAndRecursedTeamDefs());
      }
      return java.util.Collections.emptyList();
   }
}
