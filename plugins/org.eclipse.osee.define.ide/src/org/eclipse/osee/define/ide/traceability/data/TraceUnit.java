/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.traceability.data;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class TraceUnit {
   private final ArtifactTypeToken traceUnitType;
   private final String name;
   private final Collection<TraceMark> traceMarks;
   private URI uriPath;

   public TraceUnit(ArtifactTypeToken traceUnitType, String name) {
      this.name = name;
      this.traceUnitType = traceUnitType;
      this.traceMarks = new HashSet<>();
   }

   public URI getUriPath() {
      return uriPath;
   }

   public void setUriPath(URI uriPath) {
      this.uriPath = uriPath;
   }

   public ArtifactTypeToken getTraceUnitType() {
      return traceUnitType;
   }

   public String getName() {
      return name;
   }

   public void addAllTraceMarks(Collection<TraceMark> traceItems) {
      if (Conditions.hasValues(traceItems)) {
         traceMarks.addAll(traceItems);
      }
   }

   public void addTraceMark(TraceMark traceMark) {
      traceMarks.add(traceMark);
   }

   public Collection<TraceMark> getTraceMarks() {
      return traceMarks;
   }

   public Set<String> getTraceMarkTypes() {
      Set<String> toReturn = new HashSet<>();
      for (TraceMark traceMark : traceMarks) {
         toReturn.add(traceMark.getTraceType());
      }
      return toReturn;
   }

   public Collection<TraceMark> getTraceMarksByType(String type) {
      Set<TraceMark> toReturn = new HashSet<>();
      for (TraceMark traceMark : traceMarks) {
         if (traceMark.getTraceType().equalsIgnoreCase(type)) {
            toReturn.add(traceMark);
         }
      }
      return toReturn;
   }
}