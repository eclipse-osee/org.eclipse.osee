/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.logger.Log;

/**
 * @author Angel Avila
 */
public class DispoResolutionValidator {

   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start() {
      logger.trace("Starting ResolutionValidator...");
   }

   public void stop() {
      logger.trace("Stopping ResolutionValidator...");
   }

   public boolean validate(DispoAnnotationData annotation) {
      String pcr = annotation.getResolution().toUpperCase().trim();
      boolean isValid = false;
      if (pcr.matches("^\\s*[a-zA-Z]{1,}\\s*\\d{1,}$")) {
         isValid = true;
      }

      return isValid;
   }
}
