/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

   public void setQueryId(int id) {
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
