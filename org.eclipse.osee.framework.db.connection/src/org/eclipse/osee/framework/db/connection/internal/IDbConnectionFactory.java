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

package org.eclipse.osee.framework.db.connection.internal;

import org.eclipse.osee.framework.db.connection.IConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IDbConnectionFactory {
   IConnection get(String driver) throws OseeCoreException;

   void bind(IConnection connection);

   void unbind(IConnection connection);
}
