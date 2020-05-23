/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.loader.criteria;

import org.eclipse.osee.orcs.core.ds.Criteria;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaArtifact extends Criteria {

   private int id;

   public CriteriaArtifact() {
      super();
      this.id = -1;
   }

   public int getQueryId() {
      return id;
   }

   protected void setQueryId(int id) {
      this.id = id;
   }

   @Override
   public String toString() {
      return "CriteriaArtifactQid [queryId=" + id + "]";
   }
}
