/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.disposition.rest.internal;

import static org.eclipse.osee.disposition.model.DispoStrings.MODIFY;
import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
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
      if (type.startsWith(MODIFY)) {
         toReturn = true;
      }
      return toReturn;
   }

   private boolean isValid(DispoAnnotationData annotation) {
      String pcr = annotation.getResolution().toUpperCase().trim();
      String type = annotation.getResolutionType().toUpperCase().trim();
      if (!type.startsWith("DEACTIVATED_")) {
         if (!Strings.isValid(pcr)) {
            return false;
         }
      }

      if (isValidType(type)) {
         return isValidWorkItem(pcr);
      }

      return true;
   }

   private boolean isValidWorkItem(String pcr) {
      boolean isValid = false;
      AtsApiService atsApiService = new AtsApiService();
      try {
         Conditions.assertNotNull(atsApiService.get(), "AtsApi can't be null.");
         Collection<IAtsWorkItem> workItemsByLegacyPcrId =
            atsApiService.get().getQueryService().getWorkItemsByLegacyPcrId(filterPcr(pcr));
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
