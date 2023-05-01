/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.orcs.core.util;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;

@FunctionalInterface
public interface PublishingTemplateSetter {

   /**
    * Implements of this method are expected to do the following:
    * <ul>
    * <li>Create a {@link CoreArtifactTypes#RendererTemplateWholeWord} artifact with the name <code>name</code> under
    * the parent {@link Artifact} <code>parent</code>.</li>
    * <li>When <code>content</code> is non-<code>null</code>, add a {@link CoreAttributeTypes#WholeWordContent}
    * attribute to the created artifact and set it's value to <code>content</code>.</li>
    * <li>When <code>rendererOptions</code> is non-<code>null</code>, add a {@link CoreAttributeTypes#RendererOptions}
    * attribute to the created artifact and set it's value to <code>rendererOptions</code>.</li>
    * <li>When <code>matchCritera</code> is non-<code>null</code> and non-empty, add a
    * {@link CoreAttributeTypes#TemplateMatchCriteria} attribute to the created artifact and set it's values to the
    * values on the list <code>matchCriteria</code>.</li>
    * </ul>
    *
    * @param parent the {@link ArtifactToken} of the OSEE Artifact to create the Publishing Template Artifact
    * hierarchically under.
    * @param name the name for the Publishing Template Artifact.
    * @param content the Word ML content for the Publishing Template Artifact.
    * @param rendererOptions the JSON content for the Publishing Template Artifact.
    * @param matchCriteria the values for the Publishing Template Artifact's Match Criteria Attribute.
    * @return the Publishing Template Manager's identifier for the Publishing Template.
    */

   String set(ArtifactToken parent, String name, String content, String rendererOptions, List<String> matchCriteria);
}

/* EOF */
