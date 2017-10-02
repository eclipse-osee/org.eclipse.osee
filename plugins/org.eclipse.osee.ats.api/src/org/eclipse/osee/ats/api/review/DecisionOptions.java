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
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class DecisionOptions {

   private final AttributeTypeId attributeType;
   private final IAtsServices services;
   private final IAtsDecisionReview decRev;

   public DecisionOptions(IAtsDecisionReview decRev, IAtsServices services)  {
      this.decRev = decRev;
      this.services = services;
      this.attributeType = AtsAttributeTypes.DecisionReviewOptions;
   }

   public AttributeTypeId getAttributeType() {
      return attributeType;
   }

   public Set<DecisionOption> getDecisionOptions()  {
      String decString = services.getAttributeResolver().getSoleAttributeValue(decRev, getAttributeType(), "");
      return getDecisionOptions(decString, services);
   }

   public static Set<DecisionOption> getDecisionOptions(String decisionOptions, IAtsServices services) {
      Set<DecisionOption> decOptions = new LinkedHashSet<>();
      for (String decsionOpt : decisionOptions.split("[\n\r]+")) {
         DecisionOption state = new DecisionOption(services);
         Result result = state.setFromXml(decsionOpt);
         if (result.isFalse()) {
            services.getLogger().error(result.getText());
         } else {
            decOptions.add(state);
         }
      }
      return decOptions;
   }

   public DecisionOption getDecisionOption(String name)  {
      for (DecisionOption opt : getDecisionOptions()) {
         if (opt.getName().equals(name)) {
            return opt;
         }
      }
      return null;
   }

   public void validateDecisionOptions(TransitionResults results)  {
      validateDecisionOptions(results, decRev,
         services.getAttributeResolver().getSoleAttributeValue(decRev, getAttributeType(), ""), services);
   }

   public static void validateDecisionOptions(TransitionResults results, IAtsDecisionReview decRev, String decisionOptions, IAtsServices services) {
      for (String decsionOpt : decisionOptions.split("[\n\r]+")) {
         DecisionOption state = new DecisionOption(services);
         Result result = state.setFromXml(decsionOpt);
         if (result.isFalse()) {
            results.addResult(decRev,
               new TransitionResult("Invalid Decision Option \"" + decsionOpt + "\" " + result.getText()));
         }
      }
   }

   public String toXml(Set<DecisionOption> opts)  {
      StringBuffer sb = new StringBuffer();
      for (DecisionOption opt : opts) {
         sb.append(opt.toXml());
         sb.append("\n");
      }
      return sb.toString().replaceFirst("\n$", "");
   }

   public void setDecisionOptions(String decisionOptions)  {
      services.getAttributeResolver().setSoleAttributeValue(decRev, getAttributeType(),
         toXml(getDecisionOptions(decisionOptions, services)));
   }

}
