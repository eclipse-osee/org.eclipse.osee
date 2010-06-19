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

import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.word.AttributeElement;
import org.eclipse.osee.framework.ui.skynet.render.word.Producer;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public interface IRenderer {

   public static final int PRESENTATION_SUBTYPE_MATCH = 50;
   public static final int PRESENTATION_TYPE = 40;
   public static final int SUBTYPE_TYPE_MATCH = 30;
   public static final int ARTIFACT_TYPE_MATCH = 20;
   public static final int DEFAULT_MATCH = 10;
   public static final int NO_MATCH = -1;

   List<String> getCommandId(PresentationType presentationType);

   Image getImage(Artifact artifact) throws OseeCoreException;

   void renderAttribute(String attributeTypeName, Artifact artifact, PresentationType presentationType, Producer producer, VariableMap map, AttributeElement attributeElement) throws OseeCoreException;

   int minimumRanking() throws OseeCoreException;

   void open(List<Artifact> artifacts) throws OseeCoreException;

   void preview(List<Artifact> artifacts) throws OseeCoreException;

   void print(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException;

   void print(List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException;

   void openMergeEdit(List<Artifact> artifacts) throws OseeCoreException;

   int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException;

   String getName();

   void setOptions(VariableMap options) throws OseeArgumentException;

   String getStringOption(String key) throws OseeArgumentException;

   boolean getBooleanOption(String key) throws OseeArgumentException;

   VariableMap getOptions();

   IRenderer newInstance() throws OseeCoreException;

   IComparator getComparator();

   List<AttributeType> orderAttributeNames(Artifact artifact, Collection<AttributeType> attributeTypes);
}
