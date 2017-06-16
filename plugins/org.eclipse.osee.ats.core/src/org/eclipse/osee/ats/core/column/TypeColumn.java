/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

/**
 * @author Donald G. Dunne
 */
public class TypeColumn extends AbstractServicesColumn {

   public TypeColumn(IAtsServices services) {
      super(services);
   }

   @Override
   public String getText(IAtsObject atsObject) {
      if (!services.getStoreService().isDeleted(atsObject)) {
         if (atsObject instanceof IAtsTeamWorkflow) {
            return ((IAtsTeamWorkflow) atsObject).getTeamDefinition().getName() + " Workflow";
         } else if (services.getAgileService().isBacklog(atsObject)) {
            return "Backlog";
         } else {
            return services.getStoreService().getArtifactType(atsObject).getName();
         }
      }
      return "(Deleted)";
   }
}