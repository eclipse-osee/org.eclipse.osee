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
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.search.widget.base.ParamSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.search.widget.base.TeamDefListener;
import org.eclipse.osee.ats.ide.workdef.XWidgetBuilderAts;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
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

   public boolean isSelected() {
      return getWidget().isEmpty();
   }

   @SuppressWarnings("unchecked")
   public ObjectType getSelectedFirst() {
      if (getWidget() instanceof XXAbstractBaseXWidget) {
         return ((XXAbstractBaseXWidget<ObjectType>) getWidget()).getSelectedFirst();
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   public Collection<ObjectType> getSelected() {
      if (getWidget() instanceof XXAbstractBaseXWidget) {
         return ((XXAbstractBaseXWidget<ObjectType>) getWidget()).getSelected();
      }
      return ((XXAbstractBaseXWidget<ObjectType>) getWidget()).getSelected();
   }

   public void addWidget(XWidgetBuilderAts wba, XOption... options) {
      addWidget(wba, 0, options);
   }

   public void addWidget(XWidgetBuilderAts wba, int beginComposite, XOption... options) {
      createWidgetInBuilder(wba, beginComposite, options);
      wba.startComp(beginComposite);
      searchItem.labelToParamSearchWidgets.put(getName(), this);
   }

   private void createWidgetInBuilder(XWidgetBuilderAts wba, int beginComposite, XOption... options) {
      Conditions.assertNotNullOrEmpty(srchWidget.getName(), "Search Widget must have a Name [%s]",
         srchWidget.toStringWithId());
      Conditions.assertTrue(srchWidget.getWidgetId().isValid(), "Search Widget must have a valid WidgetId [%s]",
         srchWidget.toStringWithId());
      wba.andWidget(srchWidget.getName(), srchWidget.getWidgetId());
      wba.and(options);
      wba.andHorizLabel();
   }

   public void addWidgetEndComposite(XWidgetBuilderAts wba) {
      createWidgetInBuilder(wba, 0);
      wba.endComp();
   }

   @SuppressWarnings("unchecked")
   public SrchXWidget getWidget() {
      return (SrchXWidget) searchItem.getxWidgets().get(srchWidget.getName());
   }

   /**
    * @return selected Team Definitions or computed Team Definitions from selected AIs
    */
   public Collection<IAtsTeamDefinition> getTeamDefs() {
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
         } else if (Widgets.isAccessible(getWidget().getLabelLinkWidget())) {
            getWidget().getLabelLinkWidget().addMouseListener(new MouseAdapter() {

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
         widget.handleClear();
      }
   }

}
