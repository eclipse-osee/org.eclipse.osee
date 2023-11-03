/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.testscript.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;

public class SetDiffToken {

   private final String name;
   private final Map<ArtifactId, SetDiffResult> results;
   private final int numSets;

   public SetDiffToken(String name, int numSets) {
      this.name = name;
      this.results = new HashMap<>();
      this.numSets = numSets;
   }

   public String getName() {
      return name;
   }

   public boolean isEqual() {
      if (getResults().isEmpty()) {
         return true;
      }
      if (getResults().size() != numSets) {
         return false;
      }
      return getResults().values().stream().allMatch(getResults().values().toArray()[0]::equals);
   }

   public Map<ArtifactId, SetDiffResult> getResults() {
      return results;
   }

   /**
    * Adds the result to the results map if it is new or more recent than the current result;
    *
    * @param setId
    * @param result
    */
   public void addResult(ArtifactId setId, ScriptResultToken result) {
      SetDiffResult diff = getResults().get(setId);
      if (diff == null || result.getExecutionDate().after(diff.getExecutionDate())) {
         getResults().put(setId, new SetDiffResult(result.getPassedCount(), result.getFailedCount(),
            result.getScriptAborted(), result.getExecutionDate()));
      }
   }

   private class SetDiffResult {
      private final int passes;
      private final int fails;
      private final boolean abort;
      private final Date executionDate;

      public SetDiffResult(int passes, int fails, boolean abort, Date executionDate) {
         this.passes = passes;
         this.fails = fails;
         this.abort = abort;
         this.executionDate = executionDate;
      }

      public int getPasses() {
         return passes;
      }

      public int getFails() {
         return fails;
      }

      public boolean isAbort() {
         return abort;
      }

      @JsonIgnore
      public Date getExecutionDate() {
         return executionDate;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof SetDiffResult) {
            SetDiffResult res = (SetDiffResult) obj;
            return getPasses() == res.getPasses() && getFails() == res.getFails() && isAbort() == res.isAbort();
         }
         return false;
      }

      @Override
      public int hashCode() {
         return super.hashCode();
      }

   }

}
