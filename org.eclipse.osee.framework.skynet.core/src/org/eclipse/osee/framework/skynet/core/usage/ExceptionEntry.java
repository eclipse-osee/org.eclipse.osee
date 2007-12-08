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
package org.eclipse.osee.framework.skynet.core.usage;

import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Robert A. Fisher
 */
public final class ExceptionEntry extends UsageEntry {

   public ExceptionEntry(Exception exception) {
      super(getExceptionDetails(exception));
   }

   @Override
   public String getDescription() {
      return "Exception";
   }

   @Override
   protected int getEventOrdinal() {
      return 3;
   }

   private static String getExceptionDetails(Exception exception) {
      return Lib.exceptionToString(exception);
   }
}
