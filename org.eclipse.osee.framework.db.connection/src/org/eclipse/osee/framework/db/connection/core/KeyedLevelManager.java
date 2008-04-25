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

package org.eclipse.osee.framework.db.connection.core;

import java.sql.SQLException;
import java.util.Stack;

/**
 * Keeps track of nested calls by tracking start keys and confirming them upon the end of the level.<br>
 * <br>
 * Methods that may be extended
 * <ul>
 * <li>startTransactionLevel</li>
 * <li>endTransactionLevel</li>
 * </ul>
 * Methods that may be reimplemented
 * <ul>
 * <li>onInitialEntry</li>
 * <li>onExitLevelError</li>
 * <li>onLastExit</li>
 * </ul>
 * 
 * @author Robert A. Fisher
 */
public class KeyedLevelManager {
   private final Stack<KeyLevel> callKeyQueue;

   private final class KeyLevel {
      private Object key;
      private boolean isProcessed;

      private KeyLevel(Object key) {
         this.key = key;
         this.isProcessed = false;
      }

      private boolean isProcessed() {
         return isProcessed;
      }

      private void setIsProcessed(boolean isProcessed) {
         this.isProcessed = isProcessed;
      }

      @Override
      public boolean equals(Object obj) {
         return key.equals(obj);
      }
   }

   public KeyedLevelManager() {
      this.callKeyQueue = new Stack<KeyLevel>();
   }

   /**
    * Marks the start of an abstract level. If the nesting level for this manager is zero then this will initiate a new
    * series, otherwise this will be included in the series that this manager is already maintaining.
    * 
    * @param key An object for the manager to track this level. This will need to be passed to
    *           <code>endTransactionLevel</code> when the calling method is done.
    * @throws SQLException
    * @see KeyedLevelManager#endTransactionLevel(Object)
    */
   public boolean startTransactionLevel(Object key) throws SQLException {
      if (key == null) throw new IllegalArgumentException("key must not be null");
      if (isValid(key)) throw new IllegalArgumentException("The provided key is already in use for this manager");

      boolean initialEntry = !inLevel();
      callKeyQueue.push(new KeyLevel(key));

      if (initialEntry) {
         onInitialEntry();
      }

      return initialEntry;
   }

   /**
    * Re-implement this method to provide functionality upon initial entry. The default implementation does nothing.
    */
   protected void onInitialEntry() throws SQLException {
   }

   /**
    * Marks the end of an abstract level. If the nesting level for this manager is one then this will finish the series,
    * otherwise this will be included in the series that this manager is already maintaining.
    * 
    * @param key An object for the manager to track this level. This must be the same key that was passed to
    *           <code>startTransactionLevel</code>.
    * @see KeyedLevelManager#startTransactionLevel(Object)
    */
   public void endTransactionLevel(Object key) throws SQLException {
      if (key == null) throw new IllegalArgumentException("key must not be null");
      if (true != isValid(key)) throw new IllegalArgumentException("The provided key is not known by this manager");

      try {
         // Check for an unclosed transaction level. This marks abnormal execution from a prior
         // calling method.
         if (true != callKeyQueue.pop().equals(key)) {
            onExitLevelError();

            // Clean up until the key so that later methods can register as normal.
            while (true != callKeyQueue.pop().equals(key))
               ;

            throw new IllegalStateException("A transaction level was not closed");
         }
      } finally {
         // Check for the end of the series
         if (callKeyQueue.isEmpty()) {
            onLastExit();
         }
      }
   }

   public void setTransactionLevelSuccess(Object key) {
      if (key == null) throw new IllegalArgumentException("key must not be null");
      if (true != isValid(key)) throw new IllegalArgumentException("The provided key is not known by this manager");

      if (false != callKeyQueue.peek().equals(key)) {
         callKeyQueue.peek().setIsProcessed(true);
      }
   }

   public boolean isTransactionLevelSuccess(Object key) {
      if (key == null) throw new IllegalArgumentException("key must not be null");
      if (true != isValid(key)) throw new IllegalArgumentException("The provided key is not known by this manager");

      boolean toReturn = false;
      if (false != callKeyQueue.peek().equals(key)) {
         toReturn = callKeyQueue.peek().isProcessed();
      }
      return toReturn;
   }

   /**
    * Reimplement to provide functionality when a level is exited abnormally from the wrong key being provided. The
    * default implementation does nothing.
    */
   protected void onExitLevelError() {
   }

   /**
    * Reimplement to provide funtionality when a full series of levels have been exited. The default implementation does
    * nothing.
    */
   protected void onLastExit() throws SQLException {
   }

   /**
    * @return The current nesting level for the transaction.
    */
   public final int getNestingLevel() {
      return callKeyQueue.size();
   }

   /**
    * @return Whether this manager is already handling a level.
    */
   public final boolean inLevel() {
      return !callKeyQueue.isEmpty();
   }

   /**
    * @return Whether this manager is already handling a series.
    */
   public final boolean inTransaction() {
      return !callKeyQueue.isEmpty();
   }

   private boolean isValid(Object key) {
      boolean toReturn = false;
      for (int index = 0; index < callKeyQueue.size(); index++) {
         if (callKeyQueue.get(index).equals(key)) {
            toReturn = true;
            break;
         }
      }
      return toReturn;
   }
}
