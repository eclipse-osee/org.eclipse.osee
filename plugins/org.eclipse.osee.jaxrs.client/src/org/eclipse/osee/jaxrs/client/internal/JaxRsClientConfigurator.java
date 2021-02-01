/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.jaxrs.client.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.client.ClientBuilder;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.eclipse.osee.jaxrs.client.JaxRsClientConfig;

/**
 * @author Roberto E. Escobar
 */
public interface JaxRsClientConfigurator {

   void configureDefaults(ObjectMapper mapper);

   void configureBean(JaxRsClientConfig config, String serverAddress, JAXRSClientFactoryBean bean);

   void configureClientBuilder(JaxRsClientConfig config, ClientBuilder builder);

   void configureConnection(JaxRsClientConfig config, HTTPConduit conduit);

   void configureProxy(JaxRsClientConfig config, HTTPConduit conduit);

   void configureJaxRsRuntime();

}