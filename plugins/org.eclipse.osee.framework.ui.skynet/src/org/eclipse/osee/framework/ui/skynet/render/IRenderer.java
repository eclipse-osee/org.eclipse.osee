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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.word.AttributeElement;
import org.eclipse.osee.framework.ui.skynet.render.word.Producer;

/**
 * @author Jeff C. Phillips
 */
public interface IRenderer {

   public static final int SPECIALIZED_KEY_MATCH = 70;
   public static final int SPECIALIZED_MATCH = 60;
   public static final int PRESENTATION_SUBTYPE_MATCH = 50;
   public static final int PRESENTATION_TYPE = 40;
   public static final int SUBTYPE_TYPE_MATCH = 30;
   public static final int ARTIFACT_TYPE_MATCH = 20;
   public static final int GENERAL_MATCH = 10;
   public static final int BASE_MATCH = 5;
   public static final int NO_MATCH = -1;

   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact);

   public void renderAttribute(AttributeTypeToken attributeType, Artifact artifact, PresentationType presentationType, Producer producer, AttributeElement attributeElement, String footer);

   public String renderAttributeAsString(AttributeTypeId attributeType, Artifact artifact, PresentationType presentationType, String defaultValue);

   public int minimumRanking();

   public void open(List<Artifact> artifacts, PresentationType presentationType);

   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions);

   public String getName();

   public IRenderer newInstance(Map<RendererOption, Object> rendererOptions);

   public IRenderer newInstance();

   public boolean supportsCompare();

   public IComparator getComparator();

   public List<AttributeTypeToken> getOrderedAttributeTypes(Artifact artifact, Collection<? extends AttributeTypeToken> attributeTypes);
}
