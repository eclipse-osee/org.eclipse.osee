/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.http.jetty.internal.osgi;

import java.util.Map;
import org.eclipse.osee.http.jetty.JettyServer.Builder;
import org.eclipse.osee.http.jetty.internal.session.InMemoryJettySessionManagerFactory;

/**
 * @author Roberto E. Escobar
 */
public class InMemoryJettyHttpService extends AbstractJettyHttpService {

   @Override
   protected void customizeJettyServer(Builder builder, Map<String, Object> props) {
      builder.sessionManagerFactory(new InMemoryJettySessionManagerFactory());
   }

}