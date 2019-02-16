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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.config.JaxTeamDefinition;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
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
         return TeamDefinitions.getTeamDefs(widget.getSelectedTeamDefintions(), AtsClientService.get());
      }
      return null;
   }

   public XHyperlabelTeamDefinitionSelection getWidget() {
      return (XHyperlabelTeamDefinitionSelection) searchItem.getxWidgets().get(TEAM_DEFINITIONS);
   }

   public void set(Collection<JaxTeamDefinition> teamDefs) {
      getWidget().setSelectedTeamDefs(teamDefs);
   }

   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         getWidget().handleClear();
         List<JaxTeamDefinition> teamDefs = new LinkedList<>();
         for (Long id : data.getTeamDefIds()) {
            JaxTeamDefinition teamDef =
               AtsClientService.get().getConfigService().getConfigurations().getIdToTeamDef().get(id);
            if (teamDef != null) {
               teamDefs.add(teamDef);
            }
         }
         set(teamDefs);
      }
   }

}
