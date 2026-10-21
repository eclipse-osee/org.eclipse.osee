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
package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XXUserTokenWithTeamComparator;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXUserTokenWidget extends XXAbstractBaseXWidget<UserToken> {

   public static final WidgetId ID = WidgetId.XXUserTokenWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXUserTokenWidget() {
      this(ID, "User");
   }

   public XXUserTokenWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      set(XOption.VAL_SINGLE_SEL);
      widData.setViewerComparator(new XXUserTokenWithTeamComparator());
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Collection<UserToken> getSelectable() {
      if (widData.is(XOption.ACTIVE)) {
         Collection<UserToken> activeUsers = OseeApiService.userSvc().getActiveUsers();
         activeUsers.remove(SystemUser.OseeSystem);
         activeUsers.remove(SystemUser.Anonymous);
         return activeUsers;
      }
      Collection<UserToken> users = OseeApiService.userSvc().getUsers();
      users.remove(SystemUser.OseeSystem);
      users.remove(SystemUser.Anonymous);
      return users;
   }

   @Override
   public UserToken getSentinel() {
      return UserToken.SENTINEL;
   }

   @Override
   public String getDisplayValue() {
      return Collections.toString(", ", Named.getNames(getSelected()));
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof UserToken;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

}
