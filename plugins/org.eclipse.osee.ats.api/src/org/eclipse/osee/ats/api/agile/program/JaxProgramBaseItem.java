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
package org.eclipse.osee.ats.api.agile.program;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.eclipse.osee.ats.api.util.UpdateLocation;
import org.eclipse.osee.ats.api.util.UpdateType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public abstract class JaxProgramBaseItem {

   UpdateLocation location;
   UpdateType type;
   String title;
   @JsonSerialize(using = ToStringSerializer.class)
   Long programId;
   @JsonSerialize(using = ToStringSerializer.class)
   Long selectedId;
   XResultData results = new XResultData();
   @JsonSerialize(using = ToStringSerializer.class)
   Long newId;

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Long getSelectedId() {
      return selectedId;
   }

   public void setSelectedId(Long selectedId) {
      this.selectedId = selectedId;
   }

   public UpdateLocation getLocation() {
      return location;
   }

   public void setLocation(UpdateLocation location) {
      this.location = location;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public UpdateType getType() {
      return type;
   }

   public void setType(UpdateType type) {
      this.type = type;
   }

   public Long getNewId() {
      return newId;
   }

   public void setNewId(Long newId) {
      this.newId = newId;
   }

   public Long getProgramId() {
      return programId;
   }

   public void setProgramId(Long programId) {
      this.programId = programId;
   }

}
