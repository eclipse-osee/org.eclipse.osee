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
package org.eclipse.osee.framework.core.access;

import org.eclipse.osee.framework.core.data.AccessContextToken;

/**
 * @author Donald G. Dunne
 */
public final class FrameworkAccessContextTokens {

   public static final AccessContextToken DEFAULT_CONTEXT = //
      AccessContextToken.valueOf(5831696394960588656L, "default.context");

   private FrameworkAccessContextTokens() {
      //
   }

}
