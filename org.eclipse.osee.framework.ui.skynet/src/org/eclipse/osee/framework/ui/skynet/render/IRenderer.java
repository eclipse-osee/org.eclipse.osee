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
package org.eclipse.osee.framework.ui.skynet.render;

import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Jeff C. Phillips
 */
public interface IRenderer {

   public static final int SUBTYPE_TYPE_MATCH = 30;
   public static final int ARTIFACT_TYPE_MATCH = 20;
   public static final int DEFAULT_MATCH = 10;
   public static final int NO_MATCH = -1;

   public abstract void edit(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException;

   public abstract void edit(List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException;

   public boolean supportsEdit();

   public abstract void preview(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException;

   public abstract void preview(List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException;

   public abstract String generateHtml(Artifact artifact) throws OseeCoreException;

   public abstract String generateHtml(List<Artifact> artifacts) throws OseeCoreException;

   public boolean supportsPreview();

   public abstract void print(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException;

   public abstract void print(List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException;

   public boolean supportsPrint();

   public String compare(Artifact baseVersion, Artifact newerVersion, IProgressMonitor monitor, PresentationType presentationType, boolean show) throws OseeCoreException;

   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, boolean show) throws OseeCoreException;

   public void compareArtifacts(List<Artifact> baseArtifacts, List<Artifact> newerArtifact, IProgressMonitor monitor, Branch branch, PresentationType presentationType) throws OseeCoreException;

   public boolean supportsCompare();

   public abstract int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException;

   public abstract String getName();

   public abstract String getArtifactUrl(Artifact artifact) throws OseeCoreException;

   public abstract String getId();

   public abstract void setOptions(VariableMap options) throws OseeArgumentException;

   public abstract String getStringOption(String key) throws OseeArgumentException;

   public abstract boolean getBooleanOption(String key) throws OseeArgumentException;

   public abstract VariableMap getOptions();

   public abstract IRenderer newInstance() throws OseeCoreException;
}
