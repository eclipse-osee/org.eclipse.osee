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
import org.eclipse.osee.ats.api.review.ReviewFormalType;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ReviewTypeSearchWidget extends AbstractXHyperlinkWfdSearchWidget<ReviewFormalType> {

   public static SearchWidget ReviewTypeWidget = new SearchWidget(98238, "Review Type", "XHyperlinkWfdForObject");

   public ReviewTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(ReviewTypeWidget, searchItem);
   }

   public void set(ReviewFormalType reviewType) {
      String selected = reviewType == null ? "" : reviewType.name();
      if (Strings.isValid(selected)) {
         getWidget().setSelected(Arrays.asList(selected));
      }
   }

   @Override
   public void set(AtsSearchData data) {
      if (data.getReviewType() != null) {
         set(data.getReviewType());
      }
   }

   @Override
   public Collection<ReviewFormalType> getSelectable() {
      return Arrays.asList(ReviewFormalType.Formal, ReviewFormalType.InFormal);
   }

   @Override
   boolean isMultiSelect() {
      return false;
   }
}
