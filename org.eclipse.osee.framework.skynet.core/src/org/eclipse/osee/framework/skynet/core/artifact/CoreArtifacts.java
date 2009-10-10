/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.skynet.core.IOseeType;

/**
 * @author Ryan D. Brooks
 */
public enum CoreArtifacts implements IOseeType {
   User("User", "AAMFDhmr+Dqqe5pn3kAA"),
   AbstractSoftwareRequirement("Abstract Software Requirement", "ABNAYPwV6H4EkjQ3+QQA"),
   SoftwareRequirementDrawing("Software Requirement Drawing", "ABNClhgUfwj6A3EAArQA");

   private final String name;
   private final String guid;

   private CoreArtifacts(String name, String guid) {
      this.name = name;
      this.guid = guid;
   }

   public String getName() {
      return this.name;
   }

   public String getGuid() {
      return guid;
   }
}
