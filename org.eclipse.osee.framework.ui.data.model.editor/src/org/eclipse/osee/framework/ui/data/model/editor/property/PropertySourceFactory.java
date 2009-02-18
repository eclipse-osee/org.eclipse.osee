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
package org.eclipse.osee.framework.ui.data.model.editor.property;

import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.NodeModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.plugin.views.property.CompositePropertySource;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Roberto E. Escobar
 */
public class PropertySourceFactory {

   private static final String CATEGORY_DIAGRAM = "Diagram";
   private static final String CATEGORY_ARTIFACT = "Artifact Model";
   private static final String CATEGORY_ATTRIBUTE = "Attribute Model";
   private static final String CATEGORY_RELATION = "Relation Model";

   private PropertySourceFactory() {
      super();
   }

   public static IPropertySource getPropertySource(Object model) {
      if (model instanceof ConnectionModel) {
         return new ConnectionPropertySource(CATEGORY_DIAGRAM, model);
      }
      if (model instanceof AttributeDataType) {
         return new CompositePropertySource(new AttributePropertySource(CATEGORY_ATTRIBUTE, model),
               new NodePropertySource(CATEGORY_DIAGRAM, model));
      }
      if (model instanceof RelationDataType) {
         return new CompositePropertySource(new RelationPropertySource(CATEGORY_RELATION, model),
               new NodePropertySource(CATEGORY_DIAGRAM, model));
      }
      if (model instanceof ArtifactDataType) {
         return new CompositePropertySource(new ArtifactPropertySource(CATEGORY_ARTIFACT, model),
               new NodePropertySource(CATEGORY_DIAGRAM, model));
      }
      if (model instanceof NodeModel) {
         return new NodePropertySource(CATEGORY_DIAGRAM, model);
      }
      return null;
   }
}
