/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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