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
package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Roberto E. Escobar
 */
public interface IdentityManager extends IdentityLocator {

   TransactionId getNextTransactionId();

   int getNextArtifactId();

   int getNextAttributeId();

   int getNextRelationId();

   long getNextGammaId();

   void invalidateIds();

   String getUniqueGuid(String guid);

}
