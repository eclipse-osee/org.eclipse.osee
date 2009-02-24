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

   private static final String CATEGORY_WIDGET = "Widget";
   private static final String CATEGORY_ARTIFACT = "Artifact";
   private static final String CATEGORY_ATTRIBUTE = "Attribute";
   private static final String CATEGORY_RELATION = "Relation";
   private static final String CATEGORY_DATA = " Data";
   private static final String CATEGORY_LINK = "Link";

   private PropertySourceFactory() {
      super();
   }

   public static IPropertySource getPropertySource(Object model) {
      IPropertySource toReturn = null;
      if (model instanceof ConnectionModel) {
         toReturn = new ConnectionPropertySource(CATEGORY_LINK, model);
      } else if (model instanceof AttributeDataType) {
         toReturn =
               new CompositePropertySource(new DataTypeElementPropertySource(CATEGORY_ATTRIBUTE, model),
                     new AttributePropertySource(CATEGORY_ATTRIBUTE + CATEGORY_DATA, model), new NodePropertySource(
                           CATEGORY_WIDGET, model));
      } else if (model instanceof RelationDataType) {
         toReturn =
               new CompositePropertySource(new DataTypeElementPropertySource(CATEGORY_RELATION, model),
                     new RelationPropertySource(CATEGORY_RELATION + CATEGORY_DATA, model), new NodePropertySource(
                           CATEGORY_WIDGET, model));
      } else if (model instanceof ArtifactDataType) {
         toReturn =
               new CompositePropertySource(new DataTypeElementPropertySource(CATEGORY_ARTIFACT, model),
                     new ArtifactPropertySource(CATEGORY_ARTIFACT + CATEGORY_DATA, model), new NodePropertySource(
                           CATEGORY_WIDGET, model));
      } else if (model instanceof NodeModel) {
         toReturn = new NodePropertySource(CATEGORY_WIDGET, model);
      }
      return toReturn;
   }
}
