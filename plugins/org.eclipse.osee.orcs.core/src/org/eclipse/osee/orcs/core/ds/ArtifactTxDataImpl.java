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
package org.eclipse.osee.orcs.core.ds;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AbstractIdentity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author John Misinco
 */
public class ArtifactTxDataImpl extends AbstractIdentity<String> implements ArtifactTransactionData {

   private final ArtifactData artifactData;
   private final List<AttributeData> attributeData;
   private final List<RelationData> relationData = new LinkedList<RelationData>();

   public ArtifactTxDataImpl(ArtifactData artifactData, List<AttributeData> attributeData) {
      super();
      this.artifactData = artifactData;
      this.attributeData = attributeData;
   }

   @Override
   public String getGuid() {
      return getArtifactData().getGuid();
   }

   @Override
   public ArtifactData getArtifactData() {
      return artifactData;
   }

   @Override
   public List<AttributeData> getAttributeData() {
      return attributeData;
   }

   @Override
   public List<RelationData> getRelationData() {
      return relationData;
   }

   @Override
   public void accept(OrcsVisitor visitor) throws OseeCoreException {
      visitor.visit(getArtifactData());
      for (AttributeData attributeData : getAttributeData()) {
         visitor.visit(attributeData);
      }
      for (RelationData relationData : getRelationData()) {
         visitor.visit(relationData);
      }
   }

}