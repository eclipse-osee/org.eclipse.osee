/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.world;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Backlog doesn't have Actions, so improve performance by not returning as parent
 * 
 * @author Donald G. Dunne
 */
public class BacklogContentProvider extends WorldContentProvider {

   public BacklogContentProvider(WorldXViewer WorldXViewer) {
      super(WorldXViewer);
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof Artifact) {
         try {
            if (((Artifact) element).isOfType(AtsArtifactTypes.TeamWorkflow)) {
               return null;
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      return super.getParent(element);
   }

}
