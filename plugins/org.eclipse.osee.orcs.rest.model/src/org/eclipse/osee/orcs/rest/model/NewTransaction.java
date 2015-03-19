/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

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
