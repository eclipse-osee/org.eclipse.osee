/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo;

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
