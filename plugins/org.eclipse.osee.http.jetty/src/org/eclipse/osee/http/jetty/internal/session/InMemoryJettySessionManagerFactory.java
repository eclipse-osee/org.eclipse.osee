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
package org.eclipse.osee.http.jetty.internal.session;

import java.util.Map;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.osee.http.jetty.JettyLogger;
import org.eclipse.osee.http.jetty.JettySessionManagerFactory;

/**
 * @author Roberto E. Escobar
 */
public class InMemoryJettySessionManagerFactory implements JettySessionManagerFactory {

   @Override
   public SessionManager newSessionManager(JettyLogger logger, Server server, Map<String, Object> props) {
      return new HashSessionManager();
   }

}