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
