/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.core.enums;

/**
 * @author Angel Avila
 */
public enum DirtyState {
   CLEAN,
   APPLICABILITY_ONLY,
   OTHER_CHANGES;

   public boolean isDirty() {
      return this != CLEAN;
   }

   public boolean isApplicOnly() {
      return this == DirtyState.APPLICABILITY_ONLY;
   }
}
