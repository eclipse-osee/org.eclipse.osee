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

   public static SearchWidget BuildImpactState2Widget =
      new SearchWidget(23388843, "Build Impact State 2", "XHyperlinkBuildImpactStateWidget");

   public BuildImpactState2SearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(BuildImpactState2Widget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      XHyperlinkBuildImpactStateWidget widget = getWidget();
      if (widget != null) {
         widget.set(data.getBuildImpactState2());
      }
   }

}
