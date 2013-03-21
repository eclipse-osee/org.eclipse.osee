/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.rest.client;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Future;

import org.eclipse.core.runtime.jobs.Job;


/**
 * @author Andrew Finkbeiner
 */
public interface OteClient {
   
   Job configureServerEnvironment(URI uri, List<File> jars, final ConfigurationStatusCallback callback) ;

   Future<GetFileProgress> getFile(URI uri, File destination, String filePath, final GetFileProgress progress);
   Future<ConfigurationProgress> configureServerEnvironment(URI uri, List<File> jars, final ConfigurationProgress progress);
}
