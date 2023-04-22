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

package org.eclipse.osee.framework.ui.skynet.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.compare.DefaultArtifactCompare;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;

/**
 * Implementations of this interface provide the following functionality:
 * <ul>
 * <li>May provide an implementation of the {@link IComparator} interface for comparing {@link Artifact}s.</li>
 * <li>Provide {@link MenuCmdDef} commands for the editors and functions that may be performed on the supported artifact
 * type.</li>
 * <li>Provide an ordering of the artifact's attributes for publishing.</li>
 * <li>Generate a Word ML representation of the supported artifact's attributes.</li>
 * <li>Generate a {@link String} representation of the supported artifact's attributes.</li>
 * </ul>
 *
 * @author Jeff C. Phillips
 * @author Loren K. Ashley
 */

public interface IRenderer extends RendererMap {

   public static final int ARTIFACT_TYPE_MATCH = 20;
   public static final int BASE_MATCH = 5;
   public static final int GENERAL_MATCH = 10;

   /**
    * {@link IRenderer} applicability match value for an implementation that does not match.
    */

   public static final int NO_MATCH = -1;

   public static final int PRESENTATION_SUBTYPE_MATCH = 50;
   public static final int PRESENTATION_TYPE = 40;
   public static final int PRESENTATION_TYPE_OPTION_MATCH = 55;
   public static final int SPECIALIZED_KEY_MATCH = 70;
   public static final int SPECIALIZED_MATCH = 60;
   public static final int SUBTYPE_TYPE_MATCH = 30;

   /**
    * Appends {@link MenuCmdDef} menu commands supported by the {@link IRenderer} implementation for the type of
    * {@link Artifact} to a list menu commands.
    *
    * @param commands the {@link MenuCmdDef} menu commands for the {@link IRenderer} implementation are appended to this
    * list.
    * @param artifact the {@link IRenderer} implementation may use the <code>artifact</code> to obtain the
    * {@link CoreAttributeTypes#Extension} attribute value to determine how to render the artifact type.
    */

