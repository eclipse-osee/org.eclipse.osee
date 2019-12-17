/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task.create;

/**
 * @author Donald G. Dunne
 */
public class ArtifactIncluded {

   private boolean included;
   private boolean deleted;

   public ArtifactIncluded() {
      // for jax-rs
   }

   public ArtifactIncluded(boolean included, boolean deleted) {
      this.included = included;
      this.deleted = deleted;
   }

   public boolean isIncluded() {
      return included;
   }

   public boolean isNotIncluded() {
      return !included;
   }

   public void setIncluded(boolean included) {
      this.included = included;
   }

   public boolean isDeleted() {
      return deleted;
   }

   public boolean isNotDeleted() {
      return !deleted;
   }

   public void setDeleted(boolean deleted) {
      this.deleted = deleted;
   }

}
