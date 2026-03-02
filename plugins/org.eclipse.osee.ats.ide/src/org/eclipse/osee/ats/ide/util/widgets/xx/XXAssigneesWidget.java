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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXUserTokenWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXAssigneesWidget extends XXUserTokenWidget {

   public static final WidgetId ID = WidgetIdAts.XXAssigneesWidget;
   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   public XXAssigneesWidget() {
      super(ID, "Assignee(s)");
   }

   @Override
   public void setWidData(XWidgetData widData) {
      super.setWidData(widData);
      setAttributeType(AtsAttributeTypes.CurrentStateAssignee);
      setSingleSelect(true);
      widData.add(XOption.ACTIVE);
   }

   @Override
   protected UserToken getSentinel() {
      return UserToken.SENTINEL;
   }

   @Override
   protected void handleSelectedPersist() {
      AtsApi atsApi = AtsApiService.get();
      if (xxWid.isWorkItem()) {
         IAtsChangeSet changes = atsApi.createChangeSet("Set");
         Set<AtsUser> assignees = new HashSet<>();
         if (selected.isEmpty()) {
            assignees.add(AtsCoreUsers.UNASSIGNED_USER);
         } else {
            for (UserToken userTok : selected) {
               AtsUser aUser = atsApi.getUserService().getUserById(userTok);
               if (aUser != null) {
                  assignees.add(aUser);
               }
            }
         }
         changes.setAssignees(xxWid.getWorkItem(), assignees);
         changes.executeIfNeeded();
      }
   }

   @Override
   public Collection<UserToken> getSelected() {
      AtsApi atsApi = AtsApiService.get();
      if (xxWid.isWorkItem()) {
         List<AtsUser> assignees = atsApi.getWorkItemService().getAssignees(xxWid.getWorkItem());
         selected.clear();
         selected.addAll(AtsObjects.toUserTokens(assignees));
      }
      return super.getSelected();
   }

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
