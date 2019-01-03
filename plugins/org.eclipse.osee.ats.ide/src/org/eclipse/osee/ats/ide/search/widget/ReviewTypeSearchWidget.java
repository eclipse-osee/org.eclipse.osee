/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
public class ReviewTypeSearchWidget extends AbstractXComboViewerSearchWidget<String> {

   public static final String REVIEW_TYPE = "Review Type";

   public ReviewTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(REVIEW_TYPE, searchItem);
   }

   public ReviewFormalType getType() {
      ReviewFormalType type = null;
      String value = get();
      if (Strings.isValid(value)) {
         try {
            type = ReviewFormalType.valueOf(value);
         } catch (Exception ex) {
            // do nothing
         }
      }
      return type;
   }

   public void set(ReviewFormalType reviewType) {
      String selected = reviewType == null ? "" : reviewType.name();
      if (Strings.isValid(selected)) {
         getWidget().setSelected(Arrays.asList(selected));
      }
   }

   @Override
   public void set(AtsSearchData data) {
      setup(getWidget());
      if (data.getReviewType() != null) {
         set(data.getReviewType());
      }
   }

   @Override
   public Collection<String> getInput() {
      return Arrays.asList("Formal", "InFormal");
   }
}
