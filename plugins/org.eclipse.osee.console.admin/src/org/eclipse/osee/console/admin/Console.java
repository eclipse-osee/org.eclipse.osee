/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.console.admin;

import java.util.Dictionary;

/**
 * @author Roberto E. Escobar
 */
public interface Console {

   public Object execute(String command);

   public void write(Object o);

   public void write(String message, Object... args);

   public void write(Throwable throwable);

   public void write(String title, Dictionary<?, ?> dictionary);

   public void writeln(Object o);

   public void writeln(String message, Object... args);

   public void writeln(Throwable throwable);

   public void writeln(String title, Dictionary<?, ?> dictionary);

   public void writeln();

}
