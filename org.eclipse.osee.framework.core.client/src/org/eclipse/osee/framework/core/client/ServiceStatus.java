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

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.IHealthStatus;

/**
 * @author Roberto E. Escobar
 */
public class ServiceStatus {
   private final String name;
   private IHealthStatus healthStatus;

   public ServiceStatus(String name) {
      this.name = name;
      this.healthStatus = new BaseStatus(Level.SEVERE, "No Status Available");
   }

   public void setHealthStatus(IHealthStatus healthStatus) {
      this.healthStatus = healthStatus;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the status
    */
   public boolean isHealthOk() {
      return Level.INFO.intValue() >= healthStatus.getLevel().intValue();
   }

   /**
    * @return the details
    */
   public String getDetails() {
      return healthStatus.getMessage();
   }

   /**
    * @return the errorMessage
    */
   public String getErrorMessage() {
      Throwable throwable = healthStatus.getException();
      return throwable != null ? Lib.exceptionToString(throwable) : "No Errors";
   }

   public Throwable getError() {
      return healthStatus.getException();
   }
}
