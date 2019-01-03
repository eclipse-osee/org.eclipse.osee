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
