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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class XPercent extends XText {

   public XPercent(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
   }

   public void set(int percent) {
      super.set(percent + "");
   }

   public Result isValid() {
      if (super.requiredEntry()) {
         Result result = super.isValid();
         if (result.isFalse()) {
            return result;
         } else if (!this.isInteger()) {
            return new Result("Percent must be an Integer");
         } else if (this.getInteger() < 0 || this.getInteger() > 100) {
            return new Result("Percent must be between 0 and 100");
         }
      }
      return Result.TrueResult;
   }
}
