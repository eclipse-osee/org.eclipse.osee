/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.search.widget.base.TeamDefListener;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXChangeTypeWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeSearchWidget extends AbstractSearchWidget<XXChangeTypeWidget, String> implements TeamDefListener {

   private static final String LABEL = "Change Type(s)";
   public static SearchWidget ChangeTypeWidget = new SearchWidget(7, LABEL, WidgetIdAts.XXChangeTypeWidget);

   public ChangeTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(ChangeTypeWidget, searchItem);
   }

   @Override
   public void widgetCreating(XWidget xWidget) {
      super.widgetCreating(xWidget);
      if (xWidget.getLabel().equals(LABEL)) {
         xWidget.set(XOption.CLEAR);
         xWidget.set(XOption.VAL_MULTI_SEL);
         ((XXChangeTypeWidget) xWidget).setTeamDefMustBeSelected(true);
      }
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         List<String> changeTypes = data.getChangeTypes();
         getWidget().setSelected(changeTypes);
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
