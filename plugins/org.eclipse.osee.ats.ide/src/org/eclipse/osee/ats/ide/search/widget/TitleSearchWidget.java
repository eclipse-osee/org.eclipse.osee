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
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;

/**
 * @author Donald G. Dunne
 */
public class TitleSearchWidget extends AbstractSearchWidget<XXTextWidget, String> {

   private static final String LABEL = "Title";
   public static SearchWidget TitleWidget = new SearchWidget(32458878, LABEL, WidgetIdAts.XXTextWidget);

   public TitleSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(TitleWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (Strings.isValid(data.getTitle())) {
         getWidget().setSelected(data.getTitle());
      } else {
         getWidget().clear();
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget) {
      super.widgetCreating(xWidget);
      if (xWidget.getLabel().equals(LABEL)) {
         xWidget.set(XOption.CLEAR, XOption.WRITE, XOption.BORDER, XOption.VAL_SINGLE_TEXT, XOption.INLINE_EDIT,
            XOption.FILL_HORZ);
      }
   }

}
