/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
