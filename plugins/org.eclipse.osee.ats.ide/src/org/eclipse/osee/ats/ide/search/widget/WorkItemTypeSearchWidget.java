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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;

/**
 * @author Donald G. Dunne
 */
public class WorkItemTypeSearchWidget {

   private final WorldEditorParameterSearchItem searchItem;
   private boolean reviewSearch;

   public WorkItemTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      this.searchItem = searchItem;
   }

   public void addWidget(int beginComposite) {
      if (reviewSearch) {
         searchItem.addWidgetXml(
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Peer Review\" labelAfter=\"true\" horizontalLabel=\"true\" beginComposite=\"6\"/>");
         searchItem.addWidgetXml(
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Decision Review\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      } else {
         searchItem.addWidgetXml(String.format(
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Team Workflow\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\" %s />",
            searchItem.getBeginComposite(beginComposite)));
         searchItem.addWidgetXml(
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Task\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
         searchItem.addWidgetXml(
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Peer Review\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
         searchItem.addWidgetXml(
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Decision Review\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
         searchItem.addWidgetXml(
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Goal\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
         searchItem.addWidgetXml(
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Agile Sprint\" labelAfter=\"true\" horizontalLabel=\"true\" />");
         searchItem.addWidgetXml(
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Agile Backlog\" labelAfter=\"true\" horizontalLabel=\"true\" endComposite=\"true\" />");
      }
   }

   public XCheckBox getWidget(String workItemType) {
      return (XCheckBox) searchItem.getxWidgets().get(workItemType);
   }

   public XCheckBox getWidget(WorkItemType type) {
      return getWidget(type.getDisplayName());
   }

   public void set(WorkItemType type, boolean selected) {
      set(type.getDisplayName(), selected);
   }

   public void set(String workItemType, boolean selected) {
      XCheckBox checkbox = getWidget(workItemType);
      if (checkbox != null) {
         checkbox.set(selected);
      }
   }

   public Collection<WorkItemType> get() {
      List<WorkItemType> types = new ArrayList<>();
      if (isReviewSearch()) {
         addTypeIfChecked(types, WorkItemType.PeerReview);
         addTypeIfChecked(types, WorkItemType.DecisionReview);
      } else {
         addTypeIfChecked(types, WorkItemType.TeamWorkflow);
         addTypeIfChecked(types, WorkItemType.Task);
         addTypeIfChecked(types, WorkItemType.PeerReview);
         addTypeIfChecked(types, WorkItemType.DecisionReview);
         addTypeIfChecked(types, WorkItemType.Goal);
         addTypeIfChecked(types, WorkItemType.AgileSprint);
         addTypeIfChecked(types, WorkItemType.AgileBacklog);
      }
      return types;
   }

   private void addTypeIfChecked(List<WorkItemType> types, WorkItemType workItemType) {
      XCheckBox widget = getWidget(workItemType.getDisplayName());
      if (widget != null && widget.isChecked()) {
         types.add(workItemType);
      }
   }

   public void clearAll() {
      for (WorkItemType type : WorkItemType.values()) {
         XCheckBox widget = getWidget(type);
         if (widget != null) {
            widget.set(false);
         }
      }
   }

   public void set(AtsSearchData data) {
      if (data.getWorkItemTypes() != null && !data.getWorkItemTypes().isEmpty()) {
         for (WorkItemType type : data.getWorkItemTypes()) {
            set(type, true);
         }
      }
   }

   public boolean isReviewSearch() {
      return reviewSearch;
   }

   public void setReviewSearch(boolean reviewSearch) {
      this.reviewSearch = reviewSearch;
   }

}
