/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.core.exception.OseeArgumentException;

public class OseeTrackedChangesException extends OseeArgumentException {

   /**
    * 
    */
   private static final long serialVersionUID = -4953741614901835086L;

   public OseeTrackedChangesException(String message) {
      super(message);
   }

}
