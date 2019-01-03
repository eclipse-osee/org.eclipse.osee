/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.demo;

/**
 * @author Donald G. Dunne
 */
public class SprintItemData {

   private String order;
   private String title;
   private String points;
   private String unPlanned;
   private String feature;
   private String createdDate;

   public SprintItemData(String order, String title, String points, String unPlanned, String feature, String createdDate) {
      this.order = order;
      this.title = title;
      this.points = points;
      this.unPlanned = unPlanned;
      this.feature = feature;
      this.createdDate = createdDate;
   }

   public String getOrder() {
      return order;
   }

   public void setOrder(String order) {
      this.order = order;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getPoints() {
      return points;
   }

   public void setPoints(String points) {
      this.points = points;
   }

   public String getUnPlanned() {
      return unPlanned;
   }

   public void setUnPlanned(String unPlanned) {
      this.unPlanned = unPlanned;
   }

   public String getFeature() {
      return feature;
   }

   public void setFeature(String feature) {
      this.feature = feature;
   }

   public String getCreatedDate() {
      return createdDate;
   }

   public void setCreatedDate(String createdDate) {
      this.createdDate = createdDate;
   }

}
