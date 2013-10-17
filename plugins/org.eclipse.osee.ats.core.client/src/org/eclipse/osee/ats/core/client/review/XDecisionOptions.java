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
package org.eclipse.osee.ats.core.client.review;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class XDecisionOptions {

   private final WeakReference<AbstractWorkflowArtifact> smaRef;
   private final IAttributeType attributeType;

   public XDecisionOptions(IAtsWorkItem workItem) throws OseeCoreException {
      this((AbstractWorkflowArtifact) AtsClientService.get().getArtifact(workItem));
   }

   public XDecisionOptions(AbstractWorkflowArtifact sma) {
      this.smaRef = new WeakReference<AbstractWorkflowArtifact>(sma);
      this.attributeType = AtsAttributeTypes.DecisionReviewOptions;
   }

   public IAttributeType getAttributeType() {
      return attributeType;
   }

   public Set<DecisionOption> getDecisionOptions() throws OseeCoreException {
      String decString = getSma().getSoleAttributeValue(getAttributeType(), "");
      return getDecisionOptions(decString);
   }

   public AbstractWorkflowArtifact getSma() throws OseeStateException {
      if (smaRef.get() == null) {
         throw new OseeStateException("Artifact has been garbage collected");
      }
      return smaRef.get();
   }

   public static Set<DecisionOption> getDecisionOptions(String decisionOptions) {
      Set<DecisionOption> decOptions = new HashSet<DecisionOption>();
      for (String decsionOpt : decisionOptions.split("[\n\r]+")) {
         DecisionOption state = new DecisionOption();
         Result result = state.setFromXml(decsionOpt);
         if (result.isFalse()) {
            OseeLog.log(Activator.class, Level.SEVERE, result.getText());
         } else {
            decOptions.add(state);
         }
      }
      return decOptions;
   }

   public DecisionOption getDecisionOption(String name) throws OseeCoreException {
      for (DecisionOption opt : getDecisionOptions()) {
         if (opt.getName().equals(name)) {
            return opt;
         }
      }
      return null;
   }

   public void validateDecisionOptions(TransitionResults results) throws OseeCoreException {
      validateDecisionOptions(results, getSma(), getSma().getSoleAttributeValue(getAttributeType(), ""));
   }

   public static void validateDecisionOptions(TransitionResults results, AbstractWorkflowArtifact awa, String decisionOptions) {
      for (String decsionOpt : decisionOptions.split("[\n\r]+")) {
         DecisionOption state = new DecisionOption();
         Result result = state.setFromXml(decsionOpt);
         if (result.isFalse()) {
            results.addResult(awa,
               new TransitionResult("Invalid Decision Option \"" + decsionOpt + "\" " + result.getText()));
         }
      }
   }

   public String toXml(Set<DecisionOption> opts) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      for (DecisionOption opt : opts) {
         sb.append(opt.toXml());
         sb.append("\n");
      }
      return sb.toString().replaceFirst("\n$", "");
   }

   public void setDecisionOptions(String decisionOptions) throws OseeCoreException {
      getSma().setSoleAttributeValue(getAttributeType(), toXml(getDecisionOptions(decisionOptions)));
   }

}
