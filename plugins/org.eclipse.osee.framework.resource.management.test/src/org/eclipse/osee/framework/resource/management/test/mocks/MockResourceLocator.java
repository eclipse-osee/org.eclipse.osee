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

import java.net.URI;
import org.eclipse.osee.framework.resource.management.IResourceLocator;

/**
 * @author Roberto E. Escobar
 */
public class MockResourceLocator implements IResourceLocator {
   private final String protocolToMatch;
   private final String rawPath;
   private final String seed;
   private final String name;

   public MockResourceLocator(String protocolToMatch, String seed, String name, String rawPath) {
      this.protocolToMatch = protocolToMatch;
      this.seed = seed;
      this.name = name;
      this.rawPath = rawPath;
   }

   public String getSeed() {
      return seed;
   }

   public String getName() {
      return name;
   }

   @Override
   public URI getLocation() {
      return null;
   }

   @Override
   public String getProtocol() {
      return protocolToMatch;
   }

   @Override
   public String getRawPath() {
      return rawPath;
   }

}