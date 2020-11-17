/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.jdk.core.result;

import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Logger that will log to console. Intended for classes that use the console as their output. This is hack to allow
 * console classes to write to system err without having to call directly. Thus, a search of the code base will not find
 * any system err or out calls.
 *
 * @author Donald G. Dunne
 */
public class XConsoleLogger extends XResultData {

   public static XConsoleLogger instance = new XConsoleLogger();

   public XConsoleLogger() {
      setLogToSysErr(true);
   }

   public static void out(String formatStr, Object... objs) {
      // If no objs, do not send back into formatter or exceptions could occur
      if (objs.length == 0) {
         instance.log(formatStr);
      } else {
         instance.logf(formatStr, objs);
      }
   }

   public static void err(String formatStr, Object... objs) {
      instance.logStr(Type.ConsoleErr, formatStr, objs);
   }

   public static void err(Exception ex) {
      instance.errorf(Lib.exceptionToString(ex));
   }

}
