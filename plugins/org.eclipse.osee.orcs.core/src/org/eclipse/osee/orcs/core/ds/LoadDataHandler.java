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

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Andrew M. Finkbeiner
 */
public interface LoadDataHandler extends AttributeDataMatchHandler {

   void onLoadStart() throws OseeCoreException;

   void onLoadDescription(LoadDescription data) throws OseeCoreException;

   void onData(BranchData data) throws OseeCoreException;
   
   void onData(TxOrcsData data) throws OseeCoreException;

   void onData(ArtifactData data) throws OseeCoreException;

   void onData(AttributeData data) throws OseeCoreException;

   void onData(RelationData data) throws OseeCoreException;

   void onLoadEnd() throws OseeCoreException;

}