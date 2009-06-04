/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.data.model.editor.model;

/**
 * @author Roberto E. Escobar
 */
public class RelationLinkModel extends ConnectionModel<ArtifactDataType> {

   private RelationDataType relation;

   public RelationLinkModel() {
      super();
      relation = null;
   }

   public RelationLinkModel(RelationDataType relation, ArtifactDataType aSide, ArtifactDataType bSide) {
      super(aSide, bSide);
      this.relation = relation;
   }

   public void setASide(ArtifactDataType aSide) {
      setSource(aSide);
   }

   public void setBSide(ArtifactDataType bSide) {
      setTarget(bSide);
   }

   public ArtifactDataType getASide() {
      return getSource();
   }

   public ArtifactDataType getBSide() {
      return getTarget();
   }

   public void setRelation(RelationDataType relation) {
      this.relation = relation;
   }

   public RelationDataType getRelation() {
      return relation;
   }
}
