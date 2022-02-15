/*********************************************************************
 * Copyright (c) 2015 Boeing
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

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class NewTransaction {

   private String comment;
   private String authorId;

   public String getComment() {
      return comment;
   }

   public String getAuthorId() {
      return authorId;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setAuthorId(String authorId) {
      this.authorId = authorId;
   }

}
