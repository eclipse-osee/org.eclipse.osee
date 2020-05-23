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

package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class DbConnectionExceptionComposite extends ExceptionComposite {

   public DbConnectionExceptionComposite(Composite parent, Exception ex) {
      this(parent, ex.toString());
   }

   public DbConnectionExceptionComposite(Composite parent, String message) {
      super(parent, message);
   }

   /**
    * Tests the DB Connection and returns true if ok. If exceptions and parent != null, the
    * DbConnectionExceptionComposite will be displayed in parent giving exception information.
    */
   public static boolean dbConnectionIsOk(Composite parent) {
      Result result = dbConnectionIsOkResult();
      if (result.isFalse()) {
         new DbConnectionExceptionComposite(parent, result.getText());
         parent.layout();
      }
      return result.isTrue();
   }

   private static Result dbConnectionIsOkResult() {
      return DbConnectionUtility.dbConnectionIsOkResult();
   }

   public static boolean dbConnectionIsOk() {
      return DbConnectionUtility.dbConnectionIsOk();
   }

}
