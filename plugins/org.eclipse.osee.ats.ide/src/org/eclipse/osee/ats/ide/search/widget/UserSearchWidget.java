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

import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXAssigneesWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class UserSearchWidget extends AbstractSearchWidget<XXAssigneesWidget, UserToken> {

   private static final String LABEL = "User";
   public static SearchWidget UserSearchWidget = new SearchWidget(2383478, LABEL, WidgetIdAts.XXAssigneesWidget);

   public UserSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(UserSearchWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         String userId = data.getUserId();
         if (Strings.isValid(userId)) {
            UserToken user = AtsApiService.get().getUserService().getUserTokenByUserId(userId);
            getWidget().setSelected(user);
         } else {
            getWidget().clear();
         }
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget) {
      super.widgetCreating(xWidget);
      if (xWidget.getLabel().equals(LABEL)) {
         xWidget.setUseToStringSorter(true);
         xWidget.set(XOption.CLEAR);
      }
   }

}
