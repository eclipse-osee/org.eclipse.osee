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
package org.eclipse.osee.console.admin.internal;

import java.util.Date;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ConsoleParametersImpl implements ConsoleParameters {

   private final String commandName;
   private final String rawString;
   private final PropertyStore store;

   public ConsoleParametersImpl(String commandName, String rawString, PropertyStore store) {
      this.commandName = commandName;
      this.rawString = rawString;
      this.store = store;
   }

   @Override
   public String getRawString() {
      return rawString;
   }

   @Override
   public String get(String key) {
      return store.get(key);
   }

   @Override
   public String[] getArray(String key) {
      return store.getArray(key);
   }

   @Override
   public boolean getBoolean(String key) {
      return store.getBoolean(key);
   }

   @Override
   public double getDouble(String key) throws NumberFormatException {
      return store.getDouble(key);
   }

   @Override
   public float getFloat(String key) throws NumberFormatException {
      return store.getFloat(key);
   }

   @Override
   public int getInt(String key) throws NumberFormatException {
      return store.getInt(key);
   }

   @Override
   public long getLong(String key) throws NumberFormatException {
      return store.getLong(key);
   }

   @Override
   public Date getDate(String key) throws IllegalArgumentException {
      long value = store.getLong(key);
      return new Date(value);
   }

   @Override
   public boolean exists(String key) {
      return Strings.isValid(store.get(key));
   }

   @Override
   public String getCommandName() {
      return commandName;
   }

}
