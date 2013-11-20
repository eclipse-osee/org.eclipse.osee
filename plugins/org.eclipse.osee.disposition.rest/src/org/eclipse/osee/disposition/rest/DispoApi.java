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
package org.eclipse.osee.disposition.rest;

import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;

/**
 * @author Angel Avila
 */
public interface DispoApi {

   // Queries

   IOseeBranch getDispoProgramById(String programId);

   ResultSet<IOseeBranch> getDispoPrograms();

   ResultSet<DispoSetData> getDispoSets(String programId);

   DispoSetData getDispoSetById(String programId, String dispoSetId);

   ResultSet<DispoItemData> getDispoItems(String programId, String dispoSetId);

   DispoItemData getDispoItemById(String programId, String itemId);

   ResultSet<DispoAnnotationData> getDispoAnnotations(String programId, String itemId);

   DispoAnnotationData getDispoAnnotationByIndex(String programId, String itemId, int index);

   // Writes
   Identifiable<String> createDispoSet(String programId, DispoSetDescriptorData descriptor);

   Identifiable<String> createDispotem(String programId, String setId, DispoItemData dispoItem);

   Integer createDispoAnnotation(String programId, String setId, String itemId, DispoAnnotationData annotation);

   // Writes

   boolean editDispoSet(String programId, String dispoSetId, DispoSetData newDispoSet);

   boolean editDispoItem(String programId, String itemId, DispoItemData newDispoItem);

   boolean editDispoAnnotation(String programId, String itemId, int index, DispoAnnotationData newAnnotation);

   // Deletes

   boolean deleteDispoSet(String programId, String dispoSetId);

   boolean deleteDispoItem(String programId, String itemId);

   boolean deleteDispoAnnotation(String programId, String itemId, int index);

   // Utilities
   boolean isUniqueSetName(String programId, String setName);

   boolean isUniqueItemName(String programId, String setId, String itemName);

}
