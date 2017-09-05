/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util.chart;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class ChartJsDataset {

   String label;
   String fill;
   String fillColor;
   String pointColor;
   String strokeColor;
   String pointStrokeColor;
   String pointHighlightFill;
   String pointHighlightStroke;
   String backgroundColor;
   String borderColor;
   List<String> data = new LinkedList<>();

   public String getFill() {
      return fill;
   }

   public void setFill(String fill) {
      this.fill = fill;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public String getPointColor() {
      return pointColor;
   }

   public void setPointColor(String pointColor) {
      this.pointColor = pointColor;
   }

   public String getPointStrokeColor() {
      return pointStrokeColor;
   }

   public void setPointStrokeColor(String pointStrokeColor) {
      this.pointStrokeColor = pointStrokeColor;
   }

   public String getPointHighlightFill() {
      return pointHighlightFill;
   }

   public void setPointHighlightFill(String pointHighlightFill) {
      this.pointHighlightFill = pointHighlightFill;
   }

   public String getPointHighlightStroke() {
      return pointHighlightStroke;
   }

   public void setPointHighlightStroke(String pointHighlightStroke) {
      this.pointHighlightStroke = pointHighlightStroke;
   }

   public List<String> getData() {
      return data;
   }

   public void setData(List<String> data) {
      this.data = data;
   }

   public String getStrokeColor() {
      return strokeColor;
   }

   public void setStrokeColor(String strokeColor) {
      this.strokeColor = strokeColor;
   }

   public String getFillColor() {
      return fillColor;
   }

   public void setFillColor(String fillColor) {
      this.fillColor = fillColor;
   }

   public String getBackgroundColor() {
      return backgroundColor;
   }

   public void setBackgroundColor(String backgroundColor) {
      this.backgroundColor = backgroundColor;
   }

   public String getBorderColor() {
      return borderColor;
   }

   public void setBorderColor(String borderColor) {
      this.borderColor = borderColor;
   }

}
