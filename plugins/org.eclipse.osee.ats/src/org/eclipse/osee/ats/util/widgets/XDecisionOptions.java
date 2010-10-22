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
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class XDecisionOptions {

   private final WeakReference<AbstractWorkflowArtifact> smaRef;
   private final IAttributeType attributeType;

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
         if (opt.getName().equals(name)) {
            return opt;
         }
      }
      return null;
   }

   public Result validateDecisionOptions() throws OseeCoreException {
      return validateDecisionOptions(getSma().getSoleAttributeValue(getAttributeType(), ""));
   }

   public static Result validateDecisionOptions(String decisionOptions) {
      for (String decsionOpt : decisionOptions.split("[\n\r]+")) {
         DecisionOption state = new DecisionOption();
         Result result = state.setFromXml(decsionOpt);
         if (result.isFalse()) {
            return new Result("Invalid Decision Option \"" + decsionOpt + "\" " + result.getText());
         }
      }
      return Result.TrueResult;
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
