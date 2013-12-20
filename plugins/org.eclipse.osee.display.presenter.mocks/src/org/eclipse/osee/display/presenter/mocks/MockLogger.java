/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.mocks;

import org.eclipse.osee.logger.Log;

/**
 * @author John R. Misinco
 */
public class MockLogger implements Log {

   @Override
   public boolean isTraceEnabled() {
      return false;
   }

   @Override
   public void trace(String format, Object... args) {
      // do nothing
   }

   @Override
   public void trace(Throwable th, String format, Object... args) {
      // do nothing
   }

   @Override
   public boolean isDebugEnabled() {
      return false;
   }

   @Override
   public void debug(String format, Object... args) {
      // do nothing
   }

   @Override
   public void debug(Throwable th, String format, Object... args) {
      // do nothing
   }

   @Override
   public boolean isInfoEnabled() {
      return false;
   }

   @Override
   public void info(String format, Object... args) {
      // do nothing
   }

   @Override
   public void info(Throwable th, String format, Object... args) {
      // do nothing
   }

   @Override
   public boolean isWarnEnabled() {
      return false;
   }

   @Override
   public void warn(String format, Object... args) {
      // do nothing
   }

   @Override
   public void warn(Throwable th, String format, Object... args) {
      // do nothing
   }

   @Override
   public boolean isErrorEnabled() {
      return false;
   }

   @Override
   public void error(String format, Object... args) {
      // do nothing
   }

   @Override
   public void error(Throwable th, String format, Object... args) {
      // do nothing
   }

}
