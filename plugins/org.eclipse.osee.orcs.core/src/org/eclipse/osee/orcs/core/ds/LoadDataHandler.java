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
package org.eclipse.osee.orcs.core.ds;

/**
 * @author Andrew M. Finkbeiner
 */
public interface LoadDataHandler extends AttributeDataMatchHandler, DynamicDataHandler {

   void onLoadStart();

   void onLoadDescription(LoadDescription data);

   void onData(BranchData data);

   void onData(TxOrcsData data);

   void onData(ArtifactData data);

   void onData(AttributeData data);

   void onData(RelationData data);

   void onLoadEnd();

}