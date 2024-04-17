/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.coverage.internal;

import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Stephen J. Molaro
 */
public class PartitionChartDataToken extends ArtifactAccessorResult {

   public static final PartitionChartDataToken SENTINEL = new PartitionChartDataToken();

   private boolean active = true;
   private String errors = "";

   public PartitionChartDataToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public PartitionChartDataToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setActive(art.getSoleAttributeValue(CoreAttributeTypes.Active, false));
      this.setErrors("");
   }

   public PartitionChartDataToken(Long id, String name) {
      super(id, name);
      this.setActive(false);
      this.setErrors("");
   }

   public PartitionChartDataToken(Exception ex) {
      super();
      this.setErrors(ex.toString());
   }

   public PartitionChartDataToken() {
      super();
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
    * @return get the stack trace errors
    */
   public String getErrors() {
      return errors;
   }

   /**
    * @param set the errors to errors
    */
   public void setErrors(String errors) {
      this.errors = errors;
   }
}