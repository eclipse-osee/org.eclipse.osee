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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.UserGroupsCheckTreeDialog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class EmailGroupsAndUserGroups extends XNavigateItemAction {

   private List<User> users = new ArrayList<User>();
   private final List<GroupType> groupTypes;
   public static enum GroupType {
      Groups, UserGroups, Both
   };

   /**
    * @param parent
    * @param teamDefHoldingVersions Team Definition Artifact that is related to versions or null for popup selection
    */
   public EmailGroupsAndUserGroups(XNavigateItem parent, GroupType... memberType) {
      super(parent,
            "Email " + (Arrays.asList(memberType).contains(GroupType.Both) ? "Groups / User Groups" : (Arrays.asList(
                  memberType).contains(GroupType.Groups) ? "Groups" : "User Groups")));
      groupTypes = Arrays.asList(memberType);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run() throws SQLException {
      Set<Artifact> groupOptions = new HashSet<Artifact>();
      if (groupTypes.contains(GroupType.Both) || groupTypes.contains(GroupType.Groups)) {
         for (Artifact art : UniversalGroup.getGroups(AtsPlugin.getAtsBranch())) {
            // Only add group if have read permissions
            if (AccessControlManager.getInstance().checkObjectPermission(art, PermissionEnum.READ)) groupOptions.add(art);
         }
      }
      if (groupTypes.contains(GroupType.Both) || groupTypes.contains(GroupType.UserGroups)) {
         for (Artifact art : ArtifactPersistenceManager.getInstance().getArtifactsFromSubtypeName("User Group",
               AtsPlugin.getAtsBranch())) {
            // Only add group if have read permissions
            if (AccessControlManager.getInstance().checkObjectPermission(art, PermissionEnum.READ)) groupOptions.add(art);
         }
      }
      UserGroupsCheckTreeDialog dialog = new UserGroupsCheckTreeDialog(groupOptions);
      dialog.setTitle("Select Groups to Email");
      if (dialog.open() == 0) {

         Set<String> emails = new HashSet<String>();
         for (Artifact artifact : dialog.getSelection()) {
            if (artifact.getArtifactTypeName().equals("Universal Group")) {
               for (Artifact userArt : artifact.getArtifacts(RelationSide.UNIVERSAL_GROUPING__MEMBERS)) {
                  if (userArt instanceof User) {
                     emails.add(((User) userArt).getEmail());
                  }
               }
            } else if (artifact.getArtifactTypeName().equals("User Group")) {
               for (User user : artifact.getArtifacts(RelationSide.Users_User, User.class)) {
                  emails.add(user.getEmail());
               }
            }
         }
         if (emails.size() == 0) {
            AWorkbench.popup("Error", "No emails configured.");
            return;
         }
         Program.launch("mailto:" + org.eclipse.osee.framework.jdk.core.util.Collections.toString(";", emails));
         AWorkbench.popup("Complete", "Configured emails openened in local email client.");
      }
   }

}
