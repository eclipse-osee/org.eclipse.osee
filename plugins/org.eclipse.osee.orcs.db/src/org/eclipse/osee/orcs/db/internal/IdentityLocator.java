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

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IdentityLocator {

   long getLocalId(IOseeBranch branch) throws OseeCoreException;

   Long parseToLocalId(String value) throws OseeCoreException;

   Long getLocalId(Long universalId) throws OseeCoreException;

   Long getUniversalId(Long localId) throws OseeCoreException;

   long getLocalId(Identity<Long> identity) throws OseeCoreException;

   IOseeBranch getBranch(long branchId) throws OseeCoreException;

}
