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
import org.eclipse.osee.framework.jdk.core.type.Pair;
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

   public Pair<Boolean, String> validate(DispoAnnotationData annotation) {
      String pcr = annotation.getResolution().toUpperCase().trim();
      boolean isValid = false;
      String type = "NONE";
      if (pcr.matches("^\\s*[CTR]\\d{4,5}\\s*$")) {
         isValid = true;
         if (pcr.startsWith("C")) {
            type = "CODE";
         } else if (pcr.startsWith("T")) {
            type = "TEST";
         } else if (pcr.startsWith("R")) {
            type = "REQ";
         } else {
            type = "OTHER";
         }
      }

      boolean isOpen = checkStatus(pcr);

      Pair<Boolean, String> toReturn = new Pair<Boolean, String>(isOpen && isValid, type);
      return toReturn;
   }

   private boolean checkStatus(String pcrNumber) {
      // Default is true for now
      // Will implement ATS validation later to check status of RPCR against DB
      return true;
   }
}
