/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.client.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefinition;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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
   public static final String WIDGET_ID = XStateCombo.class.getSimpleName();

   public XStateCombo() {
      super("State");
   }

   private String[] getStateNames() {
      List<String> validStates = new ArrayList<String>();
      try {
         WorkDefinition workDef = null;
         if (getArtifact() instanceof AbstractWorkflowArtifact && ((AbstractWorkflowArtifact) getArtifact()).getParentTeamWorkflow() != null) {
            workDef = ((AbstractWorkflowArtifact) getArtifact()).getParentTeamWorkflow().getWorkDefinition();
         }
         if (workDef != null) {
            for (StateDefinition state : workDef.getStates()) {
               if (!validStates.contains(state.getName())) {
                  validStates.add(state.getName());
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      Collections.sort(validStates);
      return validStates.toArray(new String[validStates.size()]);
   }

   public String getSelectedState() {
      return (String) getData();
   }

   @Override
   public void setAttributeType(Artifact artifact, IAttributeType attributeType) throws OseeCoreException {
      super.setAttributeType(artifact, AtsAttributeTypes.RelatedToState);
      setDataStrings(getStateNames());
   }
}
