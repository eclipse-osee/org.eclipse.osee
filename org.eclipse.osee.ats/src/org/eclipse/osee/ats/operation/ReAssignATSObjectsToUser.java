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
package org.eclipse.osee.ats.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeAndDescriptiveLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactCheckTreeDialog;

/**
 * @author Donald G. Dunne
 */
public class ReAssignATSObjectsToUser extends AbstractBlam {

   public static String FROM_ASSIGNEE = "From Assignee";
   public static String TO_ASSIGNEE = "To Assignee";

   @Override
   public String getName() {
      return "Re-Assign ATS Objects To User";
   }

   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               final User fromUser = variableMap.getUser(FROM_ASSIGNEE);
               if (fromUser == null) {
                  AWorkbench.popup("ERROR", "Please select From Assignee");
                  return;
               }

               final User toUser = variableMap.getUser(TO_ASSIGNEE);
               if (toUser == null) {
                  AWorkbench.popup("ERROR", "Please select To Assignee");
                  return;
               }

               // Get all things user is directly assigned to
               Collection<Artifact> assignedToArts =
                     fromUser.getRelatedArtifacts(CoreRelationTypes.Users_Artifact, Artifact.class);
               Set<Artifact> atsArts = new HashSet<Artifact>();
               for (Artifact assignedArt : assignedToArts) {
                  if (assignedArt instanceof StateMachineArtifact) {
                     atsArts.add(assignedArt);
                  }
               }
               if (atsArts.size() == 0) {
                  AWorkbench.popup("ERROR", "Not workflows, tasks or reviews assigned to " + fromUser);
                  return;
               }

               // Show in list dialog and allow select for ones to change
               ArtifactCheckTreeDialog dialog =
                     new ArtifactCheckTreeDialog(atsArts, new ArtifactTypeAndDescriptiveLabelProvider());
               dialog.setTitle("ReAssign ATS Object to User");
               dialog.setMessage("Select to re-assign to user \"" + toUser);
               if (dialog.open() != 0) return;
               final Collection<Artifact> artsToReAssign = dialog.getSelection();

               // Make the changes and persist
               SkynetTransaction transaction =
                     new SkynetTransaction(AtsUtil.getAtsBranch(), "Re-Assign ATS Objects to User");
               for (Artifact artifact : artsToReAssign) {
                  if (artifact instanceof StateMachineArtifact) {
                     ((StateMachineArtifact) artifact).getSmaMgr().getStateMgr().removeAssignee(fromUser);
                     ((StateMachineArtifact) artifact).getSmaMgr().getStateMgr().addAssignee(toUser);
                  }
                  artifact.persist(transaction);
               }
               transaction.execute();
               OseeNotificationManager.sendNotifications();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         };
      });
   }

   @Override
   public String getXWidgetsXml() {
      StringBuffer buffer = new StringBuffer("<xWidgets>");
      buffer.append("<XWidget xwidgetType=\"XMembersCombo\" displayName=\"" + FROM_ASSIGNEE + "\" />");
      buffer.append("<XWidget xwidgetType=\"XMembersCombo\" displayName=\"" + TO_ASSIGNEE + "\" />");
      buffer.append("</xWidgets>");
      return buffer.toString();
   }

   @Override
   public String getDescriptionUsage() {
      return "Re-Assign ATS Workflows, Tasks and Reviews to another user.  Enter to and from User and select play.  You will be promted to select the ATS Objects to reassign.";
   }

   public Collection<String> getCategories() {
      return Arrays.asList("ATS.Admin");
   }
}