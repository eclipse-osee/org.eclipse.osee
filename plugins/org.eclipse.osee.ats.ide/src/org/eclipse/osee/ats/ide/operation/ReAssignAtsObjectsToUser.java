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

package org.eclipse.osee.ats.ide.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeAndDescriptiveLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeArtifactDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class ReAssignAtsObjectsToUser extends AbstractBlam {

   public final static String FROM_ASSIGNEE = "From Assignee";
   public final static String TO_ASSIGNEE = "To Assignee";

   @Override
   public String getName() {
      return "Re-Assign ATS Objects To User";
   }

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
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
                  Collections.castAll(AtsApiService.get().getQueryService().getAssigned(
                     AtsApiService.get().getUserService().getUserById(fromUser), false));
               Set<Artifact> atsArts = new HashSet<>();
               for (Artifact assignedArt : assignedToArts) {
                  if (assignedArt instanceof AbstractWorkflowArtifact) {
                     atsArts.add(assignedArt);
                  }
               }
               if (atsArts.isEmpty()) {
                  AWorkbench.popup("ERROR", "Not workflows, tasks or reviews assigned to " + fromUser);
                  return;
               }

               // Show in list dialog and allow select for ones to change
               FilteredCheckboxTreeArtifactDialog dialog =
                  new FilteredCheckboxTreeArtifactDialog("ReAssign ATS Object to User",
                     "Select to re-assign to user \"" + toUser, atsArts, new ArtifactTypeAndDescriptiveLabelProvider());
               dialog.setShowSelectButtons(true);
               if (dialog.open() != 0) {
                  return;
               }
               final Collection<Artifact> artsToReAssign = dialog.getChecked();

               IAtsChangeSet changes = AtsApiService.get().createChangeSet("ReAssign ATS Object to User");
               // Make the changes and persist
               for (Artifact artifact : artsToReAssign) {
                  if (artifact instanceof AbstractWorkflowArtifact) {
                     AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
                     awa.getStateMgr().removeAssignee(AtsApiService.get().getUserService().getUserById(fromUser));
                     awa.getStateMgr().addAssignee(AtsApiService.get().getUserService().getUserById(toUser));
                     changes.add(awa);
                  }
               }
               changes.execute();
            } catch (Exception ex) {
               log(ex);
            }
         };
      });
   }

   @Override
   public String getXWidgetsXml() {
      StringBuffer buffer = new StringBuffer("<xWidgets>");
      buffer.append("<XWidget xwidgetType=\"XMembersComboAll\" displayName=\"" + FROM_ASSIGNEE + "\" />");
      buffer.append("<XWidget xwidgetType=\"XMembersCombo\" displayName=\"" + TO_ASSIGNEE + "\" />");
      buffer.append("</xWidgets>");
      return buffer.toString();
   }

   @Override
   public String getDescriptionUsage() {
      return "Re-Assign ATS Workflows, Tasks and Reviews to another user.  Enter to and from User and select play.  You will be promted to select the ATS Objects to reassign.";
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsImage.ACTION);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.ACTION);
   }

}
