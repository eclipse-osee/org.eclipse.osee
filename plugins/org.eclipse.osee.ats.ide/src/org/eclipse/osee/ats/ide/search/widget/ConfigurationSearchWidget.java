/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkConfigurationWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

/**
 * @author Donald G. Dunne
 */
public class ConfigurationSearchWidget {

   public static final String CONFIGURATION = "Configuration";
   private final WorldEditorParameterSearchItem searchItem;
   private XHyperlabelTeamDefinitionSelection teamSelection;
   private XHyperlinkConfigurationWidget hypWidget;
   boolean listenerAdded = false;

   public ConfigurationSearchWidget(WorldEditorParameterSearchItem searchItem) {
      this.searchItem = searchItem;
   }

   public void addWidget() {
      addWidget(0);
   }

   public void addWidget(int beginComposite) {
      String beginComp = (beginComposite > 0 ? "beginComposite=\"" + beginComposite + "\"" : "");
      searchItem.addWidgetXml(
         "<XWidget xwidgetType=\"XHyperlinkConfigurationWidget\" " + beginComp + " displayName=\"" + CONFIGURATION + "\" horizontalLabel=\"true\"/>");
   }

   public XHyperlinkConfigurationWidget getWidget() {
      return (XHyperlinkConfigurationWidget) searchItem.getxWidgets().get(CONFIGURATION);
   }

   public void set(AtsSearchData data) {
      XHyperlinkConfigurationWidget widget = getWidget();
      if (widget != null) {
         widget.set(data.getConfiguration());
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
      getWidget().setToolTip("Select Single Team to populate Applicability list");
      Collection<TeamDefinition> teamDefs = teamSelection.getSelectedTeamDefintions();
      if (teamDefs.size() == 1) {
         getWidget().setTeamDef(teamDefs.iterator().next());
      }
      if (hypWidget == null && xWidget != null) {
         hypWidget = (XHyperlinkConfigurationWidget) xWidget;
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

   public BranchViewToken getSelected() {
      return getWidget().getToken();
   }

   protected void clear() {
      if (getWidget() != null) {
         setup(getWidget());
         XHyperlinkConfigurationWidget widget = getWidget();
         widget.clear();
      }
   }

}
