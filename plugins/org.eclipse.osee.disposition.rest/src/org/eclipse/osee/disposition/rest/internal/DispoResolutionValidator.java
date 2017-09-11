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
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Angel Avila
 */
public class DispoResolutionValidator {

   private static final String REGEX_1 = "\\(.*\\)?";
   private static final String REGEX_2 = "TRAX|MPD|TPCR|RPCR";

   enum types {
      CODE,
      TEST,
      REQUIREMENT
   }

   private Log logger;
   private IAtsServer atsServer;

   private IAtsServer getAtsServer() {
      return atsServer;
   }

   public void setAtsServer(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

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
      if (!Strings.isValid(pcr)) {
         return false;
      }
      String type = annotation.getResolutionType().toUpperCase().trim();
      if (isValidType(type)) {
         return isValidWorkItem(pcr);
      }
      return true;
   }

   private boolean isValidWorkItem(String pcr) {
      boolean isValid = false;
      try {
         String workItemsByLegacyPcrId = getAtsServer().getActionEndpoint().getActionState(filterPcr(pcr));
         if (workItemsByLegacyPcrId != null && !workItemsByLegacyPcrId.isEmpty()) {
            isValid = true;
         }
      } catch (Exception ex) {
         logger.error("Error validating DispoAnnotationData [%s]", ex.getMessage());
      }
      return isValid;
   }

   private boolean isValidType(String type) {
      return types.CODE.name().equals(type) || //
         types.REQUIREMENT.name().equals(type) || //
         types.TEST.name().equals(type);
   }

   private String filterPcr(String pcr) {
      String pcrNum = pcr;
      pcrNum = pcrNum.replaceAll(REGEX_2, "");
      pcrNum = pcrNum.replaceAll(REGEX_1, "");
      return pcrNum.trim();
   }

}
