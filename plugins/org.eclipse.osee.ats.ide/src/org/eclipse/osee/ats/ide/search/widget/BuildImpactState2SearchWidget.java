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

import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkBuildImpactStateWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;

/**
 * @author Donald G. Dunne
 */
public class BuildImpactState2SearchWidget extends BuildImpactStateSearchWidget {

   public static final String BUILD_IMPACT_STATE_2 = "Build Impact State 2";

   public BuildImpactState2SearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(searchItem);
   }

   @Override
   public String getName() {
      return BUILD_IMPACT_STATE_2;
   }

   @Override
   public void set(AtsSearchData data) {
      XHyperlinkBuildImpactStateWidget widget = getWidget();
      if (widget != null) {
         widget.set(data.getBuildImpactState2());
      }
   }

}
