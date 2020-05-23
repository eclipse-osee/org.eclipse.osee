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

package org.eclipse.osee.logger;

/**
 * @author Roberto E. Escobar
 */
public interface Log {

   boolean isTraceEnabled();

   void trace(String format, Object... args);

   void trace(Throwable th, String format, Object... args);

   boolean isDebugEnabled();

   void debug(String format, Object... args);

   void debug(Throwable th, String format, Object... args);

   boolean isInfoEnabled();

   void info(String format, Object... args);

   void info(Throwable th, String format, Object... args);

   boolean isWarnEnabled();

   void warn(String format, Object... args);

   void warn(Throwable th, String format, Object... args);

   boolean isErrorEnabled();

   void error(String format, Object... args);

   void error(Throwable th, String format, Object... args);
}
