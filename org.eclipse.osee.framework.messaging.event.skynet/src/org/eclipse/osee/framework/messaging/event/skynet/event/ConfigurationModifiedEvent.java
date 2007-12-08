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
package org.eclipse.osee.framework.messaging.event.skynet.event;

/**
 * @author Robert A. Fisher
 */
public class ConfigurationModifiedEvent extends SkynetEventBase {
   private static final long serialVersionUID = 4199206743701390599L;

   /**
    * @param branchId
    * @param transactionId
    * @param author TODO
    */
   public ConfigurationModifiedEvent(int branchId, int transactionId, int author) {
      super(branchId, transactionId, author);
   }
}
