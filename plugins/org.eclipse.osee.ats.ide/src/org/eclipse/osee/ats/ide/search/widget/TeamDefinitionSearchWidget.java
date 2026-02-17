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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionSearchWidget extends AbstractSearchWidget<XHyperlabelTeamDefinitionSelection, Object> {

   public static SearchWidget TeamDefintiionWidget =
      new SearchWidget(3492378, "Team Definition(s)", "XHyperlabelTeamDefinitionSelection");

   public TeamDefinitionSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(TeamDefintiionWidget, searchItem);
   }

   public Collection<Long> getIds() {
      List<Long> ids = new LinkedList<>();
      if (get() != null) {
         for (IAtsTeamDefinition teamDef : get()) {
            ids.add(teamDef.getId());
         }
      }
      return ids;
   }

   public Collection<TeamDefinition> get() {
      XHyperlabelTeamDefinitionSelection widget = getWidget();
      if (widget != null) {
         return widget.getSelectedTeamDefintions();
      }
      return null;
   }

   public void set(Collection<TeamDefinition> teamDefs) {
      getWidget().setSelectedTeamDefs(teamDefs);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         getWidget().handleClear();
         List<TeamDefinition> teamDefs = new LinkedList<>();
         for (Long id : data.getTeamDefIds()) {
            TeamDefinition teamDef =
               AtsApiService.get().getConfigService().getConfigurations().getIdToTeamDef().get(id);
            if (teamDef != null) {
               teamDefs.add(teamDef);
            }
         }
         set(teamDefs);
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget) {
      super.widgetCreated(xWidget);
      getWidget().addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            searchItem.updateAisOrTeamDefs();
         }
      });
   }

}
