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
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractSearchWidget<SrchXWidget extends XWidget, ObjectType extends Object> implements ParamSearchWidget {

   protected final WorldEditorParameterSearchItem searchItem;
   protected final SearchWidget srchWidget;
   boolean listenerAdded = false;

   public AbstractSearchWidget(SearchWidget srchWidget, WorldEditorParameterSearchItem searchItem) {
      this.srchWidget = srchWidget;
      this.searchItem = searchItem;
   }

   @Override
   public String getName() {
      return srchWidget.getName();
   }

   public void addWidget() {
      addWidget(0);
   }

   public void addWidget(int beginComposite) {
      String xml = String.format("<XWidget xwidgetType=\"%s\" displayName=\"%s\" horizontalLabel=\"true\" %s />",
         srchWidget.getWidgetName(), srchWidget.getName(), searchItem.getBeginComposite(beginComposite));
      searchItem.addWidgetXml(xml, this);
   }

   public void addWidgetEndComposite() {
      String xml = String.format(
         "<XWidget xwidgetType=\"%s()\" displayName=\"%s\" horizontalLabel=\"true\" endComposite=\"true\" />",
         srchWidget.getWidgetName(), srchWidget.getName());
      searchItem.addWidgetXml(xml, this);
   }

   @SuppressWarnings("unchecked")
   public SrchXWidget getWidget() {
      return (SrchXWidget) searchItem.getxWidgets().get(srchWidget.getName());
   }

   /**
    * @return selected Team Definitions or computed Team Definitions from selected AIs
    */
   public Collection<TeamDefinition> getTeamDefs() {
      return searchItem.getTeamDefs();
   }

   @Override
   public void widgetCreated(XWidget xWidget) {
      if (this instanceof TeamDefListener) {
         getWidget().setToolTip("Select Single Team to populate " + getName());
      }
      if (!listenerAdded) {
         listenerAdded = true;
         if (Widgets.isAccessible(getWidget().getLabelWidget())) {
            getWidget().getLabelWidget().addMouseListener(new MouseAdapter() {

               @Override
               public void mouseUp(MouseEvent e) {
                  if (e.button == 3) {
                     clear();
                  }
               }

            });
         } else if (Widgets.isAccessible(getWidget().getLabelHyperlink())) {
            getWidget().getLabelHyperlink().addMouseListener(new MouseAdapter() {

               @Override
               public void mouseUp(MouseEvent e) {
                  if (e.button == 3) {
                     clear();
                  }
               }

            });
         }
      }
      refresh();
   }

   public void refresh() {
      // do nothing
   }

   public abstract void set(AtsSearchData data);

   public void clear() {
      if (getWidget() != null) {
         XWidget widget = getWidget();
         widget.clear();
      }
   }

}
