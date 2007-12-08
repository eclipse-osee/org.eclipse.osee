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

package org.eclipse.osee.framework.svn.entry;

public class NullRepositoryEntry implements IRepositoryEntry {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.svn.entry.IRepositoryEntry#getVersion()
    */
   public String getVersion() {
      return "-";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.svn.entry.IRepositoryEntry#getURL()
    */
   public String getURL() {
      return "-";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.svn.entry.IRepositoryEntry#getVersionControlSystem()
    */
   public String getVersionControlSystem() {
      return "-";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.svn.entry.IRepositoryEntry#getModifiedFlag()
    */
   public String getModifiedFlag() {
      return "-";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.svn.entry.IRepositoryEntry#getLastAuthor()
    */
   public String getLastAuthor() {
      return "-";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.svn.entry.IRepositoryEntry#getLastModificationDate()
    */
   public String getLastModificationDate() {
      return "-";
   }

}
