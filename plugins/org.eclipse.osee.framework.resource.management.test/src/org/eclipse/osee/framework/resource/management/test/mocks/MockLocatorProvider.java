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
package org.eclipse.osee.framework.resource.management.test.mocks;

import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorProvider;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;

/**
 * @author Roberto E. Escobar
 */
public class MockLocatorProvider implements IResourceLocatorProvider {

   private final String protocolToMatch;

   public MockLocatorProvider(String protocolToMatch) {
      this.protocolToMatch = protocolToMatch;
   }

   @Override
   public IResourceLocator generateResourceLocator(String seed, String name) throws MalformedLocatorException {
      return new MockResourceLocator(getSupportedProtocol(), seed, name, null);
   }

   @Override
   public IResourceLocator getResourceLocator(String path) throws MalformedLocatorException {
      return new MockResourceLocator(getSupportedProtocol(), null, null, path);
   }

   @Override
   public boolean isValid(String protocol) {
      return protocol != null && protocol.startsWith(getSupportedProtocol());
   }

   @Override
   public String getSupportedProtocol() {
      return protocolToMatch;
   }
}