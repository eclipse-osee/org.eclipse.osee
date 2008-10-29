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
package org.eclipse.osee.framework.core.client;

/**
 * @author Roberto E. Escobar
 */
public class ServiceInfo {

   private String name;
   private String details;
   private String errorMessage;
   private boolean status;

   public ServiceInfo(String details, String name, String errorMessage, boolean status) {
      super();
      this.details = details;
      this.name = name;
      this.status = status;
      this.errorMessage = errorMessage;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the details
    */
   public String getDetails() {
      return details;
   }

   /**
    * @return the status
    */
   public boolean isAvailable() {
      return status;
   }

   /**
    * @return the errorMessage
    */
   public String getErrorMessage() {
      return errorMessage;
   }
}
