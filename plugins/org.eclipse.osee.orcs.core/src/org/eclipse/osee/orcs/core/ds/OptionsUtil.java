/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public final class OptionsUtil {

   private OptionsUtil() {
      // Utility method
   }
   private static final int TRANSACTION_SENTINEL = -1;

   private static final String FROM_TRANSACTION = "from.transaction";
   private static final String INCLUDE_DELETED = "include.deleted";
   private static final String INCLUDE_CACHE = "include.cache";
   private static final String LOAD_LEVEL = "load.level";

   public static Options createOptions() {
      Options options = new Options();
      setIncludeCache(options, false);
      setIncludeDeleted(options, false);
      setHeadTransaction(options);
      setLoadLevel(options, LoadLevel.SHALLOW);
      return options;
   }

   public static void reset(Options options) {
      setIncludeCache(options, false);
      setIncludeDeleted(options, false);
      setHeadTransaction(options);
      setLoadLevel(options, LoadLevel.SHALLOW);
   }

   public static boolean isCacheIncluded(Options options) {
      return options.getBoolean(INCLUDE_CACHE);
   }

   public static void setIncludeCache(Options options, boolean enabled) {
      options.put(INCLUDE_CACHE, enabled);
   }

   public static boolean areDeletedIncluded(Options options) {
      return options.getBoolean(INCLUDE_DELETED);
   }

   public static void setIncludeDeleted(Options options, boolean enabled) {
      options.put(INCLUDE_DELETED, enabled);
   }

   public static DeletionFlag getIncludeDeleted(Options options) {
      boolean includeDeleted = areDeletedIncluded(options);
      return DeletionFlag.allowDeleted(includeDeleted);
   }

   public static LoadLevel getLoadLevel(Options options) {
      String level = options.get(LOAD_LEVEL);
      LoadLevel loadLevel = LoadLevel.SHALLOW;
      if (Strings.isValid(level)) {
         loadLevel = LoadLevel.valueOf(level);
      }
      return loadLevel;
   }

   public static void setLoadLevel(Options options, LoadLevel loadLevel) {
      options.put(LOAD_LEVEL, loadLevel.name());
   }

   public static void setFromTransaction(Options options, int transactionId) {
      int transactionToSet = transactionId;
      if (transactionToSet < -1) {
         transactionToSet = TRANSACTION_SENTINEL;
      }
      options.put(FROM_TRANSACTION, transactionToSet);
   }

   public static int getFromTransaction(Options options) {
      int transactionId = TRANSACTION_SENTINEL;
      if (!options.isEmpty(FROM_TRANSACTION)) {
         transactionId = options.getInt(FROM_TRANSACTION);
      }
      if (transactionId < -1) {
         transactionId = TRANSACTION_SENTINEL;
      }
      return transactionId;
   }

   public static void setHeadTransaction(Options options) {
      setFromTransaction(options, TRANSACTION_SENTINEL);
   }

   public static boolean isHeadTransaction(Options options) {
      return TRANSACTION_SENTINEL == getFromTransaction(options);
   }

   public static boolean isHistorical(Options options) {
      return !isHeadTransaction(options);
   }

}
