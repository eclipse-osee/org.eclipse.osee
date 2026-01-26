/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkBuildImpactStateWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

/**
 * @author Donald G. Dunne
 */
public class BuildImpactStateSearchWidget {

   public static final String BUILD_IMPACT_STATE = "Build Impact State";
   private final WorldEditorParameterSearchItem searchItem;
   private XHyperlabelTeamDefinitionSelection teamSelection;
   private XHyperlinkBuildImpactStateWidget hypWidget;
   boolean listenerAdded = false;

   public BuildImpactStateSearchWidget(WorldEditorParameterSearchItem searchItem) {
      this.searchItem = searchItem;
   }

   public String getName() {
      return BUILD_IMPACT_STATE;
   }

   public void addWidget() {
      addWidget(0);
   }

   public void addWidget(int beginComposite) {
      String beginComp = (beginComposite > 0 ? "beginComposite=\"" + beginComposite + "\"" : "");
      String xml =
         String.format("<XWidget xwidgetType=\"XHyperlinkBuildImpactStateWidget\" " + beginComp + " displayName=\"" + //
            getName() + "\" horizontalLabel=\"true\" %s />", searchItem.getBeginComposite(beginComposite));
      searchItem.addWidgetXml(xml);
   }

   public XHyperlinkBuildImpactStateWidget getWidget() {
      return (XHyperlinkBuildImpactStateWidget) searchItem.getxWidgets().get(getName());
   }

   public void set(AtsSearchData data) {
      XHyperlinkBuildImpactStateWidget widget = getWidget();
      if (widget != null) {
         widget.set(data.getBuildImpactState());
      }
   }

   public void setupTeamDef(XWidget teamWidget) {
      this.teamSelection = (XHyperlabelTeamDefinitionSelection) teamWidget;
      teamWidget.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            setup(getWidget());
         }
      });
   }

   public void setup(XWidget xWidget) {
      getWidget().setToolTip("Select Single Team to populate Build Impact State list");
      Collection<TeamDefinition> teamDefs = teamSelection.getSelectedTeamDefintions();
      if (teamDefs.size() == 1) {
         getWidget().setTeamDef(teamDefs.iterator().next());
      }
      if (hypWidget == null && xWidget != null) {
         hypWidget = (XHyperlinkBuildImpactStateWidget) xWidget;
         if (!listenerAdded) {
            listenerAdded = true;
            hypWidget.addLabelMouseListener(new MouseAdapter() {

               @Override
               public void mouseUp(MouseEvent e) {
                  if (e.button == 3) {
                     clear();
                  }
               }

            });
         }
      }
   }

   public String getCurrentValue() {
      String value = getWidget().getCurrentValue();
      if (Strings.isInvalid(value)) {
         value = Widgets.NOT_SET;
      }
      return value;
   }

   protected void clear() {
      if (getWidget() != null) {
         setup(getWidget());
         XHyperlinkBuildImpactStateWidget widget = getWidget();
         widget.clear();
      }
   }

}
