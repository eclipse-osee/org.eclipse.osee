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
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class UserTypeSearchWidget extends AbstractSearchWidget<XXUserTypeWidget, AtsSearchUserType> {

   private static final String LABEL = "User Type";
   public static SearchWidget UserTypeWidget = new SearchWidget(982384, LABEL, WidgetIdAts.XXUserTypeWidget);

   public UserTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(UserTypeWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (data.getUserType() != null) {
         getWidget().setSelected(data.getUserType());
      } else {
         getWidget().clear();
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget) {
      super.widgetCreating(xWidget);
      if (xWidget.getLabel().equals(LABEL)) {
         xWidget.set(XOption.CLEAR);
         xWidget.set(XOption.CLEAR);
      }
   }

}
