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
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkApplicabilityWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;

/**
 * @author Donald G. Dunne
 */
public class ApplicabilitySearchWidget extends AbstractSearchWidget<XHyperlinkApplicabilityWidget, Long> implements TeamDefListener {

   public static SearchWidget ApplicabilityWidget =
      new SearchWidget(1234124, "Applicability", "XHyperlinkApplicabilityWidget");

   public ApplicabilitySearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(ApplicabilityWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      XHyperlinkApplicabilityWidget widget = getWidget();
      if (widget != null) {
         widget.set(data.getApplicId());
      }
   }

   @Override
   public void updateAisOrTeamDefs() {
      Collection<TeamDefinition> teamDefs = searchItem.getTeamDefs();
      if (teamDefs.size() == 1) {
         getWidget().setTeamDef(teamDefs.iterator().next());
      }
   }

}
