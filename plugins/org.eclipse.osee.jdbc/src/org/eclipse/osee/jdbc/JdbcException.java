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

package org.eclipse.osee.jdbc;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public final class JdbcException extends OseeCoreException {

   private static final long serialVersionUID = 3288916326837602854L;

   private JdbcException(Throwable cause, String message, Object... args) {
      super(cause, message, args);
   }

   private JdbcException(Throwable cause) {
      super(cause);
   }

   private JdbcException(String message, Object... args) {
      super(message, args);
   }

   public static JdbcException newJdbcException(Throwable cause, String message, Object... args) {
      return new JdbcException(cause, message, args);
   }

   public static JdbcException newJdbcException(Throwable cause) {
      return new JdbcException(cause);
   }

   public static JdbcException newJdbcException(String message, Object... args) {
      return new JdbcException(message, args);
   }
}
