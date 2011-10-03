/*
 * Created on Sep 29, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.relation;

import org.eclipse.osee.logger.Log;

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

   private void commonOut(String format, Object... args) {
      System.out.printf(format, args);
      System.out.println();
   }

   private void commonOut(Throwable th, String format, Object... args) {
      commonOut(format, args);
      th.printStackTrace();
   }

}
