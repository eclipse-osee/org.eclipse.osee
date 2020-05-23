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

package org.eclipse.osee.account.rest.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Angel Avila
 */
public class Link {
   String name;
   String url;
   String id;
   String team;
   List<String> tags = new ArrayList<>();

   public void setId(String id) {
      this.id = id;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void setTeam(String team) {
      this.team = team;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getUrl() {
      return url;
   }

   public String getTeam() {
      return team;
   }

   public List<String> getTags() {
      return tags;
   }

   public void setTags(List<String> tags) {
      this.tags = tags;
   }
}
