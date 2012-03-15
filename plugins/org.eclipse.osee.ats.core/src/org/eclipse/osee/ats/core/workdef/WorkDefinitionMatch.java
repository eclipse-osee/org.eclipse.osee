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
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
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
