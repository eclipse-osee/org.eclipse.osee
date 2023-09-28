/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript.internal;

import java.util.Date;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Stephen J. Molaro
 */
public class ScriptProgramToken extends ArtifactAccessorResult {

   public static final ScriptProgramToken SENTINEL = new ScriptProgramToken();

   private String importPath;
   private boolean active;
   private Date startDate;
   private Date endDate;

   public ScriptProgramToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ScriptProgramToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setImportPath(art.getSoleAttributeAsString(CoreAttributeTypes.ImportPath, ""));
      this.setActive(art.getSoleAttributeValue(CoreAttributeTypes.Active, false));
      this.setStartDate(art.getSoleAttributeValue(CoreAttributeTypes.StartDate, new Date()));
      this.setEndDate(art.getSoleAttributeValue(CoreAttributeTypes.EndDate, new Date()));
   }

   public ScriptProgramToken(Long id, String name) {
      super(id, name);
      this.setImportPath("");
      this.setActive(false);
      this.setStartDate(new Date());
      this.setEndDate(new Date());
   }

   public ScriptProgramToken() {
      super();
   }

   /**
    * @return the importPath
    */
   public String getImportPath() {
      return importPath;
   }

   /**
    * @param processorId the processorId to set
    */
   public void setImportPath(String importPath) {
      this.importPath = importPath;
   }

   /**
    * @return the active
    */
   public boolean getActive() {
      return active;
   }

   /**
    * @param active the active to set
    */
   public void setActive(boolean active) {
      this.active = active;
   }

   /**
    * @return the startDate
    */
   public Date getStartDate() {
      return startDate;
   }

   /**
    * @param startDate the startDate to set
    */
   public void setStartDate(Date startDate) {
      this.startDate = startDate;
   }

   /**
    * @return the endDate
    */
   public Date getEndDate() {
      return endDate;
   }

   /**
    * @param endDate the endDate to set
    */
   public void setEndDate(Date endDate) {
      this.endDate = endDate;
   }

}