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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.ITransitionResult;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TransitionWorkItemResult {

   @JsonIgnore
   private IAtsWorkItem workItem;
   private ArtifactToken workItemId;
   private List<ITransitionResult> transitionResults = new ArrayList<ITransitionResult>();
   @JsonIgnore
   private AtsApi atsApi;

   public void addResult(ITransitionResult result) {
      transitionResults.add(result);
   }

   @JsonIgnore
   public void clear() {
      transitionResults.clear();
   }

   public boolean isEmpty() {
      return transitionResults.isEmpty();
   }

   public boolean contains(String string) {
      return toString().contains(string);
   }

   public boolean contains(TransitionResult transitionResult) {
      return transitionResults.contains(transitionResult);
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
      if (transitionResults.isEmpty()) {
         return "<Empty>";
      }
      StringBuffer sb = new StringBuffer();
      if (workItem != null) {
         sb.append(workItem.getArtifactTypeName());
         sb.append(" [");
         sb.append(workItem.getAtsId());
         sb.append("] Titled [");
         sb.append(workItem.getName());
         sb.append("]\n\n");
      } else if (workItemId != null) {
         sb.append(workItemId.getArtifactType().getName());
         sb.append(" [");
         sb.append(workItemId.getIdString());
         sb.append("] Titled [");
         sb.append(workItemId.getName());
         sb.append("]\n\n");
      }
      appendResultsString(sb, transitionResults);

      return sb.toString();
   }

   public void appendResultsString(StringBuffer sb, List<ITransitionResult> results) {
      for (ITransitionResult result : results) {
         sb.append("    - ");
         sb.append(result.getDetails());
         if (result.getException() != null) {
            if (Strings.isValid(result.getException())) {
               sb.append(" - Exception [");
               sb.append(result.getException());
               sb.append("] (see log for details)");
            } else {
               sb.append(" - (see log for details)");
            }
         }
         sb.append("\n");
      }

   }

   @Override
   public String toString() {
      return getResultString();
   }

   public List<ITransitionResult> getResults() {
      return transitionResults;
   }

   void setResults(List<ITransitionResult> transitionResults) {
      this.transitionResults = transitionResults;
   }

   @JsonIgnore
   public AtsApi getAtsApi() {
      return atsApi;
   }

   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public IAtsWorkItem getWorkItem() {
      if (workItem == null && workItemId.isValid()) {
         workItem = atsApi.getWorkItemService().getWorkItem(workItemId);
      }
      return workItem;
   }

   public void setWorkItem(IAtsWorkItem workItem) {
      this.workItem = workItem;
   }

   public ArtifactToken getWorkItemId() {
      if ((workItemId == null || workItemId.isInvalid()) && workItem != null) {
         workItemId = workItem.getArtifactToken();
      }
      return workItemId;
   }

   public void setWorkItemId(ArtifactToken workItemId) {
      this.workItemId = workItemId;
   }

}
