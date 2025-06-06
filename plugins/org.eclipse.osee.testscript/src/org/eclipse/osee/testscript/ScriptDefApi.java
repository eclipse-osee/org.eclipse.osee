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

package org.eclipse.osee.testscript;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Stephen J. Molaro
 */
public interface ScriptDefApi {

   ScriptDefToken get(BranchId branch, ArtifactId scriptDefTypeId);

   Collection<ScriptDefToken> getAll(BranchId branch);

   Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId);

   Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId, List<FollowRelation> followRelations);

   Collection<ScriptDefToken> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId, AttributeTypeId orderByAttribute);

   Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId, List<FollowRelation> followRelations,
      AttributeTypeId orderByAttribute);

   Collection<ScriptDefToken> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize);

   Collection<ScriptDefToken> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId, List<FollowRelation> followRelations,
      long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   Collection<ScriptDefToken> getAll(BranchId branch, Collection<FollowRelation> followRelations, String filter,
      Collection<AttributeTypeId> attributes, long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<ScriptDefToken> getAllByFilter(BranchId branch, String filter);

   Collection<ScriptDefToken> getAllByFilter(BranchId branch, String filter,
      Collection<AttributeTypeId> searchAttributes);

   Collection<ScriptDefToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter);

   Collection<ScriptDefToken> getAllByFilter(BranchId branch, String filter, AttributeTypeId orderByAttribute);

   Collection<ScriptDefToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      AttributeTypeId orderByAttribute);

   Collection<ScriptDefToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize);

   Collection<ScriptDefToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize);

   Collection<ScriptDefToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<ScriptDefToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   Collection<ScriptDefToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      Collection<AttributeTypeId> searchAttributes, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   Collection<ScriptDefToken> getAllByFilter(BranchId branch, String filter, Collection<FollowRelation> followRelations,
      long pageCount, long pageSize, AttributeTypeId orderByAttribute, Collection<AttributeTypeId> followAttributes);

   int getCountWithFilter(BranchId branch, ArtifactId viewId, String filter, Collection<AttributeTypeId> attributes);

   Collection<ScriptDefToken> getAllByRelationThrough(BranchId branch, LinkedList<RelationTypeSide> relations,
      ArtifactId relatedId, String filter, Collection<AttributeTypeId> attributes,
      Collection<FollowRelation> followRelations, long pageCount, long pageSize, AttributeTypeId orderByAttribute,
      Collection<AttributeTypeId> followAttributes, ArtifactId viewId);

   int getAllByRelationThroughAndCount(BranchId branch, LinkedList<RelationTypeSide> relations, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, ArtifactId viewId);

}
