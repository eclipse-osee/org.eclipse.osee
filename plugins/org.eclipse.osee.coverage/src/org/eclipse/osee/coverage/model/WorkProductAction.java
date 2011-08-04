/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.model;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.coverage.internal.ServiceProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class WorkProductAction {

   Set<WorkProductTask> tasks = new HashSet<WorkProductTask>();
   private final Artifact artifact;

   public WorkProductAction(Artifact artifact) {
      this.artifact = artifact;
   }

   public String getGuid() {
      return artifact.getGuid();
   }

   public String getName() {
      return artifact.getName();
   }

   public boolean isCompleted() {
      return ServiceProvider.getOseeCmService().isCompleted(artifact);
   }

   public Set<WorkProductTask> getTasks() {
      return tasks;
   }

   @Override
   public String toString() {
      return getName() + " - " + (isCompleted() ? "[Completed]" : "[InWork]");
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((artifact == null) ? 0 : artifact.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      WorkProductAction other = (WorkProductAction) obj;
      if (artifact == null) {
         if (other.artifact != null) {
            return false;
         }
      } else if (!artifact.equals(other.artifact)) {
         return false;
      }
      return true;
   }

}
