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
package org.eclipse.osee.ats.util;

import java.util.Set;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.IArtifactAnnotation;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class AtsArtifactAnnotations implements IArtifactAnnotation {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.artifact.IArtifactAnnotation#getAnnotations(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   public void getAnnotations(Artifact artifact, Set<ArtifactAnnotation> annotations) {
      if (artifact instanceof StateMachineArtifact) {
         SMAManager smaMgr = new SMAManager((StateMachineArtifact) artifact);
         Result result = smaMgr.getDeadlineMgr().isDeadlineDateAlerting();
         if (result.isTrue()) annotations.add(ArtifactAnnotation.getWarning("org.eclipse.osee.ats.deadline",
               result.getText()));
      }
   }

}
