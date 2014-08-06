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
package org.eclipse.osee.framework.jdk.core.type;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public interface OseePrincipal extends Principal, Identity<Long> {

   String getLogin();

   Set<String> getRoles();

   String getDisplayName();

   String getUserName();

   String getEmailAddress();

   boolean isActive();

   boolean isAuthenticated();

   Map<String, String> getProperties();

}