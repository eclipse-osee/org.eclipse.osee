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
public class SystemInfoData {

   private String osArch;
   private String osName;
   private String osVersion;
   private String oseeVersion;
   private String oseeServerTitle;
   /**
    * @return the osArch
    */
   public String getOsArch() {
      return osArch;
   }
   /**
    * @return the osName
    */
   public String getOsName() {
      return osName;
   }
   /**
    * @return the osVersion
    */
   public String getOsVersion() {
      return osVersion;
   }
   /**
    * @return the oseeVersion
    */
   public String getOseeVersion() {
      return oseeVersion;
   }
   /**
    * @return the oseeServerTitle
    */
   public String getOseeServerTitle() {
      return oseeServerTitle;
   }
   /**
    * @param osArch
    * @param osName
    * @param osVersion
    * @param oseeVersion
    * @param oseeServerTitle
    */
   SystemInfoData(String osArch, String osName, String osVersion, String oseeVersion, String oseeServerTitle) {
      this.osArch = osArch;
      this.osName = osName;
      this.osVersion = osVersion;
      this.oseeVersion = oseeVersion;
      this.oseeServerTitle = oseeServerTitle;
   }

}
