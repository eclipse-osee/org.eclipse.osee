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
   private final CriteriaRelation2 relationCriteria2;

   public CriteriaOrcsLoad(CriteriaArtifact artifactCriteria, CriteriaAttribute attributeCriteria, CriteriaRelation relationCriteria, CriteriaRelation2 relationCriteria2) {
      super();
      this.artifactCriteria = artifactCriteria;
      this.attributeCriteria = attributeCriteria;
      this.relationCriteria = relationCriteria;
      this.relationCriteria2 = relationCriteria2;
   }

   public void setQueryId(Long id) {
      artifactCriteria.setQueryId(id);
      attributeCriteria.setQueryId(id);
      relationCriteria.setQueryId(id);
      relationCriteria2.setQueryId(id);
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

   public Criteria getRelationCriteria2() {
      return relationCriteria2;
   }

   @Override
   public String toString() {
      return "CriteriaOrcsLoad [artifactCriteria=" + artifactCriteria + ", attributeCriteria=" + attributeCriteria + ", relationCriteria=" + relationCriteria + ", relationCriteria2=" + relationCriteria2 + "]";
   }
}
