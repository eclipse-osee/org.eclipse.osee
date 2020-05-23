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
