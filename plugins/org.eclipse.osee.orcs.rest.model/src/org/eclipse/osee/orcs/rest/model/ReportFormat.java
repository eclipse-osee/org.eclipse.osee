/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * Enumerates the supported report output formats. Each format knows its file extension and media type, keeping
 * format-specific knowledge in one place and eliminating Cartesian explosion across endpoints.
 *
 * @author David W. Miller
 */
public enum ReportFormat {

   XML("xml", "application/xml"),
   XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
   HTML("html", "text/html");

   private final String extension;
   private final String mediaType;

   ReportFormat(String extension, String mediaType) {
      this.extension = extension;
      this.mediaType = mediaType;
   }

   public String extension() {
      return extension;
   }

   public String mediaType() {
      return mediaType;
   }

   public static ReportFormat fromString(String value) {
      if (value == null || value.isBlank()) {
         return XML;
      }
      for (ReportFormat f : values()) {
         if (f.extension.equalsIgnoreCase(value.trim())) {
            return f;
         }
      }
      throw new OseeArgumentException("Unsupported report format: %s. Valid values: xml, xlsx, html", value);
   }
}
