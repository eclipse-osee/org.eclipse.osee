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

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionSearchWidget {

   public static final String TEAM_DEFINITIONS = "Team Definition(s)";
   private final WorldEditorParameterSearchItem searchItem;

   public TeamDefinitionSearchWidget(WorldEditorParameterSearchItem searchItem) {
      this.searchItem = searchItem;
   }

   public void addWidget(int beginComposite) {
      searchItem.addWidgetXml(String.format(
         "<XWidget displayName=\"%s\" xwidgetType=\"XHyperlabelTeamDefinitionSelection\" horizontalLabel=\"true\" %s />",
         TEAM_DEFINITIONS, searchItem.getBeginComposite(beginComposite)));
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

   public Collection<IAtsTeamDefinition> get() {
      XHyperlabelTeamDefinitionSelection widget = getWidget();
      if (widget != null) {
         return AtsApiService.get().getTeamDefinitionService().getTeamDefs(widget.getSelectedTeamDefintions());
      }
      return null;
   }

   public XHyperlabelTeamDefinitionSelection getWidget() {
      return (XHyperlabelTeamDefinitionSelection) searchItem.getxWidgets().get(TEAM_DEFINITIONS);
   }

   public void set(Collection<TeamDefinition> teamDefs) {
      getWidget().setSelectedTeamDefs(teamDefs);
   }

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

   public void setTeamDefs(List<TeamDefinition> teamDefs) {
      getWidget().setTeamDefs(teamDefs);
   }

}
