/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.api.workflow.transition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TransitionResults {

   boolean cancelled;
   private Set<ArtifactToken> workItemIds = new HashSet<>();
   private List<TransitionResult> results = new ArrayList<>();
   private List<TransitionWorkItemResult> transitionWorkItems = new ArrayList<>();
   @JsonIgnore
   private AtsApi atsApi;

   public void addResult(IAtsWorkItem workItem, TransitionResult result) {
      TransitionWorkItemResult workItemResult = getTransitionWorkItemResult(workItem);
      if (workItemResult == null) {
         workItemResult = new TransitionWorkItemResult();
         workItemResult.setWorkItem(workItem);
         transitionWorkItems.add(workItemResult);
      }
      workItemResult.addResult(result);
      workItemIds.add(workItem.getStoreObject());
   }

   private TransitionWorkItemResult getTransitionWorkItemResult(IAtsWorkItem workItem) {
      for (TransitionWorkItemResult workItemResult : transitionWorkItems) {
         if (workItem.getId().equals(workItemResult.getWorkItemId().getId())) {
            return workItemResult;
         }
      }
      return null;
   }

   @JsonIgnore
   public void clear() {
      results.clear();
      transitionWorkItems.clear();
   }

   public void addResult(TransitionResult result) {
      results.add(result);
   }

   public boolean isEmpty() {
      return results.isEmpty() && transitionWorkItems.isEmpty();
   }

   public boolean isCancelled() {
      return cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public boolean contains(String string) {
      return toString().contains(string);
   }

   public boolean contains(TransitionResult transitionResult) {
      return results.contains(transitionResult);
   }

   public boolean isEmpty(IAtsWorkItem workItem) {
      TransitionWorkItemResult transitionWorkItemResult = getTransitionWorkItemResult(workItem);
      return transitionWorkItemResult == null || transitionWorkItemResult.getResults().isEmpty();
   }

   public boolean contains(IAtsWorkItem workItem, TransitionResult transitionResult) {
      TransitionWorkItemResult transitionWorkItemResult = getTransitionWorkItemResult(workItem);
      if (transitionWorkItemResult != null) {
         return transitionWorkItemResult.getResults().contains(transitionResult);
      }
      return false;
   }

   @JsonIgnore
   public boolean isErrors() {
      return !isEmpty();
   }

   @JsonIgnore
   public boolean isSuccess() {
      return isEmpty();
   }

   @JsonIgnore
   public String getResultString() {
      if (results.isEmpty() && transitionWorkItems.isEmpty()) {
         return "<Empty>";
      }
      StringBuffer sb = new StringBuffer();
      sb.append("Reason(s):\n");
      appendResultsString(sb, results);
      for (TransitionWorkItemResult workItem : transitionWorkItems) {
         sb.append(workItem.getResultString());
      }
      return sb.toString();
   }

   public void appendResultsString(StringBuffer sb, List<TransitionResult> results) {
      for (TransitionResult result : results) {
         sb.append("    - ");
         sb.append(result.getDetails());
         if (Strings.isValid(result.getException())) {
            if (Strings.isValid(result.getException())) {
               sb.append(" - Exception [");
               sb.append(result.getException());
               sb.append("] (see log for details)");
            }
         }
         sb.append("\n");
      }

   }

   @JsonIgnore
   public XResultData getResultXResultData() {
      XResultData resultData = new XResultData(false);
      resultData.log("Transition Failed");
      String str = getResultString();
      resultData.addRaw(str);
      return resultData;
   }

   @Override
   public String toString() {
      return getResultString();
   }

   public List<TransitionResult> getResults() {
      return results;
   }

   @JsonIgnore
   public AtsApi getAtsApi() {
      return atsApi;
   }

   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public List<TransitionWorkItemResult> getTransitionWorkItems() {
      return transitionWorkItems;
   }

   public void setTransitionWorkItems(List<TransitionWorkItemResult> transitionWorkItems) {
      this.transitionWorkItems = transitionWorkItems;
      if (!transitionWorkItems.isEmpty()) {
         this.workItemIds.clear();
         for (TransitionWorkItemResult transitionWorkItemResult : transitionWorkItems) {
            this.workItemIds.add(transitionWorkItemResult.getWorkItemId());
         }
      }
   }

   public void setResults(List<TransitionResult> results) {
      this.results = results;
   }

   @JsonIgnore
   public Collection<IAtsWorkItem> getWorkItems() {
      List<IAtsWorkItem> workItems = new ArrayList<>();

      return workItems;
   }

   public Set<ArtifactToken> getWorkItemIds() {
      return workItemIds;
   }

   public void setWorkItemIds(Set<ArtifactToken> workItemIds) {
      this.workItemIds = workItemIds;
   }

}
