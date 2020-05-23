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

package org.eclipse.osee.jaxrs.client.internal.ext;

import java.net.URI;
import java.util.Map;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.client.spec.ClientImpl.WebTargetImpl;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;

/**
 * Facade over WebTarget
 * 
 * @author Roberto E. Escobar
 */
public class JaxRsWebTargetImpl implements JaxRsWebTarget {

   private final WebTarget target;

   public JaxRsWebTargetImpl(WebTarget target) {
      this.target = target;
   }

   @Override
   public Configuration getConfiguration() {
      return target.getConfiguration();
   }

   @Override
   public JaxRsWebTarget property(String name, Object value) {
      target.property(name, value);
      return this;
   }

   @Override
   public JaxRsWebTarget register(Class<?> componentClass) {
      target.register(componentClass);
      return this;
   }

   @Override
   public JaxRsWebTarget register(Class<?> componentClass, int priority) {
      target.register(componentClass, priority);
      return this;
   }

   @Override
   public JaxRsWebTarget register(Class<?> componentClass, Class<?>... contracts) {
      target.register(componentClass, contracts);
      return this;
   }

   @Override
   public JaxRsWebTarget register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
      target.register(componentClass, contracts);
      return this;
   }

   @Override
   public JaxRsWebTarget register(Object component) {
      target.register(component);
      return this;
   }

   @Override
   public JaxRsWebTarget register(Object component, int priority) {
      target.register(component, priority);
      return this;
   }

   @Override
   public JaxRsWebTarget register(Object component, Class<?>... contracts) {
      target.register(component, contracts);
      return this;
   }

   @Override
   public JaxRsWebTarget register(Object component, Map<Class<?>, Integer> contracts) {
      target.register(component, contracts);
      return this;
   }

   @Override
   public URI getUri() {
      return target.getUri();
   }

   @Override
   public UriBuilder getUriBuilder() {
      return target.getUriBuilder();
   }

   @Override
   public Builder request() {
      return target.request();
   }

   @Override
   public Builder request(String... acceptedResponseTypes) {
      return target.request(acceptedResponseTypes);
   }

   @Override
   public Builder request(MediaType... acceptedResponseTypes) {
      return target.request(acceptedResponseTypes);
   }

   protected WebClient getWebClient() {
      WebClient webClient = null;
      if (target instanceof WebTargetImpl) {
         webClient = ((WebTargetImpl) target).getWebClient();
      }
      return webClient;
   }

   @Override
   public <T> T newProxy(Class<T> clazz) {
      // This is here to force a webClient to store its configuration
      target.request();

      WebClient webClient = getWebClient();
      return JAXRSClientFactory.fromClient(webClient, clazz);
   }

}