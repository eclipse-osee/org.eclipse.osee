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
package org.eclipse.osee.ats.ide.util;

import java.util.Set;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.IArtifactAnnotation;

/**
 * @author Donald G. Dunne
 */
public class AtsArtifactAnnotations implements IArtifactAnnotation {

   @Override
   public void getAnnotations(Artifact artifact, Set<ArtifactAnnotation> annotations) {
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
         Result result = DeadlineManager.isDeadlineDateAlerting(awa);
         if (result.isTrue()) {
            annotations.add(ArtifactAnnotation.getWarning("org.eclipse.osee.ats.ide.deadline", result.getText()));
         }
         result = DeadlineManager.isEcdDateAlerting(awa);
         if (result.isTrue()) {
            annotations.add(ArtifactAnnotation.getWarning("org.eclipse.osee.ats.ide.ecd", result.getText()));
         }
      }
   }

}
