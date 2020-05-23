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

package org.eclipse.osee.framework.skynet.core.utility;

import org.eclipse.osee.framework.core.client.OseeClientProperties;

/**
 * @author Donald G. Dunne
 */
public final class DbUtil {

   private DbUtil() {
      // Utility Class - class should only have static methods
   }

   public static boolean isDbInit() {
      return OseeClientProperties.isInDbInit();
   }

}
