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
package org.eclipse.osee.ats.ide.workflow;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkflowLabelProvider;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class WorkflowLabelProvider extends TeamWorkflowLabelProvider {

   @Override
   public String getText(Object element) {
      if (Artifacts.isOfType(element, AtsArtifactTypes.TeamWorkflow)) {
         return super.getText(element);
      }
      return element.toString();
   }

}
