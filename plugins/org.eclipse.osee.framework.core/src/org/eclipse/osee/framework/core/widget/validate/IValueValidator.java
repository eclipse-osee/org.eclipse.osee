/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.core.widget.validate;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IValueValidator {

   default public boolean isNoOp() {
      return this instanceof XXNoOpValidator;
   }

   default public boolean isEmpty() {
      return true;
   }

   default public XResultData isValid(Collection<? extends Object> objects) {
      for (Object obj : objects) {
         XResultData rd = isValid(obj);
         if (rd.isErrors()) {
            return rd;
         }
      }
      return XWidgetStatus.OK;
   }

   default public XResultData isValid(Object obj) {
      return XWidgetStatus.OK;
   }

}
