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
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;

/**
 * @author Donald G. Dunne
 */
public class ColorTeamSearchWidget extends AbstractXComboViewerSearchWidget<String> {

   public static final String COLOR_TEAM = "Color Team";

   public ColorTeamSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(COLOR_TEAM, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         String colorTeam = data.getColorTeam();
         XComboViewer combo = getWidget();
         if (Strings.isValid(colorTeam)) {
            combo.setSelected(Arrays.asList(colorTeam));
         }
      }
   }

   @Override
   public Collection<String> getInput() {
      return Collections.castAll(AtsClientService.get().getEarnedValueService().getColorTeams());
   }

}
