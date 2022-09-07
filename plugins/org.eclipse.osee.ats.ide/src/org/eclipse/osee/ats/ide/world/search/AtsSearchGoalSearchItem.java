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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.WorkItemType;

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
   public Collection<WorkItemType> getWorkItemTypes() {
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
