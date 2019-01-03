/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
      searchItem.addWidgetXml(
         String.format("<XWidget xwidgetType=\"%s()\" displayName=\"%s\" horizontalLabel=\"true\" %s />", widgetName,
            name, searchItem.getBeginComposite(beginComposite)));
   }

   public void addWidgetEndComposite() {
      searchItem.addWidgetXml(String.format(
         "<XWidget xwidgetType=\"%s()\" displayName=\"%s\" horizontalLabel=\"true\" endComposite=\"true\" />",
         widgetName, name));
   }

   @SuppressWarnings("unchecked")
   public WidgetType getWidget() {
      return (WidgetType) searchItem.getxWidgets().get(name);
   }

}
