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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class LoadDataHandlerAdapter implements LoadDataHandler {

   @Override
   public void onLoadStart()  {
      //
   }

   @Override
   public void onLoadDescription(LoadDescription data)  {
      //
   }

   @Override
   public void onData(BranchData data)  {
      //
   }

   @Override
   public void onData(TxOrcsData data)  {
      //
   }

   @Override
   public void onData(ArtifactData data)  {
      //
   }

   @Override
   public void onData(AttributeData data)  {
      //
   }

   @Override
   public void onData(RelationData data)  {
      //
   }

   @Override
   public void onData(AttributeData data, MatchLocation match)  {
      //
   }

   @Override
   public void onDynamicData(Map<String, Object> data)  {
      //
   }

   @Override
   public void onLoadEnd()  {
      //
   }
}
