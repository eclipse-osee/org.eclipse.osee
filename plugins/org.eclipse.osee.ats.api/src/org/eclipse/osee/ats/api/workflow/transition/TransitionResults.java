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
package org.eclipse.osee.ats.api.workflow.transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.ITransitionResult;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TransitionResults {

   boolean cancelled;

   private final List<ITransitionResult> results = new ArrayList<>();

   private final Map<IAtsWorkItem, List<ITransitionResult>> workItemToResults =
      new HashMap<IAtsWorkItem, List<ITransitionResult>>();

   public void addResult(IAtsWorkItem workItem, ITransitionResult result) {
      List<ITransitionResult> results = workItemToResults.get(workItem);
      if (results == null) {
         results = new ArrayList<>();
         workItemToResults.put(workItem, results);
      }
      results.add(result);
   }

   public void clear() {
      results.clear();
      workItemToResults.clear();
   }

   public void addResult(ITransitionResult result) {
      results.add(result);
   }

   public boolean isEmpty() {
      return results.isEmpty() && workItemToResults.isEmpty();
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
      List<ITransitionResult> workItemResults = workItemToResults.get(workItem);
      if (workItemResults == null || workItemResults.isEmpty()) {
         return true;
      }
      return false;
   }

   public boolean contains(IAtsWorkItem workItem, TransitionResult transitionResult) {
      List<ITransitionResult> workItemResults = workItemToResults.get(workItem);
      if (workItemResults == null) {
         return false;
      }
      return workItemResults.contains(transitionResult);
   }

   public String getResultString() {
      if (results.isEmpty() && workItemToResults.isEmpty()) {
         return "<Empty>";
      }
      StringBuffer sb = new StringBuffer();
      sb.append("Reason(s):\n");
      appendResultsString(sb, results);
      for (IAtsWorkItem workItem : workItemToResults.keySet()) {
         sb.append("\n");
         sb.append(workItem.getArtifactTypeName());
         sb.append(" [");
         sb.append(workItem.getAtsId());
         sb.append("] Titled [");
         sb.append(workItem.getName());
         sb.append("]\n\n");
         appendResultsString(sb, workItemToResults.get(workItem));
      }

      return sb.toString();
   }

   public void appendResultsString(StringBuffer sb, List<ITransitionResult> results) {
      for (ITransitionResult result : results) {
         sb.append("    - ");
         sb.append(result.getDetails());
         if (result.getException() != null) {
            if (Strings.isValid(result.getException().getLocalizedMessage())) {
               sb.append(" - Exception [");
               sb.append(result.getException().getLocalizedMessage());
               sb.append("] (see log for details)");
            } else {
               sb.append(" - (see log for details)");
            }
         }
         sb.append("\n");
      }

   }

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

   public List<ITransitionResult> getResults() {
      return results;
   }

   public Map<IAtsWorkItem, List<ITransitionResult>> getWorkItemToResults() {
      return workItemToResults;
   }

}
