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
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;

/**
 * @author Jeff C. Phillips
 */
public interface IRenderer {

   public static final int ARTIFACT_TYPE_MATCH = 20;
   public static final int BASE_MATCH = 5;
   public static final int GENERAL_MATCH = 10;
   public static final int NO_MATCH = -1;
   public static final int PRESENTATION_SUBTYPE_MATCH = 50;
   public static final int PRESENTATION_TYPE = 40;
   public static final int PRESENTATION_TYPE_OPTION_MATCH = 55;
   public static final int SPECIALIZED_KEY_MATCH = 70;
   public static final int SPECIALIZED_MATCH = 60;
   public static final int SUBTYPE_TYPE_MATCH = 30;

   void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact);

   int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions);

   IComparator getComparator();

   /**
    * Gets the renderer's identifier used for publishing template selection.
    *
    * @return the renderer's unique identifier.
    */

   String getIdentifier();

   /**
    * Gets the renderer's name.
    *
    * @return a {@link String} with the renderer's name.
    */

   String getName();

   List<AttributeTypeToken> getOrderedAttributeTypes(Artifact artifact, Collection<? extends AttributeTypeToken> attributeTypes);

   int minimumRanking();

   IRenderer newInstance();

   IRenderer newInstance(Map<RendererOption, Object> rendererOptions);

   void open(List<Artifact> artifacts, PresentationType presentationType);

   void renderAttribute(AttributeTypeToken attributeType, Artifact artifact, PresentationType presentationType, WordMLProducer producer, String format, String label, String footer);

   String renderAttributeAsString(AttributeTypeId attributeType, Artifact artifact, PresentationType presentationType, String defaultValue);

   boolean supportsCompare();

   /**
    * Updates the value of a renderer option.
    *
    * @param key the {@link RendererOption} to be updated.
    * @param value the new value.
    * @throws NullPointerException when either of the parameters <code>key</code> or <code>value</code> are
    * <code>null</code>.
    */

   void updateOption(RendererOption key, Object value);
}

/* EOF */
