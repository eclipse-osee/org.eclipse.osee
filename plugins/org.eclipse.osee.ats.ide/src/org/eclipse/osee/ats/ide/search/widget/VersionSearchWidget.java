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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.search.widget.base.TeamDefListener;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXVersionSelWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class VersionSearchWidget extends AbstractSearchWidget<XXVersionSelWidget, ArtifactToken> implements TeamDefListener {

   public static SearchWidget VersionWidget = new SearchWidget(9233478, "Version", WidgetIdAts.XXVersionSelWidget);

   public VersionSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(VersionWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         if (data.getVersionId() > 0) {
            IAtsVersion version =
               AtsApiService.get().getVersionService().getVersionById(ArtifactId.valueOf(data.getVersionId()));
            if (version != null) {
               getWidget().setSelected(Arrays.asList(version.getArtifactToken()));
            }
         }
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
