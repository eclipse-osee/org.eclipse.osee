/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.search.widget;

import java.util.Collection;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class UserSearchWidget extends AbstractXHyperlinkWfdSearchWidget<AtsUser> {

   public static SearchWidget UserWidget = new SearchWidget(2383478, "User", "XHyperlinkWfdForObject");

   public UserSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(UserWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         String userId = data.getUserId();
         if (Strings.isValid(userId)) {
            AtsUser user = AtsApiService.get().getUserService().getUserByUserId(userId);
            getWidget().setSelected(user);
         }
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget) {
      super.widgetCreating(xWidget);
      xWidget.setUseToStringSorter(true);
   }

   @Override
   public Collection<AtsUser> getSelectable() {
      return Collections.castAll(AtsApiService.get().getUserService().getUsers(Active.Both));
   }

   @Override
   boolean isMultiSelect() {
      return false;
   }

}
