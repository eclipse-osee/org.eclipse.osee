/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs.server.internal;

import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class JaxRsConfiguration {

   private String baseContext;

   private JaxRsConfiguration() {
      //Builder class
   }

   public String getBaseContext() {
      return baseContext;
   }

   public static JaxRsConfigurationBuilder newBuilder() {
      return new JaxRsConfigurationBuilder();
   }

   public static JaxRsConfigurationBuilder fromProperties(Map<String, Object> props) {
      return newBuilder().properties(props);
   }

   @Override
   public String toString() {
      return "JaxRsConfiguration [baseContext=" + baseContext + "]";
   }

   public static final class JaxRsConfigurationBuilder {
      private String baseContext;

      public JaxRsConfiguration build() {
         JaxRsConfiguration config = new JaxRsConfiguration();
         config.baseContext = baseContext;
         return config;
      }

      public JaxRsConfigurationBuilder properties(Map<String, Object> props) {
         baseContext(
            JaxRsUtils.get(props, JaxRsConstants.JAXRS_BASE_CONTEXT, JaxRsConstants.DEFAULT_JAXRS_BASE_CONTEXT));
         return this;
      }

      public JaxRsConfigurationBuilder baseContext(String contextName) {
         this.baseContext = JaxRsUtils.normalize(contextName);
         return this;
      }

   }
}
