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
package org.eclipse.osee.ats.api.report;

import java.util.ArrayList;
import java.util.List;

public class BuildMemoRequest {

   private String title;
   private String fromTag;
   private String toTag;
   private List<String> allowedCommitTypes = new ArrayList<>();
   private List<BuildMemoRepository> repositories = new ArrayList<>();
   private List<BuildMemoCommit> commits = new ArrayList<>();

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getFromTag() {
      return fromTag;
   }

   public void setFromTag(String fromTag) {
      this.fromTag = fromTag;
   }

   public String getToTag() {
      return toTag;
   }

   public void setToTag(String toTag) {
      this.toTag = toTag;
   }

   public List<String> getAllowedCommitTypes() {
      return allowedCommitTypes;
   }

   public void setAllowedCommitTypes(List<String> allowedCommitTypes) {
      this.allowedCommitTypes = allowedCommitTypes;
   }

   public List<BuildMemoRepository> getRepositories() {
      return repositories;
   }

   public void setRepositories(List<BuildMemoRepository> repositories) {
      this.repositories = repositories;
   }

   public List<BuildMemoCommit> getCommits() {
      return commits;
   }

   public void setCommits(List<BuildMemoCommit> commits) {
      this.commits = commits;
   }

   public static class BuildMemoRepository {
      private String name;
      private String fromTag;
      private String toTag;

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public String getFromTag() {
         return fromTag;
      }

      public void setFromTag(String fromTag) {
         this.fromTag = fromTag;
      }

      public String getToTag() {
         return toTag;
      }

      public void setToTag(String toTag) {
         this.toTag = toTag;
      }
   }

   public static class BuildMemoCommit {
      private String type;
      private String cmOrigin;
      private String id;
      private String title;
      private String message;
      private String body;
      private String repository;

      public String getType() {
         return type;
      }

      public void setType(String type) {
         this.type = type;
      }

      public String getCmOrigin() {
         return cmOrigin;
      }

      public void setCmOrigin(String cmOrigin) {
         this.cmOrigin = cmOrigin;
      }

      public String getId() {
         return id;
      }

      public void setId(String id) {
         this.id = id;
      }

      public String getTitle() {
         return title;
      }

      public void setTitle(String title) {
         this.title = title;
      }

      public String getMessage() {
         return message;
      }

      public void setMessage(String message) {
         this.message = message;
      }

      public String getBody() {
         return body;
      }

      public void setBody(String body) {
         this.body = body;
      }

      public String getRepository() {
         return repository;
      }

      public void setRepository(String repository) {
         this.repository = repository;
      }
   }
}
