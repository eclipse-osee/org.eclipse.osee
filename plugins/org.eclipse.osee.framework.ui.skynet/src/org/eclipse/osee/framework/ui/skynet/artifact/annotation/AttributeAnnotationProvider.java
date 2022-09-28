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

package org.eclipse.osee.framework.ui.skynet.artifact.annotation;

import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class AttributeAnnotationProvider implements ArtifactAnnotationProvider {

   @Override
   public void getAnnotations(Artifact artifact, Set<ArtifactAnnotation> annotations) {
      try {
         if (artifact.isAttributeTypeValid(CoreAttributeTypes.Annotation)) {
            for (String value : artifact.getAttributesToStringList(CoreAttributeTypes.Annotation)) {
               ArtifactAnnotation annotation = new ArtifactAnnotation(value);
               annotations.add(annotation);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }
}
