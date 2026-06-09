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
package org.eclipse.osee.ats.rest.internal.report;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class Commit {

   private final CommitType type;
   private final CmOrigin cm;
   private final String id;
   private final String title;
   private final String message;
   private final String body;

   public Commit(CommitType type, CmOrigin cm, String id, String title, String message, String body) {
      this.type = type;
      this.cm = cm;
      this.id = id;
      this.title = title;
      this.message = message;
      this.body = body;
   }

   public CommitType getType() {
      return type;
   }

   public CmOrigin getCM() {
      return cm;
   }

   public String getId() {
      return id;
   }

   public String getTitle() {
      return title;
   }

   public String getMessage() {
      return message;
   }

   public String getBody() {
      return body;
   }

   public boolean hasId() {
      return Strings.isValid(getId());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (cm == null ? 0 : cm.hashCode());
      result = prime * result + (id == null ? 0 : id.hashCode());
      result = prime * result + (body == null ? 0 : body.hashCode());
      result = prime * result + (type == null ? 0 : type.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
         return false;
      }
      Commit other = (Commit) obj;
      if (cm != other.cm || type != other.type) {
         return false;
      }
      if (id == null ? other.id != null : !id.equals(other.id)) {
         return false;
      }
      if (body == null ? other.body != null : !body.equals(other.body)) {
         return false;
      }
      return true;
   }

   public static Commit createCommit(String type, String cm, String id, String title, String message, String body) {
      return new Commit(CommitType.fromString(type), CmOrigin.fromString(cm), id, title, message, body);
   }
}
