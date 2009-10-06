/*
 * Created on Oct 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.skynet.core.IOseeType;

/**
 * @author Ryan D. Brooks
 */
public enum CoreArtifacts implements IOseeType {
   AbstractSoftwareRequirement("Abstract Software Requirement", "ABNAYPwV6H4EkjQ3+QQA"), SoftwareRequirementDrawing(
         "Software Requirement Drawing", "ABNClhgUfwj6A3EAArQA");

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
