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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Identity;

/**
 * @author Roberto E. Escobar
 */
public interface IdentityLocator {

   int getLocalId(IOseeBranch branch) throws OseeCoreException;

   int parseToLocalId(String value) throws OseeCoreException;

   Integer getLocalId(Long universalId) throws OseeCoreException;

   Long getUniversalId(Integer localId) throws OseeCoreException;

   int getLocalId(Identity<Long> identity) throws OseeCoreException;

   IOseeBranch getBranch(int branchId) throws OseeCoreException;

}
