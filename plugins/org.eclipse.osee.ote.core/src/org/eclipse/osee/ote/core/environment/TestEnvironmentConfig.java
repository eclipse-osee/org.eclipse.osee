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
package org.eclipse.osee.ote.core.environment;

import java.io.Serializable;
import java.util.Map;

public class TestEnvironmentConfig implements Serializable {
   private static final long serialVersionUID = -382800417464413074L;
   private Map<String, String> envVars;
   private String CLEARCASE_VIEW;
   private String[] jarVersions;

   public TestEnvironmentConfig(String[] jarVersions) {
      this.jarVersions = jarVersions;
   }

   public TestEnvironmentConfig(Map<String, String> envVars, String CLEARCASE_VIEW) {
      this.CLEARCASE_VIEW = CLEARCASE_VIEW;
      this.envVars = envVars;
   }

   public Map<String, String> getLibrarySearchPath() {
      return envVars;
   }

   public String getClearCaseView() {
      return CLEARCASE_VIEW;
   }

   /**
    * @return the jarVersions
    */
   public String[] getJarVersions() {
      return jarVersions;
   }

}
