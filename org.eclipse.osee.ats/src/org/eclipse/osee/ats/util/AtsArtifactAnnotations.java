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
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.IArtifactAnnotation;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class AtsArtifactAnnotations implements IArtifactAnnotation {

   public void getAnnotations(Artifact artifact, Set<ArtifactAnnotation> annotations) {
      try {
         if (artifact instanceof StateMachineArtifact) {
            StateMachineArtifact sma = (StateMachineArtifact) artifact;
            Result result = sma.getDeadlineMgr().isDeadlineDateAlerting();
            if (result.isTrue()) {
               annotations.add(ArtifactAnnotation.getWarning("org.eclipse.osee.ats.deadline", result.getText()));
            }
            result = sma.getDeadlineMgr().isEcdDateAlerting();
            if (result.isTrue()) {
               annotations.add(ArtifactAnnotation.getWarning("org.eclipse.osee.ats.ecd", result.getText()));
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

}
