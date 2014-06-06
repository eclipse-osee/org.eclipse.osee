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
package org.eclipse.osee.ats.config;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsClientConstants;
import org.eclipse.osee.jaxrs.client.JaxRsClientFactory;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigurationUtil {

   public static AtsConfigurations getConfigurations() {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri = UriBuilder.fromPath("ats").path("config").build();
      Map<String, Object> config = new HashMap<String, Object>();
      config.put(JaxRsClientConstants.JAXRS_CLIENT_SERVER_ADDRESS, appServer);
      JaxRsClient client = JaxRsClientFactory.createClient(config);
      WebResource resource = client.createResource(uri);
      return resource.accept(MediaType.APPLICATION_JSON).get(AtsConfigurations.class);
   }
}
