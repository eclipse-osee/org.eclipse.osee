/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkConfigurationWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchViewToken;

/**
 * @author Donald G. Dunne
 */
public class ConfigurationSearchWidget extends AbstractSearchWidget<XHyperlinkConfigurationWidget, ArtifactToken> implements TeamDefListener {

   public static SearchWidget ConfigurationWidget =
      new SearchWidget(732892316, "Configuration", "XHyperlinkConfigurationWidget");

   public ConfigurationSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(ConfigurationWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      XHyperlinkConfigurationWidget widget = getWidget();
      if (widget != null) {
         widget.set(data.getConfiguration());
      }
   }

   @Override
   public void updateAisOrTeamDefs() {
      Collection<TeamDefinition> teamDefs = getTeamDefs();
      if (teamDefs.size() == 1) {
         getWidget().setTeamDef(teamDefs.iterator().next());
      }
   }

   public BranchViewToken getSelected() {
      return getWidget().getToken();
   }

}
