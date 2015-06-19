/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.ats.api.config.JaxAtsObject;

/**
 * Donald G. Dunne
 */
@XmlRootElement
public class AgileItem extends JaxAtsObject {

   private String name;
   private String assignees;
   private String state;
   private int order;
   private String atsId;
   private long uuid;
   private String featureGroups;
   private String sprint;

   public AgileItem() {
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public void setName(String name) {
      this.name = name;
   }

   public String getAssignees() {
      return assignees;
   }

   public void setAssignees(String assignees) {
      this.assignees = assignees;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }

   public int getOrder() {
      return order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public String getAtsId() {
      return atsId;
   }

   public void setAtsId(String atsId) {
      this.atsId = atsId;
   }

   @Override
   public long getUuid() {
      return uuid;
   }

   @Override
   public void setUuid(long uuid) {
      this.uuid = uuid;
   }

   public String getFeatureGroups() {
      return featureGroups;
   }

   public void setFeatureGroups(String featureGroups) {
      this.featureGroups = featureGroups;
   }

   public String getSprint() {
      return sprint;
   }

   public void setSprint(String sprint) {
      this.sprint = sprint;
   }

}
