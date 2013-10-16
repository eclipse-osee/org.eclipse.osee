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

import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class LoadDataHandlerDecorator extends LoadDataHandlerAdapter {

   private final LoadDataHandler handler;

   public LoadDataHandlerDecorator(LoadDataHandler handler) {
      this.handler = handler;
   }

   public boolean hasValidHandler() {
      return handler != null;
   }

   protected LoadDataHandler getHandler() {
      return handler;
   }

   @Override
   public void onLoadStart() throws OseeCoreException {
      if (handler != null) {
         handler.onLoadStart();
      }
   }

   @Override
   public void onData(AttributeData data, MatchLocation match) throws OseeCoreException {
      if (handler != null) {
         handler.onData(data, match);
      }
   }

   @Override
   public void onLoadDescription(LoadDescription data) throws OseeCoreException {
      if (handler != null) {
         handler.onLoadDescription(data);
      }
   }

   @Override
   public void onData(BranchData data) throws OseeCoreException {
         if (handler != null) {
         handler.onData(data);
      }
   }

   @Override
   public void onData(TxOrcsData data) throws OseeCoreException {
      if (handler != null) {
         handler.onData(data);
      }
   }

   @Override
   public void onData(ArtifactData data) throws OseeCoreException {
      if (handler != null) {
         handler.onData(data);
      }
   }

   @Override
   public void onData(AttributeData data) throws OseeCoreException {
      if (handler != null) {
         handler.onData(data);
      }
   }

   @Override
   public void onData(RelationData data) throws OseeCoreException {
      if (handler != null) {
         handler.onData(data);
      }
   }

   @Override
   public void onLoadEnd() throws OseeCoreException {
      if (handler != null) {
         handler.onLoadEnd();
      }
   }
}