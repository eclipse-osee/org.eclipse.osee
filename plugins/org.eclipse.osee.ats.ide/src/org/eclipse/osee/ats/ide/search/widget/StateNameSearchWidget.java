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

import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.search.widget.xx.XXStateNameWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class StateNameSearchWidget extends AbstractSearchWidget<XXStateNameWidget, String> {

   private static final String LABEL = "State Name";
   public static SearchWidget StateNameWidget = new SearchWidget(238234789, LABEL, WidgetIdAts.XXStateNameWidget);

   public StateNameSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(StateNameWidget, searchItem);
   }

   @Override
   public void widgetCreating(XWidget xWidget) {
      super.widgetCreating(xWidget);
      if (xWidget.getLabel().equals(LABEL)) {
         xWidget.set(XOption.CLEAR);
         xWidget.setUseToStringSorter(true);
      }
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         List<String> stateNames = data.getStates();
         getWidget().setSelected(stateNames);
      }
   }

}
