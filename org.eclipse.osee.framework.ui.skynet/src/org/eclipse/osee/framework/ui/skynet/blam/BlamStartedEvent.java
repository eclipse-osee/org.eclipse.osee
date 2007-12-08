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
package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.Date;

/**
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 */
public class BlamStartedEvent implements IBlamEvent {

   private final Date date;

   /**
    * @param date
    */
   public BlamStartedEvent() {
      super();
      this.date = new Date();
   }

   /**
    * @return the date
    */
   public Date getDate() {
      return date;
   }
}
