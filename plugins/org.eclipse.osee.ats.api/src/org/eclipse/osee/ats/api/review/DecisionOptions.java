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
package org.eclipse.osee.ats.api.review;

import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public class DecisionOptions {

   private final AttributeTypeToken attributeType;
   private final AtsApi atsApi;
   private final IAtsDecisionReview decRev;

   public DecisionOptions(IAtsDecisionReview decRev, AtsApi atsApi) {
      this.decRev = decRev;
      this.atsApi = atsApi;
      this.attributeType = AtsAttributeTypes.DecisionReviewOptions;
   }

   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   public Set<DecisionOption> getDecisionOptions() {
      String decString = atsApi.getAttributeResolver().getSoleAttributeValue(decRev, getAttributeType(), "");
      return getDecisionOptions(decString, atsApi);
   }

   public static Set<DecisionOption> getDecisionOptions(String decisionOptions, AtsApi atsApi) {
      Set<DecisionOption> decOptions = new LinkedHashSet<>();
      for (String decsionOpt : decisionOptions.split("[\n\r]+")) {
         DecisionOption state = new DecisionOption(atsApi);
         Result result = state.setFromXml(decsionOpt);
         if (result.isFalse()) {
            atsApi.getLogger().error(result.getText());
         } else {
            decOptions.add(state);
         }
      }
      return decOptions;
   }

   public DecisionOption getDecisionOption(String name) {
      for (DecisionOption opt : getDecisionOptions()) {
         if (opt.getName().equals(name)) {
            return opt;
         }
      }
      return null;
   }

   public void validateDecisionOptions(TransitionResults results) {
      validateDecisionOptions(results, decRev,
         atsApi.getAttributeResolver().getSoleAttributeValue(decRev, getAttributeType(), ""), atsApi);
   }

   public static void validateDecisionOptions(TransitionResults results, IAtsDecisionReview decRev, String decisionOptions, AtsApi atsApi) {
      for (String decsionOpt : decisionOptions.split("[\n\r]+")) {
         DecisionOption state = new DecisionOption(atsApi);
         Result result = state.setFromXml(decsionOpt);
         if (result.isFalse()) {
            results.addResult(decRev,
               new TransitionResult("Invalid Decision Option \"" + decsionOpt + "\" " + result.getText()));
         }
      }
   }

   public String toXml(Set<DecisionOption> opts) {
      StringBuffer sb = new StringBuffer();
      for (DecisionOption opt : opts) {
         sb.append(opt.toXml());
         sb.append("\n");
      }
      return sb.toString().replaceFirst("\n$", "");
   }

   public void setDecisionOptions(String decisionOptions) {
      atsApi.getAttributeResolver().setSoleAttributeValue(decRev, getAttributeType(),
         toXml(getDecisionOptions(decisionOptions, atsApi)));
   }

}
