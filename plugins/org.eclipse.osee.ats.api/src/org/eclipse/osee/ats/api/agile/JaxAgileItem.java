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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class JaxAgileItem {

   private final List<Long> uuids = new ArrayList<Long>();
   private final List<Long> features = new ArrayList<Long>();
   private long sprintUuid = 0;
   private long backlogUuid = 0;
   private boolean setFeatures = false;
   private boolean setSprint = false;
   private boolean setBacklog = false;

   public List<Long> getFeatures() {
      return features;
   }

   public long getSprintUuid() {
      return sprintUuid;
   }

   public void setSprintUuid(long sprintUuid) {
      this.sprintUuid = sprintUuid;
   }

   public List<Long> getUuids() {
      return uuids;
   }

   public boolean isSetFeatures() {
      return setFeatures;
   }

   public void setSetFeatures(boolean setFeatures) {
      this.setFeatures = setFeatures;
   }

   public boolean isSetSprint() {
      return setSprint;
   }

   public void setSetSprint(boolean setSprint) {
      this.setSprint = setSprint;
   }

   public boolean isSetBacklog() {
      return setBacklog;
   }

   public void setSetBacklog(boolean setBacklog) {
      this.setBacklog = setBacklog;
   }

   public long getBacklogUuid() {
      return backlogUuid;
   }

   public void setBacklogUuid(long backlogUuid) {
      this.backlogUuid = backlogUuid;
   }

}
