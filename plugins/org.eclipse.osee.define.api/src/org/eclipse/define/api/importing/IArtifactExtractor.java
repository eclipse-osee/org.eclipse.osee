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

package org.eclipse.define.api.importing;

import java.io.FileFilter;
import java.net.URI;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Ryan D. Brooks
 */
public interface IArtifactExtractor {

   public abstract String getName();

   public abstract String getDescription();

   public abstract XResultData process(OrcsApi orcsApi, XResultData results, URI source, RoughArtifactCollector collector) throws Exception;

   public abstract FileFilter getFileFilter();

   public abstract boolean usesTypeList();

   public boolean isDelegateRequired();

   public void setDelegate(IArtifactExtractorDelegate delegate);

   public IArtifactExtractorDelegate getDelegate();

   public boolean hasDelegate();

   // return true if theArtifact has been modifed
   public boolean artifactCreated(TransactionBuilder transaction, ArtifactId theArtifact, RoughArtifact source);
}
