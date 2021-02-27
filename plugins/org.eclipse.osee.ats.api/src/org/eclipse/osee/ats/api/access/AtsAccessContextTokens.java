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

package org.eclipse.osee.ats.api.access;

import org.eclipse.osee.framework.core.data.AccessContextToken;

/**
 * @author Donald G. Dunne
 */
public final class AtsAccessContextTokens {

   public static final AccessContextToken DENY_CONTEXT =
      AccessContextToken.valueOf(4870045005030602805L, "ats.branchobject.deny");

   private AtsAccessContextTokens() {
      // Branch Object Contexts;
   }
}
