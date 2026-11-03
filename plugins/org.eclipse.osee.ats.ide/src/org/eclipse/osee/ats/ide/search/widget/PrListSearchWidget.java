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
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.search.widget.base.TeamDefListener;
import org.eclipse.osee.ats.ide.search.widget.xx.XXPrBuildSelWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class PrListSearchWidget extends AbstractSearchWidget<XXPrBuildSelWidget, ArtifactToken> implements TeamDefListener {

   private static final String LABEL = "Previous PRs List";
   public static SearchWidget PrListWidget = new SearchWidget(883472, LABEL, WidgetIdAts.XXPrBuildSelWidget);

   public PrListSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(PrListWidget, searchItem);
   }

   @Override
   public void widgetCreating(XWidget xWidget) {
      super.widgetCreating(xWidget);
      if (xWidget.getLabel().equals(LABEL)) {
         xWidget.set(XOption.CLEAR);
      }
   }

   @Override
   public void set(AtsSearchData data) {
      XXPrBuildSelWidget widget = getWidget();
      if (widget != null) {
         if (data.getPreviousPrListId() > 0) {
            ArtifactToken art =
               AtsApiService.get().getQueryService().getArtifactToken(ArtifactId.valueOf(data.getPreviousPrListId()));
            widget.setSelected(art);
         } else {
            widget.setSelected(ArtifactToken.SENTINEL);
         }
      }
   }

   public String getCurrentValue() {
      String value = getSelectedFirst() == null ? "" : getWidget().getSelectedFirst().getName();
      return value;
   }

   public ArtifactToken getArtifactToken() {
      return getWidget().getSelectedFirst();
   }

   @Override
   public void updateAisOrTeamDefs() {
      Collection<IAtsTeamDefinition> teamDefs = getTeamDefs();
      if (teamDefs.size() == 1) {
         getWidget().setTeamDef(teamDefs.iterator().next());
      } else {
         getWidget().setTeamDef(null);
         //         getWidget().clear();
      }
   }

}
