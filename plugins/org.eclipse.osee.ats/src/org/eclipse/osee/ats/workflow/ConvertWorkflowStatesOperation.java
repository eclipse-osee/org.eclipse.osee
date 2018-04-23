/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * This operation will take source workflows and convert state names. Use this BLAM when Work Definition changes require
 * state re-name or removal. This BLAM will fix old workflows. This works by reading the attributes on the workflow and
 * search/replacing the fromStateName to the toStateName. NOTE: ATS log will show name changes, but not OSEE history.";
 *
 * @author Donald G. Dunne
 */
public class ConvertWorkflowStatesOperation extends AbstractOperation {

   private final Collection<? extends AbstractWorkflowArtifact> workflows;
   private final Map<String, String> fromStateToStateMap;
   private final XResultData rd;
   private final boolean persist;
   private final Pattern pattern = Pattern.compile("^[0-9A-Za-z-_ ]+$");

   /**
    * @param fromStateToStateMap mapping of pairs of fromStateName and toStateName strings
    * @param persist true if changes are to be persisted to database
    * @param rd will contain results of changes which should be reviewed before persisting to DB
    */
   public ConvertWorkflowStatesOperation(Map<String, String> fromStateToStateMap, Collection<? extends AbstractWorkflowArtifact> workflows, boolean persist, XResultData rd) {
      super("Convert ATS Workflow States", Activator.PLUGIN_ID);
      this.fromStateToStateMap = fromStateToStateMap;
      this.workflows = workflows;
      this.persist = persist;
      this.rd = rd;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      SkynetTransaction transaction = null;
      if (persist) {
         transaction = TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), getName());
      }
      if (fromStateToStateMap.isEmpty()) {
         rd.error("Must enter FromToState pairs");
      } else if (!stateNamesAreValid(fromStateToStateMap, rd)) {
         return;
      } else if (workflows.isEmpty()) {
         rd.error("No workflows entered");
      } else {
         try {
            for (AbstractWorkflowArtifact awa : workflows) {
               convertCurrentState(awa);
               convertStates(awa);
               convertCompletedFromState(awa);
               convertCancelledFromState(awa);
               convertLogStates(awa);
               if (persist) {
                  awa.persist(transaction);
               }
            }
         } catch (OseeCoreException ex) {
            rd.error(ex.getLocalizedMessage() + " (see error log)");
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      if (transaction != null && persist) {
         transaction.execute();
      }
   }

   private boolean stateNamesAreValid(Map<String, String> fromStateToStateMap, XResultData rd) {
      boolean valid = true;
      for (Entry<String, String> fromStateNameToStateName : fromStateToStateMap.entrySet()) {
         String fromStateName = fromStateNameToStateName.getKey();
         if (!stateNameIsValid(fromStateName, rd)) {
            valid = false;
         }
         String toStateName = fromStateNameToStateName.getValue();
         if (!stateNameIsValid(toStateName, rd)) {
            valid = false;
         }
      }
      return valid;
   }

   private boolean stateNameIsValid(String stateName, XResultData rd) {
      Matcher m = pattern.matcher(stateName);
      if (!m.find()) {
         rd.errorf("State name must be alpha-numeric with dashes, spaces or underscores.  Invalid for [%s]", stateName);
         return false;
      }
      return true;
   }

   private void convertLogStates(AbstractWorkflowArtifact awa) {
      String logStr = awa.getSoleAttributeValue(AtsAttributeTypes.Log, "");
      String resultLogStr = logStr;
      for (Entry<String, String> fromToState : fromStateToStateMap.entrySet()) {
         String fromStr = fromToState.getKey();
         String toStr = fromToState.getValue();
         resultLogStr = resultLogStr.replaceAll("state=\"" + fromStr + "\"", "state=\"" + toStr + "\"");
      }
      if (!logStr.equals(resultLogStr)) {
         awa.setSoleAttributeValue(AtsAttributeTypes.Log, resultLogStr);
         rd.logf("Converted <can't display>\n", AXml.xmlToText(logStr), AXml.xmlToText(resultLogStr));
      }
   }

   @SuppressWarnings("deprecation")
   private void convertStateAttributes(AbstractWorkflowArtifact awa, AttributeTypeToken attrType) {
      for (Attribute<Object> attribute : awa.getAttributes(attrType)) {
         for (Entry<String, String> fromToState : fromStateToStateMap.entrySet()) {
            if (((String) attribute.getValue()).startsWith(fromToState.getKey() + ";")) {
               String fromStr = (String) attribute.getValue();
               String toStr = fromStr.replaceFirst(fromToState.getKey() + ";", fromToState.getValue() + ";");
               attribute.setValue(toStr);
               rd.logf("Convert [%s] \n   [%s] to \n   [%s]\n", attrType.getName(), fromStr, toStr);
            }
         }
      }
   }

   private void convertCurrentState(AbstractWorkflowArtifact awa) {
      convertStateAttributes(awa, AtsAttributeTypes.CurrentState);
   }

   private void convertStates(AbstractWorkflowArtifact awa) {
      convertStateAttributes(awa, AtsAttributeTypes.State);
   }

   private void convertCompletedFromState(AbstractWorkflowArtifact awa) {
      convertExactMatchAttributeValue(awa, AtsAttributeTypes.CompletedFromState);
   }

   private void convertCancelledFromState(AbstractWorkflowArtifact awa) {
      convertExactMatchAttributeValue(awa, AtsAttributeTypes.CancelledFromState);
   }

   private void convertExactMatchAttributeValue(AbstractWorkflowArtifact awa, AttributeTypeToken attrType) {
      @SuppressWarnings("deprecation")
      List<Attribute<Object>> attributes = awa.getAttributes(attrType);
      if (attributes != null && !attributes.isEmpty()) {
         for (Attribute<Object> attribute : attributes) {
            for (Entry<String, String> fromToState : fromStateToStateMap.entrySet()) {
               if (attribute.getValue().equals(fromToState.getKey())) {
                  String fromStr = (String) attribute.getValue();
                  String toStr = fromToState.getValue();
                  attribute.setValue(toStr);
                  rd.logf("Convert [%s] \n   [%s] to \n   [%s]\n", attrType.getName(), fromStr, toStr);
               }
            }
         }
      }
   }
}
