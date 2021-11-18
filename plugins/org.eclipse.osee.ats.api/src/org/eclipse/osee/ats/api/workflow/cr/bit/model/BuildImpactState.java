/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow.cr.bit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class BuildImpactState extends OseeEnum {

   private static final Long ENUM_ID = 321817019823L;

   public static BuildImpactState Open = new BuildImpactState(111L, "Open");
   public static BuildImpactState Analyzed = new BuildImpactState(222L, "Analyzed");
   public static BuildImpactState InWork = new BuildImpactState(333L, "InWork");
   public static BuildImpactState Promoted = new BuildImpactState(444L, "Promoted");
   public static BuildImpactState Closed = new BuildImpactState(555L, "Closed");
   public static BuildImpactState Deferred = new BuildImpactState(666L, "Deferred");
   public static BuildImpactState Cancelled = new BuildImpactState(777L, "Cancelled");

   public BuildImpactState(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return Open;
   }

}
