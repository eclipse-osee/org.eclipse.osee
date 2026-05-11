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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelActionableItemSelection;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionLabelProvider;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class VersionSearchWidget extends AbstractXHyperlinkWfdSearchWidget<IAtsVersion> implements TeamDefListener {

   public static SearchWidget VersionWidget = new SearchWidget(9233478, "Version", "XHyperlinkWfdForObject");
   private XHyperlabelActionableItemSelection actionableSelection;

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
               getWidget().setSelected(Arrays.asList(version));
            }
         }
      }
   }

   private List<IAtsVersion> getSortedVersions(IAtsTeamDefinition teamDefHoldingVersions) {
      List<IAtsVersion> versions = new ArrayList<>();
      versions.addAll(AtsApiService.get().getVersionService().getVersions(teamDefHoldingVersions));
      Collections.sort(versions, new Comparator<IAtsVersion>() {

         @Override
         public int compare(IAtsVersion aObj1, IAtsVersion aObj2) {
            if (!aObj1.isReleased() && aObj2.isReleased()) {
               return -1;
            } else if (aObj1.isReleased() && !aObj2.isReleased()) {
               return 1;
            }
            return aObj1.getName().compareTo(aObj2.getName());
         }
      });
      return versions;
   }

   public Collection<IAtsTeamDefinition> getSelectedTeamDefinitions() {
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(searchItem.getTeamDefs());
   }

   public Collection<IAtsActionableItem> getSelectedActionableItems() {
      if (actionableSelection == null) {
         return java.util.Collections.emptyList();
      }
      return actionableSelection.getSelectedActionableItems();
   }

   @Override
   public void updateAisOrTeamDefs() {
      if (searchItem.getTeamDefWidget() != null && getWidget() != null) {
         getWidget().setSelectable(org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getSelectable()));
      }
   }

   @Override
   public Collection<IAtsVersion> getSelectable() {
      Set<IAtsVersion> versions = new HashSet<>();
      Collection<IAtsActionableItem> teamActArts = getSelectedActionableItems();
      Collection<IAtsTeamDefinition> teamDefArts = getSelectedTeamDefinitions();
      if (!teamDefArts.isEmpty()) {

         IAtsTeamDefinition teamDefHoldingVersions =
            AtsApiService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(
               teamDefArts.iterator().next());
         if (teamDefHoldingVersions != null) {
            versions.addAll(getSortedVersions(teamDefHoldingVersions));
         }
      }
      if (!teamActArts.isEmpty()) {

         for (IAtsActionableItem ai : teamActArts) {
            for (ArtifactToken teamDefArt : AtsApiService.get().getRelationResolver().getRelated(ai,
               AtsRelationTypes.TeamActionableItem_TeamDefinition)) {
               IAtsTeamDefinition teamDef =
                  AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(teamDefArt);
               IAtsTeamDefinition teamDefHoldVer =
                  AtsApiService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(teamDef);
               versions.addAll(getSortedVersions(teamDefHoldVer));
            }
         }
      }
      return versions;
   }

   @Override
   boolean isMultiSelect() {
      return false;
   }

   @Override
   public void widgetCreating(XWidget xWidget) {
      super.widgetCreating(xWidget);
      xWidget.setLabelProvider(new VersionLabelProvider());
   }

}
