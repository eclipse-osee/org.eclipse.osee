/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog.UserCheckTreeLabelProvider;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXUserTokenWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XXUserTokenWithTeamComparator;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXAssigneesWidget extends XXUserTokenWidget {

   public static final WidgetId ID = WidgetIdAts.XXAssigneesWidget;

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAssigneesWidget() {
      this(ID, "Assignee(s)");
   }

   public XXAssigneesWidget(String label, Collection<UserToken> assignees) {
      this(ID, label);
      if (!assignees.isEmpty()) {
         setSelectable(assignees);
      }
   }

   /**
    * Do not seed {@link SystemUser#UnAssigned} here. This widget is also used without an artifact (searches, dialogs,
    * and other selectors), where a new widget must have a truly empty selection and blank display. UnAssigned remains
    * available in selection dialogs as a user-facing clear option, but selection normalization below converts it to
    * an empty collection; workflow persistence maps that empty collection to the stored unassigned user.
    */
   public XXAssigneesWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      setAttributeType(AtsAttributeTypes.CurrentStateAssignee);
      remove(XOption.VAL_SINGLE_SEL);
      set(XOption.VAL_MULTI_SEL, XOption.ACTIVE, XOption.CLEAR);
      setOseeImage(CoreImage.USER);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   /**
    * Override if default ViewerComparator is not sufficient for selection dialogs
    */
   @Override
   public UserToken getSentinel() {
      return UserToken.SENTINEL;
   }

   /** Converts the UnAssigned clear option to the widget's canonical empty selection. */
   @Override
   public void setSelected(UserToken selected, boolean refresh) {
      if (SystemUser.UnAssigned.equals(selected)) {
         super.clear(refresh);
      } else {
         super.setSelected(selected, refresh);
      }
   }

   /**
    * Removes UnAssigned from selected values. It is a clear option, not widget data, and must never coexist with or
    * replace real selected users.
    */
   @Override
   public void setSelected(Collection<UserToken> selected, boolean refresh) {
      List<UserToken> normalized = new ArrayList<>(selected);
      normalized.removeIf(SystemUser.UnAssigned::equals);
      super.setSelected(normalized, refresh);
   }

   @Override
   protected TransactionToken handleSelectedPersist() {
      AtsApi atsApi = AtsApiService.get();
      if (xxWid.isWorkItem()) {
         IAtsChangeSet changes = atsApi.createChangeSet("Set Assignee");
         Set<AtsUser> assignees = new HashSet<>();
         if (isNotSelected()) {
            assignees.add(AtsCoreUsers.UNASSIGNED_USER);
         } else {
            for (UserToken userTok : getSelected()) {
               AtsUser aUser = atsApi.getUserService().getUserById(userTok);
               if (aUser != null) {
                  assignees.add(aUser);
               }
            }
            if (assignees.size() > 1) {
               assignees.remove(AtsCoreUsers.UNASSIGNED_USER);
            }
         }
         changes.setAssignees(xxWid.getWorkItem(), assignees);
         return changes.executeIfNeeded();
      }
      return TransactionToken.SENTINEL;
   }

   public Collection<AtsUser> getAssignees() {
      List<AtsUser> assignees = new ArrayList<>();
      for (UserToken userTok : getSelected()) {
         assignees.add(AtsApiService.get().getUserService().getUserById(userTok));
      }
      return assignees;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   protected ViewerComparator getComparator() {
      Set<AtsUser> members = new HashSet<AtsUser>();
      members.addAll(xxWid.getTeamMembers());
      if (!members.isEmpty()) {
         return new XXUserTokenWithTeamComparator(getSelected(), members);
      }
      return super.getComparator();
   }

   @Override
   public ILabelProvider getLabelProvider() {
      Collection<AtsUser> teamMembers = xxWid.getTeamMembers();
      if (!teamMembers.isEmpty()) {
         return new UserCheckTreeLabelProvider(teamMembers);
      }
      return super.getLabelProvider();
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   public void filteredCheckboxTreeCreated(
      org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTree treeViewer) {
      treeViewer.getCheckboxTreeViewer().addCheckStateListener(event -> {
         if (event.getChecked() && SystemUser.UnAssigned.equals(event.getElement())) {
            // When UnAssigned is checked, uncheck all others
            for (Object checked : treeViewer.getCheckboxTreeViewer().getCheckedElements()) {
               if (!SystemUser.UnAssigned.equals(checked)) {
                  treeViewer.getCheckboxTreeViewer().setChecked(checked, false);
               }
            }
         } else if (event.getChecked() && !SystemUser.UnAssigned.equals(event.getElement())) {
            // When any other user is checked, uncheck UnAssigned
            treeViewer.getCheckboxTreeViewer().setChecked(SystemUser.UnAssigned, false);
         }
      });
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      if (xxArt.isArtifact() && getAttributeType().isValid()) {
         List<AtsUser> assignees = AtsApiService.get().getWorkItemService().getAssignees(xxWid.getWorkItem());
         setSelected(AtsObjects.toUserTokens(assignees), false);
      }
   }

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
