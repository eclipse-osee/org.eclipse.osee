/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task.create;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportModDelArts {

   private Collection<ArtifactId> modified = new ArrayList<>();
   private Collection<ArtifactId> deleted = new ArrayList<>();

   public ChangeReportModDelArts() {
      // for jax-rs
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
