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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.util.Date;

/**
 * @author Ryan D. Brooks
 */
public class SimpleDateAttribute extends DateAttribute {

   /**
    * @param name
    */
   public SimpleDateAttribute(String name) {
      super(new VarcharMediaResolver(), name);
   }

   /**
    * @param name
    * @param value
    */
   public SimpleDateAttribute(String name, Date value) {
      super(new VarcharMediaResolver(), name, value);
   }
}
