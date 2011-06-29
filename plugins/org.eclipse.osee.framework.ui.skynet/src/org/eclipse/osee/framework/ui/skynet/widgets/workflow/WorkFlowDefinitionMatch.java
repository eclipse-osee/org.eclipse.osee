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

   public void addTrace(String traceStr) {
      if (trace.size() >= 1 && !trace.get(trace.size() - 1).equals(traceStr)) {
         trace.add(traceStr);
      }
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
