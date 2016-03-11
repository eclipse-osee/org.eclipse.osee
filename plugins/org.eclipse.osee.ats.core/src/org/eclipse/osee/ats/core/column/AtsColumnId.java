/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

/**
 * @author Donald G. Dunne
 */
public enum AtsColumnId {

   ActivityId("ats.column.activityId"),
   Team("ats.column.team"),
   Title("framework.artifact.name.Title"),
   WorkPackageName("ats.column.workPackageName"),
   WorkPackageId("ats.column.workPackageId"),
   WorkPackageType("ats.column.workPackageType"),
   WorkPackageProgram("ats.column.workPackageProgram"),
   WorkPackageGuid("ats.column.workPackageGuid");

   private final String id;

   private AtsColumnId(String id) {
      this.id = id;
   }

   public String getId() {
      return id;
   }

}
