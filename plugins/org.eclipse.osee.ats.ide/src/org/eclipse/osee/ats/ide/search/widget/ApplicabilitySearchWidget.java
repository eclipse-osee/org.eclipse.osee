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
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkApplicabilityWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class ApplicabilitySearchWidget {

   public static final String APPLICABILITY = "Applicability";
   private final WorldEditorParameterSearchItem searchItem;
   private XHyperlabelTeamDefinitionSelection teamSelection;

   public ApplicabilitySearchWidget(WorldEditorParameterSearchItem searchItem) {
      this.searchItem = searchItem;
   }

   public void addWidget() {
      addWidget(0);
   }

   public void addWidget(int beginComposite) {
      searchItem.addWidgetXml(
         "<XWidget xwidgetType=\"XHyperlinkApplicabilityWidget\" beginComposite=\"4\" displayName=\"" + APPLICABILITY + "\" horizontalLabel=\"true\"/>" + //
            "<XWidget xwidgetType=\"XLabel\" displayName=\"               \" />" + //
            "<XWidget xwidgetType=\"XButtonPush\" displayLabel=\"false\" endComposite=\"true\" displayName=\"Generate Build Memo\" />" //
      );
   }

   public XHyperlinkApplicabilityWidget getWidget() {
      return (XHyperlinkApplicabilityWidget) searchItem.getxWidgets().get(APPLICABILITY);
   }

   public void set(AtsSearchData data) {
      XHyperlinkApplicabilityWidget widget = getWidget();
      if (widget != null) {
         widget.set(data.getApplicId());
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

   public void setup(XWidget widget) {
      getWidget().setToolTip("Select Single Team to populate Applicability list");
      Collection<TeamDefinition> teamDefs = teamSelection.getSelectedTeamDefintions();
      if (teamDefs.size() == 1) {
         getWidget().setTeamDef(teamDefs.iterator().next());
      }
   }

}
