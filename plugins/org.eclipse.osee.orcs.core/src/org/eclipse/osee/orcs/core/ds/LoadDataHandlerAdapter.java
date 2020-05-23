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
public class LoadDataHandlerAdapter implements LoadDataHandler {

   @Override
   public void onLoadStart() {
      //
   }

   @Override
   public void onLoadDescription(LoadDescription data) {
      //
   }

   @Override
   public void onData(ArtifactData data) {
      //
   }

   @Override
   public <T> void onData(AttributeData<T> data) {
      //
   }

   @Override
   public void onData(RelationData data) {
      //
   }

   @Override
   public <T> void onData(AttributeData<T> data, MatchLocation match) {
      //
   }

   @Override
   public void onDynamicData(Map<String, Object> data) {
      //
   }

   @Override
   public void onLoadEnd() {
      //
   }
}