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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;

/**
 * Provides access to annotations stored as the "Annotation" attribute in the specified artifact. NOTE: Annotations can
 * also be provided through IArtifactAnnotation extension point.
 * 
 * @author Donald G. Dunne
 */
public class AttributeAnnotationManager {

   public static final String ANNOTATION_ATTRIBUTE = "Annotation";
   private final org.eclipse.osee.framework.skynet.core.artifact.Artifact artifact;

   public AttributeAnnotationManager(Artifact artifact) {
      this.artifact = artifact;
   }

   public DynamicAttributeManager getDam() throws SQLException {
      return artifact.getAttributeManager(ANNOTATION_ATTRIBUTE);
   }

   /**
    * @return annotations stored in "Annotation" attribute of given artifact. NOTE: This is not a full list of
    *         annotation for this artifact as annotations can be added via extension point.
    * @throws SQLException
    */
   public Set<ArtifactAnnotation> getAnnotations() throws SQLException {
      Set<ArtifactAnnotation> annotations = new HashSet<ArtifactAnnotation>();
      for (Attribute attr : getDam().getAttributes()) {
         ArtifactAnnotation annotation = new ArtifactAnnotation(attr.getStringData());
         annotations.add(annotation);
      }
      return annotations;
   }

   /**
    * Add an annotation to be stored in the "Annotation" attribute of this given artifact.
    * 
    * @param newAnnotation
    * @throws SQLException
    */
   public void addAnnotation(ArtifactAnnotation newAnnotation) throws SQLException {
      // Update attribute if it already exists
      for (Attribute attr : getDam().getAttributes()) {
         ArtifactAnnotation annotation = new ArtifactAnnotation(attr.getStringData());
         if (newAnnotation.equals(annotation)) {
            attr.setStringData(newAnnotation.toXml());
            return;
         }
      }
      // Else, doesn't exist yet, create
      getDam().getNewAttribute().setStringData(newAnnotation.toXml());
   }

   /**
    * Remove the annotation from the "Annotation" attribute of the given artifact.
    * 
    * @param annotation
    * @throws SQLException
    */
   public void removeAnnotation(ArtifactAnnotation annotation) throws SQLException {
      // Update attribute if it already exists
      for (Attribute attr : getDam().getAttributes()) {
         ArtifactAnnotation attrAnnotation = new ArtifactAnnotation(attr.getStringData());
         if (annotation.equals(attrAnnotation)) {
            attr.delete();
            return;
         }
      }
   }

}
