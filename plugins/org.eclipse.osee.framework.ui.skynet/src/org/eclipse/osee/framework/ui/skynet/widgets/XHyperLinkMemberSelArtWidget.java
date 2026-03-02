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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.osgi.service.component.annotations.Component;

/**
 * Select users and store as single art id attributes. This is an AUTO_SAVE widget.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperLinkMemberSelArtWidget extends XHyperlinkMemberSelWidget {

   public static WidgetId ID = WidgetId.XHyperLinkMemberSelArtWidget;

   public XHyperLinkMemberSelArtWidget() {
      this(ID, "Select User");
   }

   public XHyperLinkMemberSelArtWidget(WidgetId widgetId, String displayLabel) {
      super(widgetId, displayLabel);
   }

   @Override
   public void refresh() {
      this.selectedUsers = getStoredUsers();
      super.refresh();
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      refresh();
   }

   public Set<UserToken> getStoredUsers() {
      Set<UserToken> users = new HashSet<>();
      try {
         for (Object artIdObj : getArtifact().getAttributeValues(getAttributeType())) {
            try {
               users.add(OseeApiService.userSvc().getUser((ArtifactId) artIdObj));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return users;
   }

   public void saveToArtifact() {
      try {
         getArtifact().setAttributeFromValues(getAttributeType(), getSelectedUsers());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null) {
               if (isRequiredEntry() && getSelectedUsers().isEmpty()) {
                  status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                     "Must Select " + getAttributeType().getUnqualifiedName());
               }
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
         }
      }
      return status;
   }

   @Override
   public boolean handleSelection() {
      boolean selected = super.handleSelection();
      if (selected) {
         saveToArtifact();
         getArtifact().persist(getClass().getSimpleName());
      }
      return selected;
   }
}
