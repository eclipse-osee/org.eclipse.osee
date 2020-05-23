/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.resource.management.test.mocks;

import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorProvider;

/**
 * @author Roberto E. Escobar
 */
public class MockLocatorProvider implements IResourceLocatorProvider {

   private final String protocolToMatch;

   public MockLocatorProvider(String protocolToMatch) {
      this.protocolToMatch = protocolToMatch;
   }

   @Override
   public IResourceLocator generateResourceLocator(String seed, String name) {
      return new MockResourceLocator(getSupportedProtocol(), seed, name, null);
   }

   @Override
   public IResourceLocator getResourceLocator(String path) {
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