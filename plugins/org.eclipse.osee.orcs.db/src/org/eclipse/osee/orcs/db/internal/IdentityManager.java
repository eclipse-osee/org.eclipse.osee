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

import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IdentityManager extends IdentityLocator {

   int getNextArtifactId() throws OseeCoreException;

   int getNextAttributeId() throws OseeCoreException;

   int getNextRelationId() throws OseeCoreException;

   String getUniqueGuid(String guid) throws OseeCoreException;

   long getNextGammaId() throws OseeCoreException;

   void invalidateIds() throws OseeDataStoreException;

}
