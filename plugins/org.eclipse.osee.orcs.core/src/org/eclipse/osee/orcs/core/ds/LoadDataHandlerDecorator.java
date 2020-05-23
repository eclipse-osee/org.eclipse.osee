/*********************************************************************
 * Copyright (c) 2013 Boeing
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
   public <T> void onData(AttributeData<T> data, MatchLocation match) {
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
   public void onData(ArtifactData data) {
      if (handler != null) {
         handler.onData(data);
      }
   }

   @Override
   public <T> void onData(AttributeData<T> data) {
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