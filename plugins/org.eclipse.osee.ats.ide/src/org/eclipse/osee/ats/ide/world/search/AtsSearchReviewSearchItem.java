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
      getWorkItemType().addWidget(1);
      getReviewType().addWidgetEndComposite();
      getTitle().addWidget();
      getAi().addWidget(3);
      getTeamDef().addWidget(2);
      getVersion().addWidget(4);
      getStateType().addWidget();
      getChangeType().addWidget(6);
      getUser().addWidget();
      getUserType().addWidget();
      getStateName().addWidget();
      getProgram().addWidget(8);
      getInsertion().addWidget();
      getInsertionActivity().addWidget();
      getWorkPackage().addWidget();
   }

}
