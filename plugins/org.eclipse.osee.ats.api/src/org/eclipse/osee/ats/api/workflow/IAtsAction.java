/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.team.IAtsTeamWorkflowProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsAction extends IAtsObject, IAtsTeamWorkflowProvider {
   // do nothing

   public String getAtsId();

   void setAtsId(String atsId) ;

   public static boolean isOfType(Object object) {
      return object instanceof IAtsAction;
   }
}
