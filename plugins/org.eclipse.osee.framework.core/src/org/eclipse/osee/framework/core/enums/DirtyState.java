/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
