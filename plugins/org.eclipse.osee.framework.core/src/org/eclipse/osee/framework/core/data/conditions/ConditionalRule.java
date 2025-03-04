/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data.conditions;

import java.util.List;
import org.eclipse.osee.framework.core.enums.BooleanTriState;

/**
 * @author Donald G. Dunne
 */
public abstract class ConditionalRule {

   public boolean isDisabled(List<String> currentValues) {
      return !isEnabled(currentValues);
   }

   public boolean isEnabled(List<String> currentValues) {
      return true;
   }

   public BooleanTriState isRequired() {
      return BooleanTriState.NotSet;
   }

}
