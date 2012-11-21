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

import java.util.Collection;
import java.util.Date;

/**
 * @author Roberto E. Escobar
 */
public interface ConsoleParameters {

   String getCommandName();

   String getRawString();

   String get(String key);

   String[] getArray(String key);

   boolean getBoolean(String key);

   double getDouble(String key) throws NumberFormatException;

   float getFloat(String key) throws NumberFormatException;

   int getInt(String key) throws NumberFormatException;

   long getLong(String key) throws NumberFormatException;

   Date getDate(String key) throws IllegalArgumentException;

   boolean exists(String key);

   Collection<String> getOptions();

}
