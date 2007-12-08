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
import org.eclipse.osee.framework.ui.skynet.httpRequests.ArtifactRequest;

/**
 * A Renderer is a stateless class responsible for rendering formatted content to either a file or a composite
 * 
 * @author Ryan D. Brooks
 */
public abstract class Renderer implements IRenderer {
   private String rendererId;

   /**
    * @param applicableArtifactTypes
    */
   public Renderer() {
      super();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#edit(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void edit(Artifact artifact, String option, IProgressMonitor monitor) throws Exception {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#edit(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void edit(List<Artifact> artifacts, String option, IProgressMonitor monitor) throws Exception {
      for (Artifact artifact : artifacts) {
         edit(artifact, option, monitor);
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
   public void preview(Artifact artifact, String option, IProgressMonitor monitor) throws Exception {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#preview(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void preview(List<Artifact> artifacts, String option, IProgressMonitor monitor) throws Exception {
      for (Artifact artifact : artifacts) {
         preview(artifact, option, monitor);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#print(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void print(Artifact artifact, String option, IProgressMonitor monitor) throws Exception {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#print(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void print(List<Artifact> artifacts, String option, IProgressMonitor monitor) throws Exception {
      for (Artifact artifact : artifacts) {
         print(artifact, option, monitor);
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
   public String generateHtml(Artifact artifact, IProgressMonitor monitor) {
      return "<b>" + artifact.getDescriptiveName() + " - " + artifact.getHumanReadableId() + "</b>";
   }

   public String generateHtml(List<Artifact> artifacts, IProgressMonitor monitor) throws Exception {
      StringBuilder result = new StringBuilder();
      for (Artifact artifact : artifacts) {
         result.append(generateHtml(artifact, monitor));
      }
      return result.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getEditOptions()
    */
   public List<String> getEditOptions() throws Exception {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getPreviewOptions()
    */
   public List<String> getPreviewOptions() throws Exception {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getPrintOptions()
    */
   public List<String> getPrintOptions() throws Exception {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#compare(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void compare(Artifact baseVersion, Artifact newerVersion, String option, IProgressMonitor monitor) throws Exception {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getCompareOptions()
    */
   public List<String> getCompareOptions() throws Exception {
      return null;
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
   public String getArtifactUrl(Artifact artifact) {
      return ArtifactRequest.getInstance().getUrl(artifact, true);
   }
}