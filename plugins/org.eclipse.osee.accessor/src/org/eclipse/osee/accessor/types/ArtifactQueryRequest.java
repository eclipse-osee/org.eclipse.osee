/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.accessor.types;

import java.util.Collection;
import java.util.LinkedList;

public class ArtifactQueryRequest {

   private Collection<ArtifactQueryElement> queries = new LinkedList<>();

   public ArtifactQueryRequest() {
   }

   /**
    * @return the queries
    */
   public Collection<ArtifactQueryElement> getQueries() {
      return queries;
   }

   /**
    * @param queries the queries to set
    */
   public void setQueries(Collection<ArtifactQueryElement> queries) {
      this.queries = queries;
   }
}
