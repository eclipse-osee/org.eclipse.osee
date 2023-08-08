/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.api.workflow.transition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class TransitionOption extends OseeEnum {

   private static final Long ENUM_ID = 219374727L;

   // @formatter:off
   public static final TransitionOption None = new TransitionOption("None");
   public static final TransitionOption OverrideTransitionValidityCheck = new TransitionOption("OverrideTransitionValidityCheck");
   public static final TransitionOption OverrideAssigneeCheck = new TransitionOption("OverrideAssigneeCheck");
   public static final TransitionOption OverrideReload = new TransitionOption("OverrideReload");
   public static final TransitionOption OverrideWorkingBranchCheck = new TransitionOption("OverrideWorkingBranchCheck");
   public static final TransitionOption OverrideIdeTransitionCheck = new TransitionOption("OverrideIdeTransitionCheck");
   // @formatter:on

   public TransitionOption() {
      super(ENUM_ID, -1L, "");
   }

   public TransitionOption(String name) {
      super(ENUM_ID, name);
   }

   public TransitionOption(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public TransitionOption getDefault() {
      return None;
   }

}