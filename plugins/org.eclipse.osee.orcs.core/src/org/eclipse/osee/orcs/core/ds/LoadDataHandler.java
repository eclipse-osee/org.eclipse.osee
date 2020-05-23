/*********************************************************************
 * Copyright (c) 2012 Boeing
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

/**
 * @author Andrew M. Finkbeiner
 */
public interface LoadDataHandler extends AttributeDataMatchHandler, DynamicDataHandler {

   void onLoadStart();

   void onLoadDescription(LoadDescription data);

   void onData(ArtifactData data);

   <T> void onData(AttributeData<T> data);

   void onData(RelationData data);

   void onLoadEnd();
}