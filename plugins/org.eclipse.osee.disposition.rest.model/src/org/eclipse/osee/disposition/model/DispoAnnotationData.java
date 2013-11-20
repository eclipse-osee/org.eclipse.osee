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
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.json.JSONArray;

/**
 * @author Angel Avila
 */

@XmlRootElement(name = "DispoAnnotationData")
public class DispoAnnotationData implements Identifiable<String> {

   private int id;
   private JSONArray notesList;
   private String locationRefs;
   private boolean isValid;
   private JSONArray idsOfCoveredDiscrepancies;

   public DispoAnnotationData() {

   }

   public int getId() {
      return id;
   }

   public JSONArray getNotesList() {
      return notesList;
   }

   public String getLocationRefs() {
      return locationRefs;
   }

   public JSONArray getIdsOfCoveredDiscrepancies() {
      return idsOfCoveredDiscrepancies;
   }

   public boolean getIsValid() {
      return isValid;
   }

   public void setLocationRefs(String locationRefs) {
      this.locationRefs = locationRefs;
   }

   public void setNotesList(JSONArray notesList) {
      this.notesList = notesList;
   }

   public void setId(int id) {
      this.id = id;
   }

   public void setIsValid(boolean isValid) {
      this.isValid = isValid;
   }

   public void setIdsOfCoveredDiscrepancies(JSONArray idsOfCoveredDiscrepancies) {
      this.idsOfCoveredDiscrepancies = idsOfCoveredDiscrepancies;
   }

   @Override
   public String getGuid() {
      return String.valueOf(id);
   }

   @Override
   public String getName() {
      return locationRefs;
   }

   public void addCoveredDiscrepancyIndex(Discrepancy discrepancy) {
      idsOfCoveredDiscrepancies.put(discrepancy.getId());
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      for (Identity<?> identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }
}
