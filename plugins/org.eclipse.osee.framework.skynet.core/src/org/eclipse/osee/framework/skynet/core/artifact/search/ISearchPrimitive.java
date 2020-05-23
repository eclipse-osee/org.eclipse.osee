/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.artifact.search;

/**
 * @author Ryan D. Brooks
 */
public interface ISearchPrimitive {

   /**
    * Returns a string which can be used to later re-acquire the primitive in full
    * 
    * @return Return storage string
    */
   public String getStorageString();

   void addToQuery(QueryBuilderArtifact builder);

}
