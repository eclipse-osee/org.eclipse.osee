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
package org.eclipse.osee.ats.rest.internal.build.report.model;

/**
 * @author Megumi Telles
 */
public class AtsBuildData {

   private String buildName;
   private String buildId;
   private String buildProgramId;

   public AtsBuildData() {
   }

   public String getBuildName() {
      return buildName;
   }

   public void setBuildName(String buildName) {
      this.buildName = buildName;
   }

   public String getBuildId() {
      return buildId;
   }

   public void setBuildId(String buildId) {
      this.buildId = buildId;
   }

   public String getBuildProgramId() {
      return buildProgramId;
   }

   public void setBuildProgramId(String buildProgramId) {
      this.buildProgramId = buildProgramId;
   }

}
