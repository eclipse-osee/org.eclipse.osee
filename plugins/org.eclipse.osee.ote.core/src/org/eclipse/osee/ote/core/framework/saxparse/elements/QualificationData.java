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
public class QualificationData {

   private String buildId;
   private String level;

   /**
    * @param value
    * @param value2
    */
   public QualificationData(String buildId, String level) {
      this.buildId = buildId;
      this.level = level;
   }

   /**
    * @return the buildId
    */
   public String getBuildId() {
      return buildId;
   }

   /**
    * @return the level
    */
   public String getLevel() {
      return level;
   }

}
