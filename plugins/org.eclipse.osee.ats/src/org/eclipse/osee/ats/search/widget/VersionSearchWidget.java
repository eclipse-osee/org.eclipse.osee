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
package org.eclipse.osee.ats.search.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class VersionSearchWidget extends AbstractXComboViewerSearchWidget<IAtsVersion> {

   public static final String VERSION = "Version";
   private XHyperlabelTeamDefinitionSelection teamSelection;

   public VersionSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(VERSION, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         if (data.getVersionId() > 0) {
            IAtsVersion version = AtsClientService.get().getCache().getAtsObject(data.getVersionId());
            if (version != null) {
               getWidget().setSelected(Arrays.asList(version));
            }
         }
      }
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
      versions.addAll(teamDefHoldingVersions.getVersions());
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
      return teamSelection.getSelectedTeamDefintions();
   }

   @Override
   public void setup(XWidget widget) {
      super.setup(widget);
      ((XComboViewer) widget).getCombo().setVisibleItemCount(25);
      widget.setToolTip("Select Team to populate Version list");
   }

   @Override
   public Collection<IAtsVersion> getInput() {
      List<IAtsVersion> versions = new LinkedList<>();
      Collection<IAtsTeamDefinition> teamDefArts = getSelectedTeamDefinitions();
      if (!teamDefArts.isEmpty()) {
         IAtsTeamDefinition teamDefHoldingVersions = teamDefArts.iterator().next().getTeamDefinitionHoldingVersions();
         if (teamDefHoldingVersions != null) {
            versions.addAll(getSortedVersions(teamDefHoldingVersions));
         }
      }
      return versions;
   }

}
