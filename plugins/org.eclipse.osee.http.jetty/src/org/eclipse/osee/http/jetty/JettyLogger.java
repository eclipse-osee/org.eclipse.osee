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

/**
 * @author Roberto E. Escobar
 */
public interface JettyLogger {

   void debug(String msg, Object... data);

   void error(Throwable ex, String msg, Object... data);

   void warn(Throwable ex, String msg, Object... data);

}