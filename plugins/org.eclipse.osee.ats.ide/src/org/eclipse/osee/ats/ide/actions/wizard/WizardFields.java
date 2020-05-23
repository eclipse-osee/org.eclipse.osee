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

package org.eclipse.osee.ats.ide.actions.wizard;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public enum WizardFields {
   Originator,
   Assignees,
   PointsNumeric("Points Numeric"),
   WorkPackage("Work Package"),
   Sprint,
   TargetedVersion("Targeted Version"),
   CreateBranch("Create Branch"),
   Points,
   UnPlannedWork("Unplanned Work"),
   FeatureGroup("Feature Group");

   private final String displayName;

   private WizardFields() {
      this(null);
   }

   private WizardFields(String displayName) {
      this.displayName = displayName;
   }

   public String getDisplayName() {
      return Strings.isValid(displayName) ? displayName : name();
   }
}
