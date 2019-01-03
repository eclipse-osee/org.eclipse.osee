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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.AtsImage;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchGoalSearchItem extends AbstractWorkItemSearchItem {

   private static final String TITLE = "Goal Search";
   public static final String NAMESPACE = AtsSearchUtil.ATS_QUERY_GOAL_NAMESPACE;

   public AtsSearchGoalSearchItem() {
      super(TITLE, AtsSearchUtil.ATS_QUERY_GOAL_NAMESPACE, AtsImage.GOAL);
   }

   public AtsSearchGoalSearchItem(AbstractWorkItemSearchItem searchItem) {
      super(searchItem, TITLE, AtsSearchUtil.ATS_QUERY_GOAL_NAMESPACE, AtsImage.GOAL);
   }

   @Override
   public AbstractWorkItemSearchItem copy() {
      AtsSearchGoalSearchItem item = new AtsSearchGoalSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public AbstractWorkItemSearchItem copyProvider() {
      AtsSearchGoalSearchItem item = new AtsSearchGoalSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public String getShortNamePrefix() {
      return "GS";
   }

   @Override
   Collection<WorkItemType> getWorkItemTypes() {
      return Arrays.asList(WorkItemType.Goal);
   }

   @Override
   protected boolean showWorkItemWidgets() {
      return false;
   }

   @Override
   protected void addWidgets() {
      if (showWorkItemWidgets()) {
         getWorkItemType().addWidget(14);
      }
      getTitle().addWidget();
      getStateType().addWidget(6);
      getUser().addWidget();
      getUserType().addWidget();
   }

}
