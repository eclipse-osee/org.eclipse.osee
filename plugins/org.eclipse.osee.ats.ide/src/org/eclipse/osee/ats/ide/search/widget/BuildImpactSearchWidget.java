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
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.search.widget.base.TeamDefListener;
import org.eclipse.osee.ats.ide.search.widget.xx.XXBuildImpactNameWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class BuildImpactSearchWidget extends AbstractSearchWidget<XXBuildImpactNameWidget, String> implements TeamDefListener {

   private static final String LABEL = "Build Impact";
   public static SearchWidget BuildImpactWidget = new SearchWidget(9935478, LABEL, WidgetIdAts.XXBuildImpactNameWidget);

   public BuildImpactSearchWidget(WorldEditorParameterSearchItem searchItem) {
      this(BuildImpactWidget, searchItem);
   }

   public BuildImpactSearchWidget(SearchWidget srchWidget, WorldEditorParameterSearchItem searchItem) {
      super(srchWidget, searchItem);
   }

   @Override
   public void widgetCreating(XWidget xWidget) {
      super.widgetCreating(xWidget);
      if (xWidget.getLabel().startsWith(LABEL)) {
         xWidget.set(XOption.CLEAR);
      }
   }

   @Override
   public void set(AtsSearchData data) {
      XXBuildImpactNameWidget widget = getWidget();
      if (widget != null) {
         widget.setSelected(data.getBuildImpact());
      }
   }

   @Override
   public void updateAisOrTeamDefs() {
      Collection<IAtsTeamDefinition> teamDefs = getTeamDefs();
      if (teamDefs.size() == 1) {
         getWidget().setTeamDef(teamDefs.iterator().next());
      } else {
         getWidget().setTeamDef(null);
         getWidget().clear();
      }
   }

}
