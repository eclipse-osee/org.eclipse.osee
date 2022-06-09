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

import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class AbstractSearchWidget<WidgetType extends XWidget, ObjectType extends Object> {

   protected final WorldEditorParameterSearchItem searchItem;
   protected final String name;
   protected final String widgetName;

   public AbstractSearchWidget(String name, String widgetName, WorldEditorParameterSearchItem searchItem) {
      this.name = name;
      this.widgetName = widgetName;
      this.searchItem = searchItem;
   }

   public void addWidget() {
      addWidget(0);
   }

   public void addWidget(int beginComposite) {
      String comboParams = (this instanceof AbstractXComboViewerSearchWidget<?>) ? "()" : "";
      String xml = String.format("<XWidget xwidgetType=\"%s%s\" displayName=\"%s\" horizontalLabel=\"true\" %s />",
         widgetName, comboParams, name, searchItem.getBeginComposite(beginComposite));
      searchItem.addWidgetXml(xml);
   }

   public void addWidgetEndComposite() {
      String xml = String.format(
         "<XWidget xwidgetType=\"%s()\" displayName=\"%s\" horizontalLabel=\"true\" endComposite=\"true\" />",
         widgetName, name);
      searchItem.addWidgetXml(xml);
   }

   @SuppressWarnings("unchecked")
   public WidgetType getWidget() {
      return (WidgetType) searchItem.getxWidgets().get(name);
   }

}
