/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.core.model.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.access.AccessData;
import org.eclipse.osee.framework.core.data.AccessContextToken;

/**
 * @author Roberto E. Escobar
 */
public interface AccessModel {

   void computeAccess(AccessContextToken contextId, Collection<Object> objectsToCheck, AccessData accessData);
}