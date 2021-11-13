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

package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Roberto E. Escobar
 */
public interface IdentityManager {

   TransactionId getNextTransactionId();

   ArtifactId getNextArtifactId();

   int getNextAttributeId();

   int getNextRelationId();

   GammaId getNextGammaId();

   String getUniqueGuid(String guid);

}