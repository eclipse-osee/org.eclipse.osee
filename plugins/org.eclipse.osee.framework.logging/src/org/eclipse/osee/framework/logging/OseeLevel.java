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

package org.eclipse.osee.framework.logging;

import java.util.logging.Level;

/**
 * @author Roberto E. Escobar
 */
public class OseeLevel extends Level {
   private static final long serialVersionUID = 4699966771242634396L;
   public static final Level SEVERE_POPUP = new OseeLevel("SEVERE_POPUP", SEVERE.intValue() + 100);

   protected OseeLevel(String name, int value) {
      super(name, value, "");
   }

}
