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
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkPrBuildSelection;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Donald G. Dunne
 */
public class PrListSearchWidget extends AbstractSearchWidget<XHyperlinkPrBuildSelection, ArtifactToken> implements TeamDefListener {

   public static SearchWidget PrListWidget =
      new SearchWidget(883472, "Previous PRs List", "XHyperlinkPrBuildSelection");

   public PrListSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(PrListWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      XHyperlinkPrBuildSelection widget = getWidget();
      if (widget != null) {
         if (data.getPreviousPrListId() > 0) {
            ArtifactToken art =
               AtsApiService.get().getQueryService().getArtifactToken(ArtifactId.valueOf(data.getPreviousPrListId()));
            widget.set(art);
         } else {
            widget.set(ArtifactToken.SENTINEL);
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

   public ArtifactToken getArtifactToken() {
      return getWidget().getToken();
   }

   @Override
   public void updateAisOrTeamDefs() {
      Collection<TeamDefinition> teamDefs = getTeamDefs();
      if (teamDefs.size() == 1) {
         getWidget().setTeamDef(teamDefs.iterator().next());
      }
   }

}
