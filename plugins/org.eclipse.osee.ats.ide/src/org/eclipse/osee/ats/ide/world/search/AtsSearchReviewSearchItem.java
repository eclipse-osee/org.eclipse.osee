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
package org.eclipse.osee.ats.ide.world.search;

import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.ide.AtsImage;
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
   protected void addWidgets() {
      addWidgetXml(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Peer Review\" labelAfter=\"true\" horizontalLabel=\"true\" beginComposite=\"6\"/>");
      addWidgetXml(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Decision Review\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      getReviewType().addWidgetEndComposite();
      getTitle().addWidget();
      getAi().addWidget(3);
      getTeamDef().addWidget(2);
      getVersion().addWidget(8);
      getStateType().addWidget();
      getUser().addWidget();
      getUserType().addWidget();
      getColorTeam().addWidget(4);
      getStateName().addWidget();
      getProgram().addWidget(8);
      getInsertion().addWidget();
      getInsertionActivity().addWidget();
      getWorkPackage().addWidget();
   }

}
