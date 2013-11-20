/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONArray;

/**
 * @author Angel Avila
 */
@XmlRootElement
public class Discrepancy {
   private int id;
   private String text;
   private LocationRange locationRange;
   private JSONArray idsOfCoveringAnnotations;

   public Discrepancy() {
   }

   public int getId() {
      return id;
   }

   public String getText() {
      return text;
   }

   public LocationRange getLocationRange() {
      return locationRange;
   }

   public JSONArray getIdsOfCoveringAnnotations() {
      return idsOfCoveringAnnotations;
   }

   // Setters
   public void setId(int id) {
      this.id = id;
   }

   public void setText(String text) {
      this.text = text;
   }

   public void setLocationRange(LocationRange locationRange) {
      this.locationRange = locationRange;
   }

   public void setIdsOfCoveringAnnotations(JSONArray idsOfCoveringAnnotations) {
      this.idsOfCoveringAnnotations = idsOfCoveringAnnotations;
   }

   // Utils
   public void addCoveringAnnotation(DispoAnnotationData annotation) {
      idsOfCoveringAnnotations.put(annotation.getId());
   }

}
