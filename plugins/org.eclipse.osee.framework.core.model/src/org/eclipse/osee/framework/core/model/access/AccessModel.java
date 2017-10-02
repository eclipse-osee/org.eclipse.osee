/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAccessContextId;

/**
 * @author Roberto E. Escobar
 */
public interface AccessModel {

   void computeAccess(IAccessContextId contextId, Collection<Object> objectsToCheck, AccessData accessData) ;
}