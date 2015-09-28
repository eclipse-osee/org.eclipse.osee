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
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IWorkDefinitionMatch;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionMatch implements IWorkDefinitionMatch {

   private IAtsWorkDefinition workDefinition;
   private String workDefinitionId;
   private final List<String> trace = new ArrayList<>();

   public WorkDefinitionMatch() {
      this(null, null);
   }

   public WorkDefinitionMatch(String workDefinitionId, String trace) {
      this.workDefinitionId = workDefinitionId;
      if (Strings.isValid(trace)) {
         this.trace.add(trace);
      }
   }

   @Override
   public String getWorkDefinitionId() {
      return workDefinitionId;
   }

   @Override
   public void setWorkDefinitionId(String workDefinitionId) {
      this.workDefinitionId = workDefinitionId;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition() {
      return workDefinition;
   }

   @Override
   public void setWorkDefinition(IAtsWorkDefinition WorkDefinition) {
      this.workDefinition = WorkDefinition;
   }

   @Override
   public void addTrace(String traceStr) {
      if (trace.isEmpty() || (!trace.get(trace.size() - 1).equals(traceStr))) {
         trace.add(traceStr);
      }
   }

   @Override
   public List<String> getTrace() {
      return trace;
   }

   @Override
   public boolean isMatched() {
      return workDefinition != null;
   }

   @Override
   public String toString() {
      if (workDefinition != null) {
         return workDefinition.getName();
      } else {
         return trace.toString();
      }
   }
}
