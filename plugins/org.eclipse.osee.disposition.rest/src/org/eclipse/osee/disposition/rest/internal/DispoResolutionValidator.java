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

import static org.eclipse.osee.disposition.model.DispoStrings.ANALYZE_CODE;
import static org.eclipse.osee.disposition.model.DispoStrings.ANALYZE_REQT;
import static org.eclipse.osee.disposition.model.DispoStrings.ANALYZE_TEST;
import java.util.Map;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Angel Avila
 */
public class DispoResolutionValidator {

   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start(Map<String, Object> properties) {
      logger.trace("Starting ResolutionValidator...");
   }

   public void stop() {
      logger.trace("Stopping ResolutionValidator...");
   }

   public void validate(DispoAnnotationData annotation) {
      annotation.setIsResolutionValid(isValid(annotation));
      annotation.setIsAnalyze(isAnalyze(annotation));
   }

   private boolean isAnalyze(DispoAnnotationData annotation) {
      String type = annotation.getResolutionType();
      boolean toReturn = false;
      if (type.equals(ANALYZE_CODE) || type.equals(ANALYZE_TEST) || type.equals(ANALYZE_REQT)) {
         toReturn = true;
      }
      return toReturn;
   }

   private boolean isValid(DispoAnnotationData annotation) {
      String pcr = annotation.getResolution().toUpperCase().trim();
      boolean isValid = false;
      if (Strings.isValid(pcr)) {
         isValid = true;
      }

      return isValid;
   }
}
