/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.agile.program;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
