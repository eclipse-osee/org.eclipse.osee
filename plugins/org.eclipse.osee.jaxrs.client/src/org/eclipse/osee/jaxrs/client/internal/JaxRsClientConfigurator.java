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
package org.eclipse.osee.jaxrs.client.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import javax.ws.rs.client.ClientBuilder;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.eclipse.osee.jaxrs.client.JaxRsClientConfig;

/**
 * @author Roberto E. Escobar
 */
public interface JaxRsClientConfigurator {

   void configureDefaults(Map<String, Object> properties);

   void configureDefaults(Map<String, Object> properties, ObjectMapper mapper);

   void configureBean(JaxRsClientConfig config, String serverAddress, JAXRSClientFactoryBean bean);

   void configureClientBuilder(JaxRsClientConfig config, ClientBuilder builder);

   void configureConnection(JaxRsClientConfig config, HTTPConduit conduit);

   void configureProxy(JaxRsClientConfig config, HTTPConduit conduit);

   void configureJaxRsRuntime();

}