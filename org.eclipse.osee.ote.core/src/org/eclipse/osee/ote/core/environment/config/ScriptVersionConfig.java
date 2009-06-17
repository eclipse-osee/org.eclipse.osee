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
package org.eclipse.osee.ote.core.environment.config;

import java.io.Serializable;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.ote.core.test.tags.BaseTestTags;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ScriptVersionConfig implements Xmlizable, Serializable {

   private static final long serialVersionUID = -4021198751318075600L;
   private String repositoryType;
   private String location;
   private String revision;
   private String lastAuthor;
   private String lastModificationDate;
   private String modifiedFlag;

   public ScriptVersionConfig() {
      repositoryType = "UNKNOWN";
      location = "-";
      revision = "-";
      lastAuthor = "-";
      lastModificationDate = "-";
      modifiedFlag = "-";
   }
   
   public ScriptVersionConfig(String repositoryType,
    String location,
    String revision,
    String lastAuthor,
    String lastModificationDate,
    String modifiedFlag) {
	      this.repositoryType =repositoryType;
	      this.location =location;
	      this.revision = revision;
	      this.lastAuthor = lastAuthor;
	      this.lastModificationDate = lastModificationDate;
	      this.modifiedFlag = modifiedFlag;
	   }

   /**
    * @return the location
    */
   public String getLocation() {
      return location;
   }

   /**
    * @param location the location to set
    */
   public void setLocation(String location) {
      this.location = location;
   }

   /**
    * @return the repositoryType
    */
   public String getRepositoryType() {
      return repositoryType;
   }

   /**
    * @param repositoryType the repositoryType to set
    */
   public void setRepositoryType(String repositoryType) {
      this.repositoryType = repositoryType;
   }

   /**
    * @return the revision
    */
   public String getRevision() {
      return revision;
   }

   /**
    * @param revision the revision to set
    */
   public void setRevision(String revision) {
      this.revision = revision;
   }

   /**
    * @return the lastAuthor
    */
   public String getLastAuthor() {
      return lastAuthor;
   }

   /**
    * @param lastAuthor the lastAuthor to set
    */
   public void setLastAuthor(String lastAuthor) {
      this.lastAuthor = lastAuthor;
   }

   /**
    * @return the lastDateModified
    */
   public String getLastModificationDate() {
      return lastModificationDate;
   }

   /**
    * @param lastModified the lastModified to set
    */
   public void setLastModificationDate(String lastModificationDate) {
      this.lastModificationDate = lastModificationDate;
   }

   /**
    * @return the modifiedFlag
    */
   public String getModifiedFlag() {
      return modifiedFlag;
   }

   /**
    * @param modifiedFlag the modifiedFlag to set
    */
   public void setModifiedFlag(String modifiedFlag) {
      this.modifiedFlag = modifiedFlag;
   }

   public Element toXml(Document doc) {
      Element scriptVersion = doc.createElement(BaseTestTags.SCRIPT_VERSION);
      scriptVersion.setAttribute(BaseTestTags.REVISION_FIELD, getRevision());
      scriptVersion.setAttribute(BaseTestTags.REPOSITORY_TYPE, getRepositoryType());
      scriptVersion.setAttribute(BaseTestTags.LAST_AUTHOR_FIELD, getLastAuthor());
      scriptVersion.setAttribute(BaseTestTags.LAST_MODIFIED, getLastModificationDate());
      scriptVersion.setAttribute(BaseTestTags.MODIFIED_FIELD, getModifiedFlag());
      scriptVersion.setAttribute(BaseTestTags.URL, getLocation());
      return scriptVersion;
   }

}
