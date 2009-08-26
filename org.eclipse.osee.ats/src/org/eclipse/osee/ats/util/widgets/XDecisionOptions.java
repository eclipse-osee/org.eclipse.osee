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
package org.eclipse.osee.ats.util.widgets;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class XDecisionOptions {

   private WeakReference<StateMachineArtifact> smaRef;

   public XDecisionOptions(StateMachineArtifact sma) {
      this.smaRef = new WeakReference<StateMachineArtifact>(sma);
   }

   public Set<DecisionOption> getDecisionOptions() throws OseeCoreException {
      String decString =
            getSma().getSoleAttributeValue(ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE.getStoreName(), "");
      return getDecisionOptions(decString);
   }

   public StateMachineArtifact getSma() throws OseeStateException {
      if (smaRef.get() == null) {
         throw new OseeStateException("Artifact has been garbage collected");
      }
      return smaRef.get();
   }

   public Set<DecisionOption> getDecisionOptions(String decisionOptions) {
      Set<DecisionOption> decOptions = new HashSet<DecisionOption>();
      for (String decsionOpt : decisionOptions.split("[\n\r]+")) {
         DecisionOption state = new DecisionOption();
         Result result = state.setFromXml(decsionOpt);
         if (result.isFalse()) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, result.getText());
         } else {
            decOptions.add(state);
         }
      }
      return decOptions;
   }

   public DecisionOption getDecisionOption(String name) throws OseeCoreException {
      for (DecisionOption opt : getDecisionOptions()) {
         if (opt.getName().equals(name)) return opt;
      }
      return null;
   }

   public Result validateDecisionOptions() throws OseeCoreException {
      return validateDecisionOptions(getSma().getSoleAttributeValue(
            ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE.getStoreName(), ""));
   }

   public static Result validateDecisionOptions(String decisionOptions) {
      for (String decsionOpt : decisionOptions.split("[\n\r]+")) {
         DecisionOption state = new DecisionOption();
         Result result = state.setFromXml(decsionOpt);
         if (result.isFalse()) return new Result("Invalid Decision Option \"" + decsionOpt + "\" " + result.getText());
      }
      return Result.TrueResult;
   }

   public String toXml(Set<DecisionOption> opts) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      for (DecisionOption opt : opts)
         sb.append(opt.toXml() + "\n");
      return sb.toString().replaceFirst("\n$", "");
   }

   public void setDecisionOptions(String decisionOptions) throws OseeCoreException {
      getSma().setSoleAttributeValue(ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE.getStoreName(),
            toXml(getDecisionOptions(decisionOptions)));
   }

}
