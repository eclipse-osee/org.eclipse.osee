/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.mocks;

import org.eclipse.osee.logger.Log;

/**
 * @author Andrew M. Finkbeiner
 */
public class MockLog implements Log {

   @Override
   public boolean isTraceEnabled() {
      return true;
   }

   @Override
   public void trace(String format, Object... args) {
      commonOut(format, args);
   }

   @Override
   public void trace(Throwable th, String format, Object... args) {
      commonOut(format, args);
   }

   @Override
   public void traceNoFormat(Throwable throwable, CharSequence message) {
      commonOut(throwable, message);
   }

   @Override
   public boolean isDebugEnabled() {
      return true;
   }

   @Override
   public void debug(String format, Object... args) {
      commonOut(format, args);
   }

   @Override
   public void debug(Throwable th, String format, Object... args) {
      commonOut(th, format, args);
   }

   @Override
   public void debugNoFormat(Throwable throwable, CharSequence message) {
      commonOut(throwable, message);
   }

   @Override
   public boolean isInfoEnabled() {
      return true;
   }

   @Override
   public void info(String format, Object... args) {
      commonOut(format, args);
   }

   @Override
   public void info(Throwable th, String format, Object... args) {
      commonOut(th, format, args);
   }

   @Override
   public void infoNoFormat(Throwable throwable, CharSequence message) {
      commonOut(throwable, message);
   }

   @Override
   public boolean isWarnEnabled() {
      return true;
   }

   @Override
   public void warn(String format, Object... args) {
      commonOut(format, args);
   }

   @Override
   public void warn(Throwable th, String format, Object... args) {
      commonOut(th, format, args);
   }

   @Override
   public void warnNoFormat(Throwable throwable, CharSequence message) {
      commonOut(throwable, message);
   }

   @Override
   public boolean isErrorEnabled() {
      return true;
   }

   @Override
   public void error(String format, Object... args) {
      commonOut(format, args);
   }

   @Override
   public void error(Throwable th, String format, Object... args) {
      commonOut(th, format, args);
   }

   @Override
   public void errorNoFormat(Throwable throwable, CharSequence message) {
      commonOut(throwable, message);
   }

   private void commonOut(String format, Object... args) {
      System.out.printf(format, args);
      System.out.println();
   }

   private void commonOut(Throwable th, String format, Object... args) {
      commonOut(format, args);
      th.printStackTrace();
   }

   private void commonOut(Throwable throwable, CharSequence message) {
      System.out.println(message);
      throwable.printStackTrace();
   }

}
