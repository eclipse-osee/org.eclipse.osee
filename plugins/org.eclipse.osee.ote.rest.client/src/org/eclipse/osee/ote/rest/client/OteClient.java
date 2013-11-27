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

import org.eclipse.osee.ote.rest.model.OTEConfiguration;
import org.eclipse.osee.ote.rest.model.OTETestRun;


/**
 * @author Andrew Finkbeiner
 */
public interface OteClient {
   Future<Progress> getFile(URI uri, File destination, String filePath, final Progress progress);
   Future<Progress> configureServerEnvironment(URI uri, List<File> jars, final Progress progress);
   Future<Progress> configureServerEnvironment(URI uri, OTEConfiguration configuration, final Progress progress);
   Future<Progress> updateServerJarCache(URI uri, String baseJarURL, List<OTECacheItem> jars, final Progress progress);
   Future<ProgressWithCancel> runTest(URI uri, OTETestRun tests, Progress progress);
}
