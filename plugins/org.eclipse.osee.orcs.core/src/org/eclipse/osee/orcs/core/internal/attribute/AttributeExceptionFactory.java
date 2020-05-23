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

package org.eclipse.osee.orcs.core.internal.attribute;

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeExceptionFactory {

   MultipleAttributesExist createManyExistException(AttributeTypeId typeSearched, int count);

   AttributeDoesNotExist createDoesNotExistException(AttributeTypeId typeSearched);

}