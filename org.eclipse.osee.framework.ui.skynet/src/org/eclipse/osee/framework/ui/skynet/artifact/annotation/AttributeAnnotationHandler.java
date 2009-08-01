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
package org.eclipse.osee.framework.ui.skynet.artifact.annotation;

import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.AttributeAnnotationManager;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.IArtifactAnnotation;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Donald G. Dunne
 */
public class AttributeAnnotationHandler implements IArtifactAnnotation {

   public void getAnnotations(Artifact artifact, Set<ArtifactAnnotation> annotations) {
      try {
         if (artifact.isAttributeTypeValid(AttributeAnnotationManager.ANNOTATION_ATTRIBUTE)) {
            AttributeAnnotationManager mgr = new AttributeAnnotationManager(artifact);
            annotations.addAll(mgr.getAnnotations());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }
}
