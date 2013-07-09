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
package org.eclipse.osee.orcs.core.internal.transaction.handler;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.ArtifactTxDataImpl;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactVisitor;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;

/**
 * Takes a snapshot of all the dirty internal OrcsData
 * 
 * @author Roberto E. Escobar
 */
public class CollectAndCopyDirtyData implements ArtifactVisitor {
   private final DataFactory dataFactory;
   private final List<ArtifactTransactionData> data;

   private ArtifactTransactionData txData;

   public CollectAndCopyDirtyData(DataFactory dataFactory, List<ArtifactTransactionData> data) {
      this.dataFactory = dataFactory;
      this.data = data;
   }

   @SuppressWarnings("unused")
   @Override
   public void visit(Artifact artifact) throws OseeCoreException {
      if (artifact.isDirty()) {
         ArtifactData copy = dataFactory.clone(artifact.getOrcsData());
         txData = new ArtifactTxDataImpl(copy, new ArrayList<AttributeData>());
         data.add(txData);
      }
   }

   @Override
   public void visit(Attribute<?> attribute) throws OseeCoreException {
      if (attribute.isDirty()) {
         AttributeData copy = dataFactory.clone(attribute.getOrcsData());
         txData.getAttributeData().add(copy);
      }
   }
}