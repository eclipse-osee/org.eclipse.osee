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

import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXUserTokenWidget;
import org.eclipse.swt.graphics.Image;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXOriginatorWidget extends XXUserTokenWidget {

   public static final WidgetId ID = WidgetIdAts.XXOriginatorWidget;

   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXOriginatorWidget() {
      super(ID, "Originator");
      setAttributeType(AtsAttributeTypes.CreatedBy);
      set(XOption.VAL_SINGLE_SEL);
      setOseeImage(CoreImage.USER);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public UserToken getSentinel() {
      return UserToken.SENTINEL;
   }

   @Override
   protected TransactionToken handleSelectedPersist() {
      if (xxWid.isWorkItem() && getAttributeType().isValid()) {
         Date now = new Date();
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set Originator");
         if (isNotSelected()) {
            changes.setCreatedBy(xxWid.getWorkItem(), AtsCoreUsers.UNASSIGNED_USER, true, now);
         } else {
            changes.setCreatedBy(xxWid.getWorkItem(),
               AtsApiService.get().getUserService().getUserById(getSelectedFirst()), true, now);
         }
         return changes.executeIfNeeded();
      }
      return TransactionToken.SENTINEL;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   protected Image getWidgetIcon() {
      UserToken origUser = getSelectedFirst();
      return FrameworkArtifactImageProvider.getUserImage(Arrays.asList(origUser));
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      if (xxWid.isWorkItem()) {
         String userId = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(getArtifact(),
            AtsAttributeTypes.CreatedBy, "");
         if (Strings.isValid(userId)) {
            UserToken userTok = OseeApiService.userSvc().getUserByUserId(userId);
            setSelected(userTok, false);
         }
      }
   }

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
