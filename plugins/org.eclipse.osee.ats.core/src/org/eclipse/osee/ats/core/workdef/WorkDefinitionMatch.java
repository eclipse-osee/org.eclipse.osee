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
package org.eclipse.osee.ats.core.workdef;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.workdef.api.IAtsWorkDefinition;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionMatch {

   private IAtsWorkDefinition workDefinition;
   private String workDefinitionId;
   private final List<String> trace = new ArrayList<String>();

   public WorkDefinitionMatch() {
      this(null, null);
   }

   public WorkDefinitionMatch(String workDefinitionId, String trace) {
      this.workDefinitionId = workDefinitionId;
      if (Strings.isValid(trace)) {
         this.trace.add(trace);
      }
   }

   public String getWorkDefinitionId() {
      return workDefinitionId;
   }

   public void setWorkDefinitionId(String workDefinitionId) {
      this.workDefinitionId = workDefinitionId;
   }

   public IAtsWorkDefinition getWorkDefinition() {
      return workDefinition;
   }

   public void setWorkDefinition(IAtsWorkDefinition WorkDefinition) {
      this.workDefinition = WorkDefinition;
   }

   public void addTrace(String traceStr) {
      if (trace.isEmpty() || (!trace.get(trace.size() - 1).equals(traceStr))) {
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
