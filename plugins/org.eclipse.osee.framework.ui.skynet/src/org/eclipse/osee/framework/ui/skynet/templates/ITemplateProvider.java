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

package org.eclipse.osee.framework.ui.skynet.templates;

import java.util.List;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;

/**
 * @author Roberto E. Escobar
 */
public interface ITemplateProvider {

   public static final int SUBTYPE_TYPE_MATCH = 30;
   public static final int ARTIFACT_TYPE_MATCH = 20;
   public static final int DEFAULT_MATCH = 10;
   public static final int NO_MATCH = -1;

   public Artifact getTemplate(IRenderer renderer, Artifact artifact, PresentationType presentationType, String option);

   public abstract int getApplicabilityRating(IRenderer renderer, Artifact artifact, PresentationType presentationType, String option);

   public List<Artifact> getAllTemplates();
}
