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
import org.eclipse.osee.framework.ui.skynet.render.word.AttributeElement;
import org.eclipse.osee.framework.ui.skynet.render.word.Producer;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public interface IRenderer {

   public static final int WORD_PUBLICATION = 60;
   public static final int PRESENTATION_SUBTYPE_MATCH = 50;
   public static final int PRESENTATION_TYPE = 40;
   public static final int SUBTYPE_TYPE_MATCH = 30;
   public static final int ARTIFACT_TYPE_MATCH = 20;
   public static final int DEFAULT_MATCH = 10;
   public static final int NO_MATCH = -1;

   public abstract List<String> getCommandId(PresentationType presentationType);

   public Image getImage(Artifact artifact) throws OseeCoreException;

   public abstract void renderAttribute(String attributeTypeName, Artifact artifact, PresentationType presentationType, Producer producer, VariableMap map, AttributeElement attributeElement) throws OseeCoreException;

   public abstract int minimumRanking() throws OseeCoreException;

   public abstract void open(List<Artifact> artifacts) throws OseeCoreException;

   public abstract void preview(List<Artifact> artifacts) throws OseeCoreException;

   public abstract void print(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException;

   public abstract void print(List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException;

   public String compare(Artifact baseVersion, Artifact newerVersion, IProgressMonitor monitor, PresentationType presentationType, boolean show) throws OseeCoreException;

   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, boolean show) throws OseeCoreException;

   public void compareArtifacts(List<Artifact> baseArtifacts, List<Artifact> newerArtifact, IProgressMonitor monitor, Branch branch, PresentationType presentationType) throws OseeCoreException;

   public abstract int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException;

   public abstract String getName();

   public abstract void setOptions(VariableMap options) throws OseeArgumentException;

   public abstract String getStringOption(String key) throws OseeArgumentException;

   public abstract boolean getBooleanOption(String key) throws OseeArgumentException;

   public abstract VariableMap getOptions();

   public abstract IRenderer newInstance() throws OseeCoreException;
}
