/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Angel Avila
 */
public interface DispoWriter {

   Identifiable<String> createDispoSet(ArtifactReadable author, String programId, DispoSet descriptor);

   void updateDispoSet(ArtifactReadable author, String programId, String dispoSetId, DispoSet data);

   boolean deleteDispoSet(ArtifactReadable author, String programId, String setId);

   Identifiable<String> createDispoItem(ArtifactReadable author, String programId, DispoSet parentSet, DispoItem itemToCreate, ArtifactReadable assigneeId);

   void updateDispoItem(ArtifactReadable author, String programId, String itemToEditId, DispoItem itemToCreate);

   boolean deleteDispoItem(ArtifactReadable author, String programId, String itemId);

   void createAnnotation(ArtifactReadable author, String programId, ArtifactId dispoItem, String annotationsJson);
}