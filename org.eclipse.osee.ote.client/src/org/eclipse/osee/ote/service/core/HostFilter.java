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
package org.eclipse.osee.ote.service.core;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.osee.connection.service.IConnectorFilter;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;

/**
 * @author Ken J. Aguilar
 */
public class HostFilter implements IConnectorFilter {

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IConnectorFilter#accept(org.eclipse.osee.connection.service.IServiceConnector)
    */
   @Override
   public boolean accept(IServiceConnector connector) {
      return connector.getService() instanceof IHostTestEnvironment;
   }

   public Collection<IServiceConnector> accept(Collection<IServiceConnector> connectors) {
      HashSet<IServiceConnector> acceptableConnectors = new HashSet<IServiceConnector>();
      for (IServiceConnector connector : connectors) {
         if (accept(connector)) {
            acceptableConnectors.add(connector);
         }
      }
      return acceptableConnectors;
   }

}
