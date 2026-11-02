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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.agile.AgileUtil;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workdef.XWidgetBuilderAts;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXCheckBoxWidget;

/**
 * @author Donald G. Dunne
 */
public class WorkItemTypeSearchWidget {

   private final WorldEditorParameterSearchItem searchItem;
   private boolean reviewSearch;

   public WorkItemTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      this.searchItem = searchItem;
   }

   public void addWidget(XWidgetBuilderAts wba, int beginComposite) {
      if (reviewSearch) {
         wba.andCheckBoxWidget("Peer Review").beginComp(6);
         wba.andCheckBoxWidget("Decision Review");
      } else {
         // New Action Searches should start with a valid, useful baseline. Saved-search loading clears all controls
         // before restoring its explicit types, so this builder default does not override saved selections.
         wba.andCheckBoxWidget(WorkItemType.TeamWorkflow.getDisplayName()).andDefault(true).beginComp(beginComposite);
         wba.andCheckBoxWidget("Task");
         wba.andCheckBoxWidget("Peer Review");
         wba.andCheckBoxWidget("Decision Review");
         if (AgileUtil.isAgileUser(AtsApiService.get())) {
            wba.andCheckBoxWidget("Goal");
            wba.andCheckBoxWidget("Agile Sprint");
            wba.andCheckBoxWidget("Agile Backlog");
         } else {
            wba.andCheckBoxWidget("Goal");
         }
      }
      wba.endComp();
   }

   public XXCheckBoxWidget getWidget(String workItemType) {
      return (XXCheckBoxWidget) searchItem.getxWidgets().get(workItemType);
   }

   public XXCheckBoxWidget getWidget(WorkItemType type) {
      return getWidget(type.getDisplayName());
   }

   public void set(WorkItemType type, boolean selected) {
      set(type.getDisplayName(), selected);
   }

   public void set(String workItemType, boolean selected) {
      XXCheckBoxWidget checkbox = getWidget(workItemType);
      if (checkbox != null) {
         checkbox.setSelected(selected);
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
      XXCheckBoxWidget widget = getWidget(workItemType.getDisplayName());
      if (widget != null && widget.isChecked()) {
         types.add(workItemType);
      }
   }

   public void clearAll() {
      for (WorkItemType type : WorkItemType.values()) {
         XXCheckBoxWidget widget = getWidget(type);
         if (widget != null) {
            widget.setSelected(false);
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
