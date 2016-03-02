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
package org.eclipse.osee.account.admin;

import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;

/**
 * @author Roberto E. Escobar
 */
public interface Account extends Identifiable<String> {

   Long getId();

   boolean isActive();

   String getEmail();

   String getUserName();

   AccountPreferences getPreferences();

   AccountWebPreferences getWebPreferences();
}
