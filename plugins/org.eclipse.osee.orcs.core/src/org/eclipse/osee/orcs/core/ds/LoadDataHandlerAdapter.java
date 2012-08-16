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

/**
 * @author Roberto E. Escobar
 */
public abstract class LoadDataHandlerAdapter implements LoadDataHandler {

   private static final ArtifactDataHandler NOOP_ART_HANDLER = new ArtifactDataHandler() {

      @Override
      public void onData(ArtifactData data) {
         //
      }
   };

   private static final AttributeDataHandler NOOP_ATTR_HANDLER = new AttributeDataHandler() {

      @Override
      public void onData(AttributeData data) {
         //
      }
   };

   private static final RelationDataHandler NOOP_REL_HANDLER = new RelationDataHandler() {

      @Override
      public void onData(RelationData data) {
         //
      }
   };

   @Override
   public ArtifactDataHandler getArtifactDataHandler() {
      return NOOP_ART_HANDLER;
   }

   @Override
   public AttributeDataHandler getAttributeDataHandler() {
      return NOOP_ATTR_HANDLER;
   }

   @Override
   public RelationDataHandler getRelationDataHandler() {
      return NOOP_REL_HANDLER;
   }

   @Override
   public void onLoadStart() {
      //
   }

   @Override
   public void onLoadEnd() {
      //
   }

}
