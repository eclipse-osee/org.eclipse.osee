/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.client.demo;

import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProviderBase;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;

/**
 * @author Ryan D. Brooks
 */
public final class ClientDemoTypeTokenProvider extends OrcsTypeTokenProviderBase {
   private static final NamespaceToken CLIENTDEMO =
      NamespaceToken.valueOf(9, "client demo", "Namespace for client demo system and content management types");

   public static final OrcsTypeTokens clientDemo = new OrcsTypeTokens(CLIENTDEMO);

   public ClientDemoTypeTokenProvider() {
      super(clientDemo);

      loadClasses(DemoOseeTypes.DemoArtifactWithSelectivePartition);
      registerTokenClasses(DemoOseeTypes.class);
   }
}