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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactVisitor;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.core.internal.proxy.ProxyFactory;
import org.eclipse.osee.orcs.data.ArtifactWriteable;

/**
 * Update the internal artifact OrcsData
 * 
 * @author Roberto E. Escobar
 */
public class UpdateBackingData implements OrcsVisitor {

   private final ProxyFactory<ArtifactImpl, ?, ?> factory;
   private final Map<String, ArtifactWriteable> writeableArtifacts;

   private UpdateHelper originalArtData;
   private UpdateHelper modifiedArtData;

   public UpdateBackingData(ProxyFactory<ArtifactImpl, ?, ?> factory, Map<String, ArtifactWriteable> writeableArtifacts) {
      super();
      this.factory = factory;
      this.writeableArtifacts = writeableArtifacts;
   }

   @Override
   public void visit(ArtifactData newData) throws OseeCoreException {
      ArtifactWriteable writeable = writeableArtifacts.get(newData.getGuid());
      ArtifactImpl original = factory.getOriginalObject(writeable);
      ArtifactImpl modified = factory.getProxiedObject(writeable);
      if (original != null && modified != null) {
         originalArtData = setArtifact(newData, original);
         modifiedArtData = setArtifact(newData, modified);
      } else {
         throw new OseeArgumentException("Invalid object [%s]", writeable);
      }
   }

   private UpdateHelper setArtifact(ArtifactData newData, ArtifactImpl destination) throws OseeCoreException {
      UpdateHelper visitor = new UpdateHelper();
      destination.accept(visitor);
      destination.setOrcsData(newData);
      return visitor;
   }

   @Override
   public void visit(AttributeData newData) throws OseeCoreException {
      originalArtData.setAttribute(newData);
      modifiedArtData.setAttribute(newData);
   }

   @Override
   public void visit(RelationData data) throws OseeCoreException {
      // TX_TODO
   }

   private static final class UpdateHelper implements ArtifactVisitor {
      private final Map<Integer, Attribute<?>> attrById = new HashMap<Integer, Attribute<?>>();

      @Override
      public void visit(ArtifactImpl artifact) {
         //
      }

      @Override
      public void visit(Attribute<?> attribute) {
         attrById.put(attribute.getId(), attribute);
      }

      public void setAttribute(AttributeData newAttributeData) throws OseeCoreException {
         Attribute<?> toUpdate = attrById.get(newAttributeData.getLocalId());
         toUpdate.setOrcsData(newAttributeData);
      }
   }
}
