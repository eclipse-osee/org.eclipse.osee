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
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.search.widget.xx.XXBuildImpactStateWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class BuildImpactStateSearchWidget extends AbstractSearchWidget<XXBuildImpactStateWidget, String> {

   private static final String LABEL = "Build Impact State";
   public static SearchWidget BuildImpactStateWidget =
      new SearchWidget(1432339, LABEL, WidgetIdAts.XXBuildImpactStateWidget);

   public BuildImpactStateSearchWidget(SearchWidget srchWidget, WorldEditorParameterSearchItem searchItem) {
      super(srchWidget, searchItem);
   }

   public BuildImpactStateSearchWidget(WorldEditorParameterSearchItem searchItem) {
      this(BuildImpactStateWidget, searchItem);
   }

   @Override
   public void widgetCreating(XWidget xWidget) {
      super.widgetCreating(xWidget);
      if (xWidget.getLabel().startsWith(LABEL)) {
         xWidget.set(XOption.CLEAR);
      }
   }

   @Override
   public void set(AtsSearchData data) {
      XXBuildImpactStateWidget widget = getWidget();
      if (widget != null) {
         widget.setSelected(data.getBuildImpactState());
      }
   }

}
