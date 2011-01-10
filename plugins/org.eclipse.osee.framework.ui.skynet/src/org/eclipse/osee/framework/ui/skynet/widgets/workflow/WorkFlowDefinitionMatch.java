/*
 * Created on Jan 10, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class WorkFlowDefinitionMatch {

   private WorkFlowDefinition workFlowDefinition;
   private final List<String> trace = new ArrayList<String>();

   public WorkFlowDefinitionMatch() {
      this(null, null);
   }

   public WorkFlowDefinitionMatch(WorkFlowDefinition workFlowDefinition, String trace) {
      this.workFlowDefinition = workFlowDefinition;
      if (Strings.isValid(trace)) {
         this.trace.add(trace);
      }
   }

   public WorkFlowDefinition getWorkFlowDefinition() {
      return workFlowDefinition;
   }

   public void setWorkFlowDefinition(WorkFlowDefinition workFlowDefinition) {
      this.workFlowDefinition = workFlowDefinition;
   }

   public List<String> getTrace() {
      return trace;
   }

   public boolean isMatched() {
      return workFlowDefinition != null;
   }

   @Override
   public String toString() {
      return String.format("[%s] from [%s]", workFlowDefinition.getName(), trace);
   }
}
