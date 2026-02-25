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
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkBuildImpactStateWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Donald G. Dunne
 */
public class BuildImpactStateSearchWidget extends AbstractSearchWidget<XHyperlinkBuildImpactStateWidget, String> implements TeamDefListener {

   public static SearchWidget BuildImpactStateWidget =
      new SearchWidget(1432339, "Build Impact State", "XHyperlinkBuildImpactStateWidget");

   public BuildImpactStateSearchWidget(SearchWidget srchWidget, WorldEditorParameterSearchItem searchItem) {
      super(srchWidget, searchItem);
   }

   public BuildImpactStateSearchWidget(WorldEditorParameterSearchItem searchItem) {
      this(BuildImpactStateWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      XHyperlinkBuildImpactStateWidget widget = getWidget();
      if (widget != null) {
         widget.set(data.getBuildImpactState());
      }
   }

   public String getCurrentValue() {
      String value = getWidget().getCurrentValue();
      if (Strings.isInvalid(value)) {
         value = Widgets.NOT_SET;
      }
      return value;
   }

   @Override
   public void updateAisOrTeamDefs() {
      Collection<TeamDefinition> teamDefs = getTeamDefs();
      if (teamDefs.size() == 1) {
         getWidget().setTeamDef(teamDefs.iterator().next());
      }
   }

}
