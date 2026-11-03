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
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXTeamDefinitionsWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionSearchWidget extends AbstractSearchWidget<XXTeamDefinitionsWidget, IAtsTeamDefinition> {

   private static final String LABEL = "Team Definition(s)";
   public static SearchWidget TeamDefintiionWidget =
      new SearchWidget(3492378, LABEL, WidgetIdAts.XXTeamDefinitionsWidget);

   public TeamDefinitionSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(TeamDefintiionWidget, searchItem);
   }

   @Override
   public void widgetCreating(XWidget xWidget) {
      super.widgetCreating(xWidget);
      if (xWidget.getLabel().equals(LABEL)) {
         xWidget.set(XOption.CLEAR);
      }
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
      XXTeamDefinitionsWidget widget = getWidget();
      if (widget != null) {
         return widget.getSelected();
      }
      return null;
   }

   public void set(Collection<IAtsTeamDefinition> teamDefs) {
      getWidget().setSelected(teamDefs);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         List<IAtsTeamDefinition> teamDefs = new LinkedList<>();
         for (Long id : data.getTeamDefIds()) {
            IAtsTeamDefinition teamDef =
               AtsApiService.get().getConfigService().getConfigurations().getIdToTeamDef().get(id);
            if (teamDef != null) {
               teamDefs.add(teamDef);
            }
         }
         getWidget().setSelected(teamDefs);
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
