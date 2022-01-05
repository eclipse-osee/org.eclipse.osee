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
public class CriteriaOrcsLoad extends Criteria {

   private final CriteriaArtifact artifactCriteria;
   private final CriteriaAttribute attributeCriteria;
   private final CriteriaRelation relationCriteria;

   public CriteriaOrcsLoad(CriteriaArtifact artifactCriteria, CriteriaAttribute attributeCriteria, CriteriaRelation relationCriteria) {
      super();
      this.artifactCriteria = artifactCriteria;
      this.attributeCriteria = attributeCriteria;
      this.relationCriteria = relationCriteria;
   }

   public void setQueryId(Long id) {
      artifactCriteria.setQueryId(id);
      attributeCriteria.setQueryId(id);
      relationCriteria.setQueryId(id);
   }

   public Criteria getArtifactCriteria() {
      return artifactCriteria;
   }

   public Criteria getAttributeCriteria() {
      return attributeCriteria;
   }

   public Criteria getRelationCriteria() {
      return relationCriteria;
   }

   @Override
   public String toString() {
      return "CriteriaOrcsLoad [artifactCriteria=" + artifactCriteria + ", attributeCriteria=" + attributeCriteria + ", relationCriteria=" + relationCriteria + "]";
   }
}
