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
package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.net.URL;

/** 
 * @author Robert A. Fisher
 *
 */
public interface IJarChangeListener <T extends JarCollectionNature> {

   /**
    * Called for each addition of bundle
    * 
    * @param url
    */
   public void handleBundleAdded(URL url);

   /**
    * Called for each change of bundle
    * 
    * @param url
    */
   public void handleBundleChanged(URL url);

   /**
    * Called for each removal of bundle
    * 
    * @param url
    */
   public void handleBundleRemoved(URL url);

   /**
    * Called after all add/change/remove methods have been
    * invoked for a given delta.
    */
   public void handlePostChange();

   /**
    * Called just before a project with the nature is closed
    * @param nature
    */
   public void handleNatureClosed(T nature);
}
