/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
      instance.logf(formatStr, objs);
   }

   public static void err(String formatStr, Object... objs) {
      instance.logStr(Type.ConsoleErr, formatStr, objs);
   }

   public static void err(Exception ex) {
      instance.errorf(Lib.exceptionToString(ex));
   }

}
