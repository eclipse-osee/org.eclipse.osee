/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import org.eclipse.osee.framework.core.data.ArtifactTypeId;

public class AttributeQuery {

   private ArtifactTypeId type = ArtifactTypeId.SENTINEL;
   private RelatedArtifact related = RelatedArtifact.SENTINEL;
   private Collection<AttributeQueryElement> queries = new LinkedList<AttributeQueryElement>();
   public AttributeQuery() {
   }

   /**
    * @return the queries
    */
   public Collection<AttributeQueryElement> getQueries() {
      return queries;
   }

   /**
    * @param queries the queries to set
    */
   public void setQueries(Collection<AttributeQueryElement> queries) {
      this.queries = queries;
   }

   /**
    * @return the type
    */
   public ArtifactTypeId getType() {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(ArtifactTypeId type) {
      this.type = type;
   }

   /**
    * @return the related
    */
   public RelatedArtifact getRelated() {
      return related;
   }

   /**
    * @param related the related to set
    */
   public void setRelated(RelatedArtifact related) {
      this.related = related;
   }

}
