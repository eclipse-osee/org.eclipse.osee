/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import java.util.List;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Angel Avila
 */
public interface DispoWriter {

   Identifiable<String> createDispoSet(ArtifactReadable author, DispoProgram program, DispoSet descriptor);

   void updateDispoSet(ArtifactReadable author, DispoProgram program, String dispoSetId, DispoSet data);

   boolean deleteDispoSet(ArtifactReadable author, DispoProgram program, String setId);

   void createDispoItems(ArtifactReadable author, DispoProgram program, DispoSet parentSet, List<DispoItem> data, String assignee);

   boolean deleteDispoItem(ArtifactReadable author, DispoProgram program, String itemId);

   void updateDispoItem(ArtifactReadable author, DispoProgram program, String dispoItemId, DispoItem data);

   void updateDispoItems(ArtifactReadable author, DispoProgram program, List<DispoItem> data);
}