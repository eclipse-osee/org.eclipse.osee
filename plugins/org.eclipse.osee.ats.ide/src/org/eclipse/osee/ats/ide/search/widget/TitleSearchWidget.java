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
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;

/**
 * @author Donald G. Dunne
 */
public class TitleSearchWidget {

   private final WorldEditorParameterSearchItem searchItem;

   public TitleSearchWidget(WorldEditorParameterSearchItem searchItem) {
      this.searchItem = searchItem;
   }

   public void addWidget() {
      addWidget(0);
   }

   public void addWidget(int beginComposite) {
      searchItem.addWidgetXml(
         String.format("<XWidget xwidgetType=\"XText\" displayName=\"Title\" horizontalLabel=\"true\"%s />",
            searchItem.getBeginComposite(beginComposite)));
   }

   public String get() {
      XText text = getWidget();
      if (text != null) {
         return text.get();
      }
      return null;
   }

   public XText getWidget() {
      return (XText) searchItem.getxWidgets().get("Title");
   }

   public void set(String title) {
      getWidget().set(title);
   }

   public void set(AtsSearchData data) {
      if (Strings.isValid(data.getTitle())) {
         set(data.getTitle());
      } else {
         set("");
      }
   }

}
