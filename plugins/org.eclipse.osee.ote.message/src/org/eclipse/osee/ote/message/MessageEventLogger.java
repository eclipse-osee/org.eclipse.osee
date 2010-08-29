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
package org.eclipse.osee.ote.message;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.enums.DataType;

/**
 * @author Ken J. Aguilar
 */
public class MessageEventLogger implements UniversalMessageListener {
   private static final String BUNDLE = "org.eclipse.osee.ote.message";
   private static final class StackTrace extends RuntimeException {
      private static final long serialVersionUID = 6805422596135323489L;

      public StackTrace() {
         super("Event stack trace");
      }
   }

   private final Message<?, ?, ?> message;
   private final String modeStatus;

   private volatile boolean showStackTrace = false;

   public MessageEventLogger(Message<?, ?, ?> message) {
      this(message, false);
   }

   /**
    * @return the message
    */
   public Message<?, ?, ?> getMessage() {
      return message;
   }

   /**
    * Creates a message event logger that also prints a stack trace when the event is fired
    * 
    */
   public MessageEventLogger(Message<?, ?, ?> message, boolean showStackTrace) {
      this.message = message;
      modeStatus = message.isWriter() ? "wirter " : "reader";
      message.addPostMemSourceChangeListener(this);
      message.addPostMessageDisposeListener(this);
      message.addPreMessageDisposeListener(this);
      message.addSchedulingChangeListener(this);
      this.showStackTrace = showStackTrace;
   }

   @Override
   public void onChange(DataType oldtype, DataType newType, Message<?, ?, ?> message) {
      log(Level.INFO, String.format("MemType for %s %s has changed from %s to %s", message.getName(), modeStatus,
         oldtype.name(), newType.name()), showStackTrace ? new StackTrace() : null);
   }

   @Override
   public void onPostDispose(Message<?, ?, ?> message) {
      log(Level.INFO, String.format("%s %s has been disposed", message.getName(), modeStatus),
         showStackTrace ? new StackTrace() : null);
   }

   @Override
   public void onPreDispose(Message<?, ?, ?> message) {
      log(Level.INFO, String.format("%s %s is about to be disposed", message.getName(), modeStatus),
         showStackTrace ? new StackTrace() : null);
   }

   @Override
   public void isScheduledChanged(boolean isScheduled) {
      log(Level.INFO, String.format("schedule status for %s has changed to %s. Env time is %d", message.getName(),
         modeStatus, isScheduled ? "scheduled" : "not scheduled"), showStackTrace ? new StackTrace() : null);
   }

   @Override
   public void onRateChanged(Message<?, ?, ?> message, double oldRate, double newRate) {
      log(Level.INFO,
         String.format("rate for %s %s change from %f to %f", message.getName(), modeStatus, oldRate, newRate),
         showStackTrace ? new StackTrace() : null);
   }

   protected void log(Level level, String message) {
      OseeLog.log(MessageSystemTestEnvironment.class, level, message);
   }

   protected void log(Level level, String message, StackTrace stackTrace) {
      OseeLog.log(MessageSystemTestEnvironment.class, level, message, stackTrace);
   }

   protected StackTraceElement[] getStackTrace() {
      return new Exception().getStackTrace();
   }
}
