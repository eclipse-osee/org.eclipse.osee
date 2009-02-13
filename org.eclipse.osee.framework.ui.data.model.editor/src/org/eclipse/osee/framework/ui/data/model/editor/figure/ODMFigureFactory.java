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
package org.eclipse.osee.framework.ui.data.model.editor.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMGraph;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ODMFigureFactory {

   public static IFigure createArtifactTypeFigure(ODMGraph graph, ArtifactDataType artifactType) {
      Image image = artifactType.getImage();
      ArtifactTypeFigure figure =
            new ArtifactTypeFigure(image, getDataText(artifactType), ColorConstants.white, ColorConstants.black);

      DataTypeCache cache = graph.getCache();
      String id = cache.getDataTypeSourceIds().iterator().next();
      DataTypeSource source = cache.getDataTypeSourceById(id);
      for (AttributeDataType attribute : source.getAttributesForArtifact(artifactType)) {
         figure.addAttribute(createAttributeTypeFigure(attribute));
      }
      for (RelationDataType relation : source.getRelationsForArtifact(artifactType)) {
         figure.addRelation(createRelationTypeFigure(relation));
      }
      return figure;
   }

   public static IFigure createAttributeTypeFigure(AttributeDataType attributeType) {
      return createDataTypeFigure(attributeType, ColorConstants.lightGray, ColorConstants.black);
   }

   public static IFigure createRelationTypeFigure(RelationDataType relationData) {
      return createDataTypeFigure(relationData, ColorConstants.lightGreen, ColorConstants.black);
   }

   private static IFigure createDataTypeFigure(DataType dataType, Color bgColor, Color fgColor) {
      DataTypeFigure figure = new DataTypeFigure(bgColor, fgColor);
      Panel panel = new Panel();
      panel.setLayoutManager(new GridLayout(2, false));

      Font labelFont = JFaceResources.getTextFont();
      Font textFont = JFaceResources.getDefaultFont();

      panel.add(createLabel(getNamespace(dataType), labelFont, ColorConstants.blue));
      panel.add(createLabel(dataType.getName(), textFont));

      figure.add(panel);
      return figure;
   }

   public static Label createLabel(String text, Font font) {
      return createLabel(text, font, PositionConstants.LEFT);
   }

   public static Label createLabel(String text, Font font, Color fgColor) {
      return createLabel(text, font, PositionConstants.LEFT, fgColor);
   }

   public static Label createLabel(String text, Font font, int position) {
      Label label = new Label(text);
      label.setFont(font);
      label.setTextAlignment(position);
      return label;
   }

   public static Label createLabel(String text, Font font, int position, Color fgColor) {
      Label label = new Label(text);
      label.setFont(font);
      label.setTextAlignment(position);
      label.setForegroundColor(fgColor);
      return label;
   }

   private static String getNamespace(DataType dataType) {
      String namespace = dataType.getNamespace();
      if (!Strings.isValid(namespace) || namespace.equals("null") || namespace.equals(ODMConstants.DEFAULT_NAMESPACE)) {
         namespace = "<<" + ODMConstants.DEFAULT_NAMESPACE + ">>";
      }
      return namespace;
   }

   private static String getDataText(DataType dataType) {
      return String.format("%s:%s", getNamespace(dataType), dataType.getName());
   }
}
