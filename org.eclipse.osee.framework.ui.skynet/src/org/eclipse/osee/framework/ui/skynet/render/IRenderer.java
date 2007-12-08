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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public interface IRenderer {

   public static final int SUBTYPE_TYPE_MATCH = 30;
   public static final int ARTIFACT_TYPE_MATCH = 20;
   public static final int DEFAULT_MATCH = 10;
   public static final int NO_MATCH = -1;

   public abstract void edit(Artifact artifact, String option, IProgressMonitor monitor) throws Exception;

   public abstract void edit(List<Artifact> artifacts, String option, IProgressMonitor monitor) throws Exception;

   public List<String> getEditOptions() throws Exception;

   public boolean supportsEdit();

   public abstract void preview(Artifact artifact, String option, IProgressMonitor monitor) throws Exception;

   public abstract void preview(List<Artifact> artifacts, String option, IProgressMonitor monitor) throws Exception;

   public List<String> getPreviewOptions() throws Exception;

   public boolean supportsPreview();

   public abstract void print(Artifact artifact, String option, IProgressMonitor monitor) throws Exception;

   public abstract void print(List<Artifact> artifacts, String option, IProgressMonitor monitor) throws Exception;

   public List<String> getPrintOptions() throws Exception;

   public boolean supportsPrint();

   public void compare(Artifact baseVersion, Artifact newerVersion, String option, IProgressMonitor monitor) throws Exception;

   public List<String> getCompareOptions() throws Exception;

   public boolean supportsCompare();

   public abstract int getApplicabilityRating(PresentationType presentationType, Artifact artifact);

   public abstract String getName();

   public abstract String getArtifactUrl(Artifact artifact);

   public abstract void setId(String rendererId);

   public abstract String getId();

}
