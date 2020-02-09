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
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelActionableItemSelection;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class VersionSearchWidget extends AbstractXComboViewerSearchWidget<IAtsVersion> {

   public static final String VERSION = "Version";
   private XHyperlabelTeamDefinitionSelection teamSelection;
   private XHyperlabelActionableItemSelection ActionableSelection;

   public VersionSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(VERSION, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         if (data.getVersionId() > 0) {
            IAtsVersion version =
               AtsClientService.get().getVersionService().getVersionById(ArtifactId.valueOf(data.getVersionId()));
            if (version != null) {
               getWidget().setSelected(Arrays.asList(version));
            }
         }
      }
   }

   public void setupActionableActs(XWidget teamCombo) {
      this.ActionableSelection = (XHyperlabelActionableItemSelection) teamCombo;
      teamCombo.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            setup(getWidget());
         }
      });
   }

   public void setupTeamDef(XWidget teamCombo) {
      this.teamSelection = (XHyperlabelTeamDefinitionSelection) teamCombo;
      teamCombo.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            setup(getWidget());
         }
      });
   }

   @Override
   public String getInitialText() {
      if (teamSelection == null || teamSelection.getSelectedTeamDefintions().isEmpty()) {
         return "--select team--";
      } else {
         return "";
      }
   }

   private List<IAtsVersion> getSortedVersions(IAtsTeamDefinition teamDefHoldingVersions) {
      List<IAtsVersion> versions = new ArrayList<>();
      versions.addAll(AtsClientService.get().getVersionService().getVersions(teamDefHoldingVersions));
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
      if (teamSelection == null) {
         return java.util.Collections.emptyList();
      }
      return AtsClientService.get().getTeamDefinitionService().getTeamDefs(teamSelection.getSelectedTeamDefintions());
   }

   public Collection<IAtsActionableItem> getSelectedActionableItems() {
      if (ActionableSelection == null) {
         return java.util.Collections.emptyList();
      }
      return ActionableSelection.getSelectedActionableItems();
   }

   @Override
   public void setup(XWidget widget) {
      super.setup(widget);
      ((XComboViewer) widget).getCombo().setVisibleItemCount(25);
      widget.setToolTip("Select Team to populate Version list");
   }

   @Override
   public Collection<IAtsVersion> getInput() {
      Set<IAtsVersion> versions = new HashSet<>();
      Collection<IAtsActionableItem> teamActArts = getSelectedActionableItems();
      Collection<IAtsTeamDefinition> teamDefArts = getSelectedTeamDefinitions();
      if (!teamDefArts.isEmpty()) {

         IAtsTeamDefinition teamDefHoldingVersions =
            AtsClientService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(
               teamDefArts.iterator().next());
         if (teamDefHoldingVersions != null) {
            versions.addAll(getSortedVersions(teamDefHoldingVersions));
         }
      }
      if (!teamActArts.isEmpty()) {

         for (IAtsActionableItem ai : teamActArts) {
            for (ArtifactToken teamDefArt : AtsClientService.get().getRelationResolver().getRelated(ai,
               AtsRelationTypes.TeamActionableItem_TeamDefinition)) {
               IAtsTeamDefinition teamDef =
                  AtsClientService.get().getTeamDefinitionService().getTeamDefinitionById(teamDefArt);
               IAtsTeamDefinition teamDefHoldVer =
                  AtsClientService.get().getTeamDefinitionService().getTeamDefinitionHoldingVersions(teamDef);
               versions.addAll(getSortedVersions(teamDefHoldVer));
            }
         }
      }
      return versions;
   }

}
