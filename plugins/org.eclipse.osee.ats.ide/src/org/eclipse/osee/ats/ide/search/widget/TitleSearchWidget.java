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
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextWidget;

/**
 * @author Donald G. Dunne
 */
public class TitleSearchWidget extends AbstractSearchWidget<XTextWidget, Object> {

   public static SearchWidget TitleWidget = new SearchWidget(32458878, "Title", WidgetIdAts.XTextWidget);

   public TitleSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(TitleWidget, searchItem);
   }

   public String get() {
      XTextWidget text = getWidget();
      if (text != null) {
         return text.get();
      }
      return null;
   }

   public void set(String title) {
      getWidget().set(title);
   }

   @Override
   public void set(AtsSearchData data) {
      if (Strings.isValid(data.getTitle())) {
         set(data.getTitle());
      } else {
         set("");
      }
   }

}
