/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.data.model.editor;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;

/**
 * @author Ryan D. Brooks
 */
public class GraphitiArtifactDiagramBehavior extends DiagramBehavior {
   private final URIConverter converter;

   public GraphitiArtifactDiagramBehavior(IDiagramContainerUI diagramContainer) {
      super(diagramContainer);
      Collection<URIHandler> uriHandlers = Collections.singletonList(new ArtifactURIHandler("Diagram save"));
      converter = new ExtensibleURIConverterImpl(uriHandlers, ContentHandler.Registry.INSTANCE.contentHandlers());
   }

   @Override
   protected void editingDomainInitialized() {
      super.editingDomainInitialized();
      getEditingDomain().getResourceSet().setURIConverter(converter);
   }
}