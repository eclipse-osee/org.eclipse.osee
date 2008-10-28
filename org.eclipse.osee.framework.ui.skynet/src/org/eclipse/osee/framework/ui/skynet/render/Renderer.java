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

import java.util.HashMap;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.httpRequests.ArtifactRequest;

/**
 * A Renderer is a stateless class responsible for rendering formatted content to either a file or a composite
 * 
 * @author Ryan D. Brooks
 */
public abstract class Renderer implements IRenderer {
   private String rendererId;
   private final HashMap<String, String> options = new HashMap<String, String>();

   /**
    * @param rendererId
    */
   public Renderer(String rendererId) {
      super();
      this.rendererId = rendererId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#edit(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void edit(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#edit(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void edit(List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         edit(artifact, monitor);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getName()
    */
   public String getName() {
      return getClass().getSimpleName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#preview(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void preview(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#preview(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void preview(List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         preview(artifact, monitor);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#print(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void print(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#print(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void print(List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         print(artifact, monitor);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#supportsEdit()
    */
   public boolean supportsEdit() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#supportsPreview()
    */
   public boolean supportsPreview() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#supportsPrint()
    */
   public boolean supportsPrint() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#generateHtml(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public String generateHtml(Artifact artifact) throws OseeCoreException {
      return "<b>" + artifact.getDescriptiveName() + " - " + artifact.getHumanReadableId() + "</b>";
   }

   @Override
   public String generateHtml(List<Artifact> artifacts) throws OseeCoreException {
      StringBuilder result = new StringBuilder();
      for (Artifact artifact : artifacts) {
         result.append(generateHtml(artifact));
      }
      return result.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#compare(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IProgressMonitor monitor, PresentationType presentationType, boolean show) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, boolean show) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#compareArtifacts(java.util.List, java.util.List, org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.ui.skynet.render.PresentationType)
    */
   @Override
   public void compareArtifacts(List<Artifact> baseArtifacts, List<Artifact> newerArtifacts, IProgressMonitor monitor, Branch branch, PresentationType presentationType) throws OseeCoreException {
      for (int i = 0; i < baseArtifacts.size(); i++) {
         compare(baseArtifacts.get(i), newerArtifacts.get(i), monitor, presentationType, true);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#supportsCompare()
    */
   public boolean supportsCompare() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getId()
    */
   public String getId() {
      return rendererId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#setId(java.lang.String)
    */
   public void setId(String rendererId) {
      this.rendererId = rendererId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getArtifactUrl(org.eclipse.osee.framework.skynet.core.artifact.Artifact, boolean)
    */
   public String getArtifactUrl(Artifact artifact) throws OseeCoreException {
      return ArtifactRequest.getInstance().getUrl(artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#setRendererOptions(java.lang.String[])
    */
   @Override
   public void setOptions(String... optionArgs) {
      for (int i = 0; i < optionArgs.length; i += 2) {
         options.put(optionArgs[i], optionArgs[i + 1]);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getOption(java.lang.String[])
    */
   @Override
   public String getOption(String key) {
      return options.get(key);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getBooleanOption(java.lang.String)
    */
   @Override
   public boolean getBooleanOption(String key) {
      String value = getOption(key);
      if (value == null) {
         return false;
      }
      return Boolean.parseBoolean(value);
   }
}