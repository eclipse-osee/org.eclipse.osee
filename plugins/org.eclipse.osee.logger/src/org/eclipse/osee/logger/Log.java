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
