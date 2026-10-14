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

import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;

/**
 * @author Donald G. Dunne
 */
public class BuildImpact2SearchWidget extends BuildImpactSearchWidget {

   public static SearchWidget BuildImpact2Widget =
      new SearchWidget(999807342, "Build Impact 2", WidgetIdAts.XXBuildImpactNameWidget);

   public BuildImpact2SearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(BuildImpact2Widget, searchItem);
   }

}
