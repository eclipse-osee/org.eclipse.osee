/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.security;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Roberto E. Escobar
 */
public interface JaxRsSessionProvider {

   void createSession(HttpServletRequest request, String scheme, Object sc);

   Object getFromSession(HttpServletRequest request);
}