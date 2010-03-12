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
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.io.FileFilter;
import java.net.URI;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;

/**
 * @author Ryan D. Brooks
 */
public interface IArtifactExtractor {

   public abstract String getName();

   public abstract String getDescription();

   public abstract void process(URI source, RoughArtifactCollector collector) throws Exception;

   public abstract FileFilter getFileFilter();

   public abstract boolean usesTypeList();

   public boolean isDelegateRequired();

   public void setDelegate(IArtifactExtractorDelegate delegate);

   public IArtifactExtractorDelegate getDelegate();

   public boolean hasDelegate();
}
