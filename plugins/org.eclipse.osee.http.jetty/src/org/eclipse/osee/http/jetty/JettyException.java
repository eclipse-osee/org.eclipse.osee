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
package org.eclipse.osee.http.jetty;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public final class JettyException extends OseeCoreException {

   private static final long serialVersionUID = -6418880754984202494L;

   private JettyException(Throwable cause, String message, Object... args) {
      super(cause, message, args);
   }

   private JettyException(Throwable cause) {
      super(cause);
   }

   private JettyException(String message, Object... args) {
      super(message, args);
   }

   public static JettyException newJettyException(Throwable cause, String message, Object... args) {
      return new JettyException(cause, message, args);
   }

   public static JettyException newJettyException(Throwable cause) {
      return new JettyException(cause);
   }

   public static JettyException newJettyException(String message, Object... args) {
      return new JettyException(message, args);
   }
}
