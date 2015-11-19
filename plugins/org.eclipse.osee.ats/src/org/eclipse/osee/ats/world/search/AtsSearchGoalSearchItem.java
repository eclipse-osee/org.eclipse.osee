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
package org.eclipse.osee.ats.world.search;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.workflow.WorkItemType;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchGoalSearchItem extends AbstractWorkItemSearchItem {

   public static final String NAMESPACE = "ats.search.goal";
   private static final String TITLE = "Goal Search";

   public AtsSearchGoalSearchItem() {
      super(TITLE, NAMESPACE, AtsImage.GOAL);
   }

   public AtsSearchGoalSearchItem(AbstractWorkItemSearchItem searchItem) {
      super(searchItem, TITLE, NAMESPACE, AtsImage.GOAL);
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
   public String getParameterXWidgetXml() {
      if (showWorkItemWidgets()) {
         getWorkItemType().addWidget(14);
      }
      getTitle().addWidget();
      getStateType().addWidget(6);
      getUser().addWidget();
      getUserType().addWidget();
      String xml = xmlSb.toString() + "</xWidgets>";
      return xml;
   }

}