   void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact);

   /**
    * Determines an applicability rating of the {@link IRenderer} implementation for the {@link Artifact} to be
    * rendered, the type of artifact presentation, and the rendering options.
    *
    * @param presentationType the type of presentation to be produced by the {@link IRenderer} implementation.
    * @param artifact the {@link Artifact} to determine the applicability of this {@link IRenderer} implementation for.
    * @param rendererOptions a {@link Map} of {@link RendererOption} values by {@link RendererOption} enumeration
    * members.
    * @return an {@link IRenderer} applicability rating.
    */

   int getApplicabilityRating(PresentationType presentationType, Artifact artifact, RendererMap rendererOptions);

   /**
    * Gets an implementation of the {@link IComparator} interface for comparing artifacts of the type supported by the
    * {@link IRenderer} implementation. When the {@link IRenderer} implementation does not support comparisons, this
    * method will return the {@link DefaultArtifactCompare} implementation of which all methods throw an
    * {@link OseeCoreException}.
    *
    * @return an implementation of the {@link IComparator} interface.
    */

   IComparator getComparator();

   /**
    * Gets a title description for the type of document processed by the {@link IRenderer} implementation.
    */

   String getDocumentTypeDescription();

   /**
    * Gets the renderer's identifier used for publishing template selection.
    *
    * @return the renderer's unique identifier.
    */

   String getIdentifier();

   /**
    * Gets a descriptive name for the {@link IRenderer} implementation.
    *
    * @return a {@link String} containing a descriptive name for the {@link IRenderer} implementation.
    */

   String getName();

   /**
    * Takes a collection of the {@link AttributeTypeToken}s for the attributes of the artifact being rendered and sorts
    * them into the rendering order for the attributes.
    *
    * @param artifact the {@link Artifact} being rendered.
    * @param attributeTypes a {@link Collection} of the {@link AttributeTypeToken}s to be ordered for rendering.
    * @return a list of the provided {@link AttributeTypeToken}s arranged in the rendering order for the attributes.
    */

   public List<AttributeTypeToken> getOrderedAttributeTypes(Artifact artifact, Collection<? extends AttributeTypeToken> attributeTypes);

   /**
    * The {@link RendererManager} uses the following process to find applicable renderers:
    * <ul>
    * <li>The {@link RendererManager}'s prototype renderers are searched for the prototype renderer with the highest
    * applicability rating. When more than one prototype renderer has the same applicability rating the
    * {@link RendererManager} will use the first found prototype renderer.</li>
    * <li>The maximum of the applicability rating returned by this method of the first found prototype renderer with the
    * highest applicability rating or {@link IRenderer#BASE_MATCH} is selected.</li>
    * <li>The {@link RendererManager}'s prototype renderers are searched for all renderers with an applicability rating
    * greater than or equal to the applicability rating selected in the previous step. A new {@link IRenderer}
    * implementation is then created for each prototype renderer and returned on a list of applicable renderers by the
    * {@link RendererManager}.
    * </ul>
    *
    * @implNote Only the {@link DefaultArtifactRenderer} implements this method and it returns a value of
    * {@link IRenderer#NO_MATCH}. So the effective minimum applicability rating is {@link IRenderer#BASE_MATCH}.
    * @return an {@link IRenderer} applicability rating.
    */

   public int minimumRanking();

   /**
    * The {@link RendererManager} loads an instance of each {@link IRenderer} implementation via their registered
    * extension points. When an {@link IRenderer} implementation is needed to process a GUI menu command, this method is
    * used as a factory method to provide an {@link IRenderer} implementation to complete the action.
    *
    * @return a new instance of the {@link IRenderer} interface.
    */

   IRenderer newInstance();

   /**
    * The {@link RendererManager} loads an instance of each {@link IRenderer} implementation via their registered
    * extension points. When an {@link IRenderer} implementation is needed to process a GUI menu command, this method is
    * used as a factory method to provide an {@link IRenderer} implementation to complete the action.
    *
    * @param rendererOptions a map of renderer options.
    * @return a new instance of the {@link IRenderer} interface.
    */

   IRenderer newInstance(RendererMap rendererOptions);

   /**
    * Opens the provided {@link Artifact} objects with the appropriate editor for the {@link PresentationType}.
    *
    * @param artifacts a {@link List} of the {@link Artifact} objects to be opened in the editor.
    * @param presentationType the type of editor to open the {@link Artifact} objects with.
    */

   void open(List<Artifact> artifacts, PresentationType presentationType);

   /**
    * Generates a Word ML representation of an artifact's attribute value.
    *
    * @param attributeType the {@link AttributeTypeToken} of the attribute to be rendered.
    * @param artifact the {@link Artifact} to obtain the attribute value from.
    * @param presentationType the type of presentation to generate for the attribute's value.
    * @param producer the {@link WordMLProducer} object to generate the Word ML with.
    * @param format the attribute formatting option from the publishing template.
    * @param label a label for the attribute in Word ML.
    * @param footer a footer for the attribute in Word ML.
    */

   void renderAttribute(AttributeTypeToken attributeType, Artifact artifact, PresentationType presentationType, WordMLProducer producer, String format, String label, String footer);

   /**
    * Generates a text representation of an artifact's attribute value.
    *
    * @param attributeType the {@link AttributeTypeId} of the attribute to be rendered.
    * @param artifact the {@link Artifact} to obtain the attribute value from.
    * @param presentationType the type of presentation to generate for the attribute's value.
    * @param defaultValue a default value for the attribute's value to be returned when a {@link String} representation
    * cannot be generated.
    * @return {@link String} representation of the artifact's attribute value.
    */

   String renderAttributeAsString(AttributeTypeId attributeType, Artifact artifact, PresentationType presentationType, String defaultValue);

   /**
    * Predicate to determine if the {@link IRenderer} implementation's method {@link #getComparator} will return a valid
    * {@link IComparator} implementation or the {@link DefaultArtifactCompare} implementation. All methods of the
    * {@link DefaultArtifactCompare} implementation throw an {@link OseeCoreException}.
    *
    * @return <code>true</code>, when the method {@link #getComparator} will return a valid comparator; otherwise
    * <code>false</code>.
    */

   boolean supportsCompare();
}

/* EOF */
