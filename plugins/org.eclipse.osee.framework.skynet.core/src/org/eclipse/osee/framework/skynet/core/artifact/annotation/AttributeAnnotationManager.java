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
package org.eclipse.osee.framework.skynet.core.artifact.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * Provides access to annotations stored as the "Annotation" attribute in the specified artifact. NOTE: Annotations can
 * also be provided through IArtifactAnnotation extension point.
 * 
 * @author Donald G. Dunne
 */
public class AttributeAnnotationManager {
   private final Artifact artifact;

   public AttributeAnnotationManager(Artifact artifact) {
      this.artifact = artifact;
   }

   private Collection<Attribute<String>> getAttributes() throws OseeCoreException {
      return artifact.getAttributes(CoreAttributeTypes.Annotation);
   }

   /**
    * @return annotations stored in "Annotation" attribute of given artifact. NOTE: This is not a full list of
    * annotation for this artifact as annotations can be added via extension point.
    */
   public List<ArtifactAnnotation> getAnnotations() throws OseeCoreException {
      List<ArtifactAnnotation> annotations = new ArrayList<ArtifactAnnotation>();
      for (String value : artifact.getAttributesToStringList(CoreAttributeTypes.Annotation)) {
         ArtifactAnnotation annotation = new ArtifactAnnotation(value);
         annotations.add(annotation);
      }
      return annotations;
   }

   /**
    * Add an annotation to be stored in the "Annotation" attribute of this given artifact.
    */
   public void addAnnotation(ArtifactAnnotation newAnnotation) throws OseeCoreException {

      // Update attribute if it already exists
      for (Attribute<String> attr : getAttributes()) {
         ArtifactAnnotation annotation = new ArtifactAnnotation(attr.getValue());
         if (newAnnotation.equals(annotation)) {
            attr.setValue(newAnnotation.toXml());
            return;
         }
      }
      artifact.addAttribute(CoreAttributeTypes.Annotation, newAnnotation.toXml());
   }

   /**
    * Remove the annotation from the "Annotation" attribute of the given artifact.
    */
   public void removeAnnotation(ArtifactAnnotation annotation) throws OseeCoreException {
      // Update attribute if it already exists
      for (Attribute<String> attr : getAttributes()) {
         ArtifactAnnotation attrAnnotation = new ArtifactAnnotation(attr.getValue());
         if (annotation.equals(attrAnnotation)) {
            attr.delete();
            return;
         }
      }
   }
}