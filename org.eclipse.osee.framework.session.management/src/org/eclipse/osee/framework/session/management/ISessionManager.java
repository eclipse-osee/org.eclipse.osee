/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.session.management;

import org.eclipse.osee.framework.core.data.OseeSession;

/**
 * @author Roberto E. Escobar
 */
public interface ISessionManager {

   public OseeSession authenticate(ICredential credential) throws AuthenticationException;
}
