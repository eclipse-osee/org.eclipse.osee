/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;

/**
 * @author Roberto E. Escobar
 */
public class LoadDataHandlerAdapter implements LoadDataHandler {

   private final OrcsDataHandler<ArtifactData> artifactHandler = new OrcsDataHandler<ArtifactData>() {

      @Override
      public void onData(ArtifactData data) throws OseeCoreException {
         LoadDataHandlerAdapter.this.onData(data);
      }
   };

   private final OrcsDataHandler<AttributeData> attributeHandler = new OrcsDataHandler<AttributeData>() {

      @Override
      public void onData(AttributeData data) throws OseeCoreException {
         LoadDataHandlerAdapter.this.onData(data);
      }
   };

   private final OrcsDataHandler<RelationData> relationHandler = new OrcsDataHandler<RelationData>() {

      @Override
      public void onData(RelationData data) throws OseeCoreException {
         LoadDataHandlerAdapter.this.onData(data);
      }
   };

   @Override
   public final OrcsDataHandler<ArtifactData> getArtifactDataHandler() {
      return artifactHandler;
   }

   @Override
   public final OrcsDataHandler<AttributeData> getAttributeDataHandler() {
      return attributeHandler;
   }

   @Override
   public final OrcsDataHandler<RelationData> getRelationDataHandler() {
      return relationHandler;
   }

   @SuppressWarnings("unused")
   @Override
   public void onLoadStart() throws OseeCoreException {
      //
   }

   @SuppressWarnings("unused")
   @Override
   public void onLoadDescription(LoadDescription data) throws OseeCoreException {
      //
   }

   @SuppressWarnings("unused")
   public void onData(ArtifactData data) throws OseeCoreException {
      //
   }

   @SuppressWarnings("unused")
   public void onData(AttributeData data) throws OseeCoreException {
      //
   }

   @SuppressWarnings("unused")
   public void onData(RelationData data) throws OseeCoreException {
      //
   }

   @Override
   @SuppressWarnings("unused")
   public void onData(AttributeData data, MatchLocation match) throws OseeCoreException {
      //
   }

   @Override
   public void onLoadEnd() {
      //
   }
}
