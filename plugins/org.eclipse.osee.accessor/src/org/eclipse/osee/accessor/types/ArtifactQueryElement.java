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

import java.util.LinkedList;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

public class ArtifactQueryElement {

   private AttributeQueryElement attributeQuery = new AttributeQueryElement();
   private LinkedList<RelationTypeSide> relationPath = new LinkedList<>();

   public ArtifactQueryElement() {
   }

   /**
    * @return the attributeQuery
    */
   public AttributeQueryElement getAttributeQuery() {
      return attributeQuery;
   }

   /**
    * @param attributeQuery the attributeQuery to set
    */
   public void setAttributeQuery(AttributeQueryElement attributeQuery) {
      this.attributeQuery = attributeQuery;
   }

   /**
    * @return the relationPath
    */
   public LinkedList<RelationTypeSide> getRelationPath() {
      return relationPath;
   }

   /**
    * @param relationPath the relationPath to set
    */
   public void setRelationPath(LinkedList<RelationTypeSide> relationPath) {
      this.relationPath = relationPath == null ? new LinkedList<>() : relationPath;
   }

   public boolean isDirectAttributeQuery() {
      return relationPath == null || relationPath.isEmpty();
   }
}
