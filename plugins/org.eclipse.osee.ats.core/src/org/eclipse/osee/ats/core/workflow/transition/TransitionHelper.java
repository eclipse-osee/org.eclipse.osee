/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow.transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class TransitionHelper extends TransitionHelperAdapter {

   private final String cancellationReason;
   private final Collection<IAtsWorkItem> workItems;
   private final String name;
   private TransitionOption[] transitionOption;
   private final Collection<? extends IAtsUser> toAssignees;
   private String toStateName;
   private final IAtsChangeSet changes;
   private boolean executeChanges = false;
   private final IAtsWorkItemService workItemService;
   private final AtsApi atsApi;

   public TransitionHelper(String name, Collection<IAtsWorkItem> workItems, String toStateName, Collection<? extends IAtsUser> toAssignees, String cancellationReason, IAtsChangeSet changes, AtsApi atsApi, TransitionOption... transitionOption) {
      super(atsApi);
      this.atsApi = atsApi;
      this.workItemService = atsApi.getWorkItemService();
      this.name = name;
      this.workItems = workItems;
      this.toStateName = toStateName;
      this.toAssignees = toAssignees;
      this.cancellationReason = cancellationReason;
      this.changes = changes;
      this.transitionOption = transitionOption;
   }

   @Override
   public boolean isOverrideAssigneeCheck() {
      return Arrays.asList(transitionOption).contains(TransitionOption.OverrideAssigneeCheck);
   }

   @Override
   public boolean isOverrideWorkingBranchCheck() {
      return Arrays.asList(transitionOption).contains(TransitionOption.OverrideWorkingBranchCheck);
   }

   @Override
   public boolean isReload() {
      return !Arrays.asList(transitionOption).contains(TransitionOption.OverrideReload);
   }

   @Override
   public boolean isOverrideTransitionValidityCheck() {
      return Arrays.asList(transitionOption).contains(TransitionOption.OverrideTransitionValidityCheck);
   }

   @Override
   public Collection<IAtsWorkItem> getWorkItems() {
      return workItems;
   }

   @Override
   public Result getCompleteOrCancellationReason() {
      if (Strings.isValid(cancellationReason)) {
         return new Result(true, cancellationReason);
      }
      return Result.FalseResult;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public Collection<? extends IAtsUser> getToAssignees(IAtsWorkItem workItem) {
      return toAssignees;
   }

   @Override
   public Result handleExtraHoursSpent(IAtsChangeSet changes) {
      return Result.TrueResult;
   }

   @Override
   public String getToStateName() {
      return toStateName;
   }

   public void addTransitionOption(TransitionOption transitionOption) {
      List<TransitionOption> options = new ArrayList<>(Arrays.asList(this.transitionOption));
      if (!options.contains(transitionOption)) {
         options.add(transitionOption);
      }
      this.transitionOption = options.toArray(new TransitionOption[options.size()]);
   }

   public void removeTransitionOption(TransitionOption transitionOption) {
      List<TransitionOption> options = new ArrayList<>(Arrays.asList(this.transitionOption));
      if (options.contains(transitionOption)) {
         options.remove(transitionOption);
      }
      this.transitionOption = options.toArray(new TransitionOption[options.size()]);
   }

   public void setToStateName(String toStateName) {
      this.toStateName = toStateName;
   }

   @Override
   public IAtsChangeSet getChangeSet() {
      return changes;
   }

   @Override
   public boolean isExecuteChanges() {
      return executeChanges;
   }

   public void setExecuteChanges(boolean executeChanges) {
      this.executeChanges = executeChanges;
   }

   @Override
   public Collection<ITransitionListener> getTransitionListeners() {
      try {
         return workItemService.getTransitionListeners();
      } catch (OseeCoreException ex) {
         OseeLog.log(TransitionHelper.class, Level.SEVERE, ex);
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public AtsApi getServices() {
      return atsApi;
   }

   public TransitionOption[] getTransitionOptions() {
      return transitionOption;
   }

}
