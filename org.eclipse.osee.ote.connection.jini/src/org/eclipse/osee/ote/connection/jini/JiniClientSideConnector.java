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
package org.eclipse.osee.ote.connection.jini;

import java.io.File;
import java.net.URI;
import java.rmi.RemoteException;

import net.jini.core.lookup.ServiceItem;

/**
 * @author Ken J. Aguilar
 */
public class JiniClientSideConnector extends JiniConnector {
   public static final String TYPE = "jini.client-end";
   private final ServiceItem serviceItem;
   private final IJiniConnectorLink link;
   JiniClientSideConnector(ServiceItem serviceItem) {
      super();
      this.serviceItem = serviceItem;
      buildPropertiesFromEntries(serviceItem.attributeSets, getProperties());
      link = (IJiniConnectorLink) getProperties().getProperty(LINK_PROPERTY);
   }

   @Override
   public Object getService() {
         return serviceItem.service;
   }

   @Override
   public String getConnectorType() {
      return TYPE;
   }

   @Override
   public URI upload(File file) throws Exception {
      return null;
   }

    @Override
    public boolean ping() {
	try {
	    return link.ping();
	} catch (RemoteException e) {
	    return false;
	}
    }

}
