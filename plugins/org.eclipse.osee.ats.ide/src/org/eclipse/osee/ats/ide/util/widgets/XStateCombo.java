/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;

/**
 * Provides combo of all valid states for a certain Task or Team Workflow. Valid states will be determined off
 * WorkDefinition of parent team workflow when Widget setArtifact is called.
 *
 * @author Donald G. Dunne
 */
public class XStateCombo extends XComboDam {

   public XStateCombo() {
      super("");
   }

   private String[] getStateNames() {
      List<String> validStates = new ArrayList<>();
      try {
         IAtsWorkDefinition workDef = null;
         if (getArtifact() instanceof AbstractWorkflowArtifact && ((AbstractWorkflowArtifact) getArtifact()).getParentTeamWorkflow() != null) {
            workDef = ((AbstractWorkflowArtifact) getArtifact()).getParentTeamWorkflow().getWorkDefinition();
         }
         if (workDef != null) {
            for (String stateName : AtsApiService.get().getWorkDefinitionService().getStateNames(workDef)) {
               if (!validStates.contains(stateName)) {
                  validStates.add(stateName);
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      Collections.sort(validStates);
      return validStates.toArray(new String[validStates.size()]);
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      super.setAttributeType(artifact, AtsAttributeTypes.RelatedToState);
      setDataStrings(getStateNames());
   }
}
