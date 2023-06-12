/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.disposition.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * @author Angel Avila
 */
@XmlRootElement
public class Discrepancy {

   private String id;
   private String text;
   private String location; //Code Line
   private Map<Integer, DispoPairAnnotation> pairAnnotations; //Used for MCDC pair coverage

   public Discrepancy() {

   }

   public String getId() {
      return id;
   }

   public String getText() {
      return text;
   }

   public String getLocation() {
      return location;
   }

   public Map<Integer, DispoPairAnnotation> getPairAnnotations() {
      return pairAnnotations;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setText(String text) {
      this.text = text;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public void setPairAnnotations(Map<Integer, DispoPairAnnotation> pairAnnotations) {
      this.pairAnnotations = pairAnnotations;
   }
}