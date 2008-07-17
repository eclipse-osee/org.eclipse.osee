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
package org.eclipse.osee.framework.svn;

import org.eclipse.core.runtime.Platform;

/**
 * @author Roberto E. Escobar
 */
public class EclipseVersion {

   private EclipseVersion() {
   }

   public static boolean isVersion(String toCheck) {
      String value = (String) Platform.getBundle("org.eclipse.pde").getHeaders().get("Bundle-Version");
      return value.startsWith(toCheck);
   }
}
