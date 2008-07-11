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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeAndDescriptiveLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactCheckTreeDialog;

/**
 * @author Donald G. Dunne
 */
public class ReAssignATSObjectsToUser extends AbstractBlam {

   public static String FROM_ASSIGNEE = "From Assignee";
   public static String TO_ASSIGNEE = "To Assignee";

   public ReAssignATSObjectsToUser() throws IOException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(final BlamVariableMap variableMap, IProgressMonitor monitor) throws OseeCoreException, SQLException {
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
                     fromUser.getArtifacts(CoreRelationEnumeration.Users_Artifact, Artifact.class);
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
               AbstractSkynetTxTemplate txWrapper =
                     new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
                        @Override
                        protected void handleTxWork() throws OseeCoreException, SQLException {
                           for (Artifact artifact : artsToReAssign) {
                              if (artifact instanceof StateMachineArtifact) {
                                 ((StateMachineArtifact) artifact).getSmaMgr().getStateMgr().removeAssignee(fromUser);
                                 ((StateMachineArtifact) artifact).getSmaMgr().getStateMgr().addAssignee(toUser);
                              }
                              artifact.persistAttributesAndRelations();
                           }
                        }
                     };
               txWrapper.execute();
               OseeNotificationManager.sendNotifications();
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         };
      });
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      StringBuffer buffer = new StringBuffer("<xWidgets>");
      buffer.append("<XWidget xwidgetType=\"XMembersCombo\" displayName=\"" + FROM_ASSIGNEE + "\" />");
      buffer.append("<XWidget xwidgetType=\"XMembersCombo\" displayName=\"" + TO_ASSIGNEE + "\" />");
      buffer.append("</xWidgets>");
      return buffer.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getDescriptionUsage()
    */
   @Override
   public String getDescriptionUsage() {
      return "Re-Assign ATS Workflows, Tasks and Reviews to another user.  Enter to and from User and select play.  You will be promted to select the ATS Objects to reassign.";
   }

}