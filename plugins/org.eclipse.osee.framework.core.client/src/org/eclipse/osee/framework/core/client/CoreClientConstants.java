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

package org.eclipse.osee.framework.core.client;

/**
 * @author Roberto E. Escobar
 */
public final class CoreClientConstants {

   private static final String PLUGIN_ID = "org.eclipse.osee.framework.core.client";

   private CoreClientConstants() {
      //
   }

   public static String getBundleId() {
      return PLUGIN_ID;
   }

}
