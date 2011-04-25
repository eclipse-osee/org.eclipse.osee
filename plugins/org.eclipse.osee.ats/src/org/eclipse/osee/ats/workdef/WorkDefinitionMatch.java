/*
 * Created on Jan 10, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class WorkDefinitionMatch {

   private WorkDefinition workDefinition;
   private final List<String> trace = new ArrayList<String>();

   public WorkDefinitionMatch() {
      this(null, null);
   }

   public WorkDefinitionMatch(WorkDefinition WorkDefinition, String trace) {
      this.workDefinition = WorkDefinition;
      if (Strings.isValid(trace)) {
         this.trace.add(trace);
      }
   }

   public WorkDefinition getWorkDefinition() {
      return workDefinition;
   }

   public void setWorkDefinition(WorkDefinition WorkDefinition) {
      this.workDefinition = WorkDefinition;
   }

   public void addTrace(String traceStr) {
      if (trace.size() == 0 || (trace.size() >= 1 && !trace.get(trace.size() - 1).equals(traceStr))) {
         trace.add(traceStr);
      }
   }

   public List<String> getTrace() {
      return trace;
   }

   public boolean isMatched() {
      return workDefinition != null;
   }

   @Override
   public String toString() {
      return workDefinition.getName();
   }
}
