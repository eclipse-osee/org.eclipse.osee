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
package org.eclipse.osee.ote.core.environment.interfaces;

import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.ote.Configuration;
import org.eclipse.osee.ote.ConfigurationStatus;
import org.eclipse.osee.ote.OTEStatusCallback;

/**
 * An interface for 
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public interface IRuntimeLibraryManager extends XmlizableStream {

   Class<?> loadFromScriptClassLoader(String path) throws ClassNotFoundException;

   Class<?> loadFromRuntimeLibraryLoader(String path) throws ClassNotFoundException;

   boolean installed();

   boolean uninstall(OTEStatusCallback<ConfigurationStatus> callable);

   boolean install(Configuration configuration, OTEStatusCallback<ConfigurationStatus> callable);

   boolean start(OTEStatusCallback<ConfigurationStatus> callable);

   void clearJarCache();

   boolean acquireBundles(Configuration configuration, OTEStatusCallback<ConfigurationStatus> callable);

   void resetScriptLoader(Configuration configuration, String[] strings) throws Exception;

}
