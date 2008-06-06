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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class XDecisionOptions {

   protected final StateMachineArtifact sma;

   public XDecisionOptions(StateMachineArtifact sma) {
      this.sma = sma;
   }

   public DecisionOption getDecisionOption(String stateName, boolean create) throws SQLException, MultipleAttributesExist {
      String decisionOptions =
            sma.getSoleAttributeValue(ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE.getStoreName(), "");
      for (String decsionOpt : decisionOptions.split("\n")) {
         DecisionOption state = new DecisionOption();
         state.setFromXml(decsionOpt);
         return state;
      }
      if (create) return new DecisionOption(stateName);
      return null;
   }

   public Set<DecisionOption> getDecisionOptions() throws SQLException, MultipleAttributesExist {
      String decString = sma.getSoleAttributeValue(ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE.getStoreName(), "");
      return getDecisionOptions(decString);
   }

   public Set<DecisionOption> getDecisionOptions(String decisionOptions) {
      Set<DecisionOption> decOptions = new HashSet<DecisionOption>();
      for (String decsionOpt : decisionOptions.split("\n")) {
         DecisionOption state = new DecisionOption();
         state.setFromXml(decsionOpt);
         decOptions.add(state);
      }
      return decOptions;
   }

   public DecisionOption getDecisionOption(String name) throws SQLException, MultipleAttributesExist {
      for (DecisionOption opt : getDecisionOptions()) {
         if (opt.getName().equals(name)) return opt;
      }
      return null;
   }

   public Result validateDecisionOptions() throws SQLException, MultipleAttributesExist {
      return validateDecisionOptions(sma.getSoleAttributeValue(
            ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE.getStoreName(), ""));
   }

   public static Result validateDecisionOptions(String decisionOptions) {
      for (String decsionOpt : decisionOptions.split("\n")) {
         DecisionOption state = new DecisionOption();
         Result result = state.setFromXml(decsionOpt);
         if (result.isFalse()) return new Result("Invalid Decision Option \"" + decsionOpt + "\" " + result.getText());
      }
      return Result.TrueResult;
   }

   public String toXml(Set<DecisionOption> opts) throws SQLException, MultipleAttributesExist {
      StringBuffer sb = new StringBuffer();
      for (DecisionOption opt : opts)
         sb.append(opt.toXml() + "\n");
      return sb.toString().replaceFirst("\n$", "");
   }

   public void setDecisionOptions(String decisionOptions) throws IllegalStateException, SQLException, MultipleAttributesExist {
      sma.setSoleAttributeValue(ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE.getStoreName(),
            toXml(getDecisionOptions(decisionOptions)));
   }

}
