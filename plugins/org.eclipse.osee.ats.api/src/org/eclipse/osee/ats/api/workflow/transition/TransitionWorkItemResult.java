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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TransitionWorkItemResult {

   private ArtifactToken workItemId;
   private String workItemType;
   private String atsId;
   private List<TransitionResult> transitionResults = new ArrayList<TransitionResult>();

   public void addResult(TransitionResult result) {
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
      if (Strings.isValid(workItemType)) {
         sb.append(workItemType);
      } else {
         sb.append("Work Item");
      }
      sb.append(" [");
      sb.append(atsId);
      sb.append("] Titled [");
      sb.append(workItemId.getName());
      sb.append("]\n\n");
      appendResultsString(sb, transitionResults);

      return sb.toString();
   }

   public void appendResultsString(StringBuffer sb, List<TransitionResult> results) {
      for (TransitionResult result : results) {
         sb.append("    - ");
         sb.append(result.getDetails());
         if (result.getException() != null) {
            if (Strings.isValid(result.getException())) {
               sb.append(" - Exception [");
               sb.append(result.getException());
               sb.append("] (see log for details)");
            }
         }
         sb.append("\n");
      }

   }

   @Override
   public String toString() {
      return getResultString();
   }

   public List<TransitionResult> getResults() {
      return transitionResults;
   }

   void setResults(List<TransitionResult> transitionResults) {
      this.transitionResults = transitionResults;
   }

   public void setWorkItem(IAtsWorkItem workItem) {
      this.workItemType = workItem.getArtifactTypeName();
      this.atsId = workItem.getAtsId();
      this.workItemId = ArtifactToken.valueOf(workItem.getId(), workItem.getName());
   }

   public ArtifactToken getWorkItemId() {
      return workItemId;
   }

   public void setWorkItemId(ArtifactToken workItemId) {
      this.workItemId = workItemId;
   }

   public String getWorkItemType() {
      return workItemType;
   }

   public void setWorkItemType(String workItemType) {
      this.workItemType = workItemType;
   }

   public String getAtsId() {
      return atsId;
   }

   public void setAtsId(String atsId) {
      this.atsId = atsId;
   }

}
