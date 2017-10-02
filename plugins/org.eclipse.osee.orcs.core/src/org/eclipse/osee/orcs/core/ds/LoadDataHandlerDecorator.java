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

import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;

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
   public void onLoadStart() {
      if (handler != null) {
         handler.onLoadStart();
      }
   }

   @Override
   public void onData(AttributeData data, MatchLocation match) {
      if (handler != null) {
         handler.onData(data, match);
      }
   }

   @Override
   public void onLoadDescription(LoadDescription data) {
      if (handler != null) {
         handler.onLoadDescription(data);
      }
   }

   @Override
   public void onData(BranchData data) {
      if (handler != null) {
         handler.onData(data);
      }
   }

   @Override
   public void onData(TxOrcsData data) {
      if (handler != null) {
         handler.onData(data);
      }
   }

   @Override
   public void onData(ArtifactData data) {
      if (handler != null) {
         handler.onData(data);
      }
   }

   @Override
   public void onData(AttributeData data) {
      if (handler != null) {
         handler.onData(data);
      }
   }

   @Override
   public void onData(RelationData data) {
      if (handler != null) {
         handler.onData(data);
      }
   }

   @Override
   public void onDynamicData(Map<String, Object> data) {
      if (handler != null) {
         handler.onDynamicData(data);
      }
   }

   @Override
   public void onLoadEnd() {
      if (handler != null) {
         handler.onLoadEnd();
      }
   }
}