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
package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.ArtifactTxDataImpl;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactVisitor;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;

/**
 * Collect all the dirty internal OrcsData
 * 
 * @author Roberto E. Escobar
 */
public class CollectDirtyData implements ArtifactVisitor {

   private final List<ArtifactTransactionData> data;

   private ArtifactTransactionData txData;

   public CollectDirtyData(List<ArtifactTransactionData> data) {
      this.data = data;
   }

   @Override
   public void visit(Artifact artifact) {
      if (artifact.isDirty()) {
         txData = new ArtifactTxDataImpl(artifact.getOrcsData(), new ArrayList<AttributeData>());
         data.add(txData);
      }
   }

   @Override
   public void visit(Attribute<?> attribute) {
      if (attribute.isDirty()) {
         txData.getAttributeData().add(attribute.getOrcsData());
      }
   }

}