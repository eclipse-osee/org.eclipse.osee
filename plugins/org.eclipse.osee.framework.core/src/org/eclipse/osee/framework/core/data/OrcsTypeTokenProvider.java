/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.OrcsTokenService;

/**
 * This interface is used to cause its implementations to be class loaded by OSGi before the start of the
 * OrcsTokenService.
 *
 * @author Ryan D. Brooks
 */
public interface OrcsTypeTokenProvider {

   /**
    * Upon binding of this OrcsTypeTokenProvider, its registerTypes method will be invoked
    */
   void registerTypes(OrcsTokenService tokenService);
}