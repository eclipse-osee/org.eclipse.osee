/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.orcs.core.ds.Criteria;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaArtifactIds extends Criteria {

   private final Collection<? extends ArtifactId> ids;

   private final ArtifactId id;

   public CriteriaArtifactIds(Collection<? extends ArtifactId> ids) {
      if (ids.size() == 1) {
         id = ids.iterator().next();
         this.ids = null;
      } else {
         this.ids = ids;
         id = null;
      }
   }

   public CriteriaArtifactIds(ArtifactId id) {
      this.id = id;
      ids = null;
   }

   public boolean hasMultipleIds() {
      return id == null;
   }

   public ArtifactId getId() {
      return id;
   }

   public Collection<? extends ArtifactId> getIds() {
      return ids;
   }

   @Override
   public String toString() {
      return "CriteriaArtifactIds [ids=" + ids + "]";
   }
}