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
package org.eclipse.osee.ats.artifact;

import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkflowLabelProvider extends ArtifactLabelProvider {

   /**
    * 
    */
   public TeamWorkflowLabelProvider() {
      super();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider#getText(java.lang.Object)
    */
   @Override
   public String getText(Object element) {
      TeamWorkFlowArtifact wf = (TeamWorkFlowArtifact) element;
      return wf.getTeamTitle();
   }

}
