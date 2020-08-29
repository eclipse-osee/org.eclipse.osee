/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.disposition.rest;

import java.util.Map;
import org.eclipse.osee.framework.core.enums.DispoOseeTypes;

/**
 * @author Dominic A. Guss
 */
public class DispoApiConfiguration {

   private String fileExtRegex;
   private String resultsFileExtRegex;

   private DispoApiConfiguration() {
      //Builder Class
   }

   public String getFileExtRegex() {
      return fileExtRegex;
   }

   public void setFileExtRegex(String fileExtRegex) {
      this.fileExtRegex = fileExtRegex;
   }

   public String getResultsFileExtRegex() {
      return resultsFileExtRegex;
   }

   public void setResultsFileExtRegex(String resultsFileExtRegex) {
      this.resultsFileExtRegex = resultsFileExtRegex;
   }

   public DispoApiConfiguration copy() {
      DispoApiConfiguration data = new DispoApiConfiguration();
      data.fileExtRegex = this.fileExtRegex;
      data.resultsFileExtRegex = this.resultsFileExtRegex;
      return data;
   }

   public static DispoApiConfigurationBuilder newBuilder() {
      return new DispoApiConfigurationBuilder();
   }

   public static DispoApiConfigurationBuilder fromProperties(Map<String, Object> props) {
      return newBuilder().properties(props);
   }

   public static DispoApiConfiguration newConfig(Map<String, Object> props) {
      return fromProperties(props).build();
   }

   public static final class DispoApiConfigurationBuilder {
      private final DispoApiConfiguration config = new DispoApiConfiguration();

      public DispoApiConfiguration build() {
         return config.copy();
      }

      public DispoApiConfigurationBuilder properties(Map<String, Object> props) {
         fileExtRegex(get(props, DispoOseeTypes.FILE_EXT_REGEX, ""));
         resultsFileExtRegex(get(props, DispoOseeTypes.RESULTS_FILE_EXT_REGEX, ""));
         return this;
      }

      public DispoApiConfigurationBuilder fileExtRegex(String fileExtRegex) {
         config.setFileExtRegex(fileExtRegex);
         return this;
      }

      public DispoApiConfigurationBuilder resultsFileExtRegex(String resultsFileExtRegex) {
         config.setResultsFileExtRegex(resultsFileExtRegex);
         return this;
      }

      private static String get(Map<String, Object> props, String key, String defaultValue) {
         String toReturn = defaultValue;
         Object object = props != null ? props.get(key) : null;
         if (object != null) {
            toReturn = String.valueOf(object);
         }
         return toReturn;
      }

   }

}
