/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.api.task.create;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportModDelArts {

   private Collection<ArtifactId> added = new ArrayList<>();
   private Collection<ArtifactId> modified = new ArrayList<>();
   private Collection<ArtifactId> deleted = new ArrayList<>();

   public ChangeReportModDelArts() {
      // for jax-rs
   }

   public Collection<ArtifactId> getAdded() {
      return added;
   }

   public void setAdded(Collection<ArtifactId> added) {
      this.added = added;
   }

   public Collection<ArtifactId> getModified() {
      return modified;
   }

   public void setModified(Collection<ArtifactId> modified) {
      this.modified = modified;
   }

   public Collection<ArtifactId> getDeleted() {
      return deleted;
   }

   public void setDeleted(Collection<ArtifactId> deleted) {
      this.deleted = deleted;
   }
}
