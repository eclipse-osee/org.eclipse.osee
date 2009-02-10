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

   private final Collection<User> users;
   private final String id;
   private final String type;
   private final String description;

   public OseeNotificationEvent(Collection<User> users, String id, String type, String description) {
      this.users = users;
      this.id = id;
      this.type = type;
      this.description = description;
   }

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
}
