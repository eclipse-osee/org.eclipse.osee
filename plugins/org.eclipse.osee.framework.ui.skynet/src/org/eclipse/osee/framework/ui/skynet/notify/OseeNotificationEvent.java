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
package org.eclipse.osee.framework.ui.skynet.notify;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.User;

/**
 * @author Donald G. Dunne
 */
public class OseeNotificationEvent {

   private Collection<User> users;
   private final String id;
   private String type;
   private String description;
   private String url;

   public OseeNotificationEvent(Collection<User> users, String id, String type, String description) {
      this.users = users;
      this.id = id;
      this.type = type;
      this.description = description;
   }

   public OseeNotificationEvent(Collection<User> users, String id, String type, String description, String url) {
      this(users, id, type, description);
      this.url = url;
   }

   @Override
   public String toString() {
      return type + " - " + id + " - " + users + " - " + description;
   }

   public String getId() {
      return id;
   }

   public String getType() {
      return type;
   }

   public String getDescription() {
      return description;
   }

   public Collection<User> getUsers() {
      return users;
   }

   public void setUsers(Collection<User> users) {
      this.users = users;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }
}
