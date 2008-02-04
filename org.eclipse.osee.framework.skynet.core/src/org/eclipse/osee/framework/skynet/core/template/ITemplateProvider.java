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
package org.eclipse.osee.framework.skynet.core.template;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;

/**
 * @author Roberto E. Escobar
 */
public interface ITemplateProvider {

   public void initializeTemplates(Artifact documentFolder, ArtifactSubtypeDescriptor documentDescriptor) throws Exception;

   public void setDefaultTemplates(String rendererId, Artifact document, String presentationType, Branch branch) throws Exception;

   public void addTemplate(String rendererId, Branch branch, String presentationType, TemplateLocator locationData) throws Exception;

   public String getTemplate(String rendererId, Branch branch, Artifact artifact, String presentationType, String option) throws Exception;

}
