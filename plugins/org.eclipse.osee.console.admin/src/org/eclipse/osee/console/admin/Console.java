/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
