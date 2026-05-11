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

package org.eclipse.osee.ats.ide.world.search;

import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.search.AtsSearchWorkflowSearchItem;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchReviewSearchItem extends AtsSearchWorkflowSearchItem {

   private static final String TITLE = "Review Search";
   public static final String NAMESPACE = AtsSearchUtil.ATS_QUERY_REVIEW_NAMESPACE;

   public AtsSearchReviewSearchItem() {
      super(TITLE, AtsImage.REVIEW);
   }

   public AtsSearchReviewSearchItem(AtsSearchReviewSearchItem searchItem) {
      super(searchItem, TITLE, AtsImage.REVIEW);
   }

   @Override
   public AtsSearchReviewSearchItem copy() {
      AtsSearchReviewSearchItem item = new AtsSearchReviewSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public AtsSearchReviewSearchItem copyProvider() {
      AtsSearchReviewSearchItem item = new AtsSearchReviewSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public String getShortNamePrefix() {
      return "RS";
   }

   @Override
   public String getNamespace() {
      return AtsSearchUtil.ATS_QUERY_REVIEW_NAMESPACE;
   }

   @Override
   public boolean isReviewSearch() {
      return true;
   }

   @Override
   protected boolean showWorkItemWidgets() {
      return true;
   }

   @Override
   protected void addWidgets() {
      getWorkItemTypeWidget().addWidget(1);
      getReviewTypeWidget().addWidgetEndComposite();
      getTitleWidget().addWidget();
      getAiWidget().addWidget(3);
      getTeamDefWidget().addWidget(2);
      getVersionWidget().addWidget(4);
      getStateTypeWidget().addWidget();
      getChangeTypeWidget().addWidget(6);
      getUserWidget().addWidget();
      getUserTypeWidget().addWidget();
      getStateNameWidget().addWidget();
   }

}
