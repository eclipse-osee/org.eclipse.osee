/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.framework.saxparse.elements;


/**
 * @author Andrew M. Finkbeiner
 *
 */
public class VersionData {

   private String name;
   private String underTest;
   private String version;
   private String versionUnit;
   
   /**
    * @param versionUnit 
    * @param version 
    * @param underTest 
    * @param name 
    */
   VersionData(String name, String underTest, String version, String versionUnit) {
      this.name = name;
      this.underTest = underTest;
      this.version = version;
      this.versionUnit = versionUnit;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the underTest
    */
   public String getUnderTest() {
      return underTest;
   }

   /**
    * @return the version
    */
   public String getVersion() {
      return version;
   }

   /**
    * @return the versionUnit
    */
   public String getVersionUnit() {
      return versionUnit;
   }

}