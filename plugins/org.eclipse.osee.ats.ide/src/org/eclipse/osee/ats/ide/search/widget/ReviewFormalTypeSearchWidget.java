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

import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.review.ReviewFormalType;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.search.widget.base.SearchWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXReviewFormalTypeWidget;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class ReviewFormalTypeSearchWidget extends AbstractSearchWidget<XXReviewFormalTypeWidget, ReviewFormalType> {

   private static final String LABEL = "Review Type";
   public static SearchWidget ReviewTypeWidget = new SearchWidget(98238, LABEL, WidgetIdAts.XXReviewFormalTypeWidget);

   public ReviewFormalTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(ReviewTypeWidget, searchItem);
   }

   @Override
   public void widgetCreating(XWidget xWidget) {
      super.widgetCreating(xWidget);
      if (xWidget.getLabel().equals(LABEL)) {
         xWidget.set(XOption.CLEAR);
      }
   }

   @Override
   public void set(AtsSearchData data) {
      if (data.getReviewFormalType() != null) {
         getWidget().setSelected(data.getReviewFormalType());
      }
   }

}
