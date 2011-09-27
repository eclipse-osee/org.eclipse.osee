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
package org.eclipse.osee.orcs;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public interface IArtifactQueryService {

   public ArtifactQuery getFromToken(IArtifactToken artifactToken, IOseeBranch branch);

   public ArtifactQuery getFromUuids(Collection<Integer> artifactIds, IOseeBranch branch);

   public ArtifactQuery getFromArtifacts(Collection<? extends Artifact> artifacts);

   public ArtifactQuery getFromGuidOrHrid(String guidOrHrid, IOseeBranch branch);

   public ArtifactQuery getFromGuidOrHrids(List<String> guidOrHrids, IOseeBranch branch);

   public ArtifactQuery getFromName(String artifactName, IOseeBranch branch);

   public ArtifactQuery getFromArtifactTypeAndName(IArtifactType artifactType, String artifactName, IOseeBranch branch);

   public ArtifactQuery getFromArtifactTypeAndAttribute(IArtifactType artifactType, IAttributeType attributeType, String attributeValue, IOseeBranch branch);

   public ArtifactQuery getFromArtifactTypeAndAttribute(IArtifactType artifactType, IAttributeType attributeType, Collection<String> attributeValues, IOseeBranch branch);

   public ArtifactQuery getFromAttribute(IAttributeType attributeType, String attributeValue, IOseeBranch branch);

   public ArtifactQuery getFromAttribute(Collection<String> attributeValues, String attributeValue, IOseeBranch branch);

   public ArtifactQuery getFromArtifactTypeAllBranches(IArtifactType artifactType);

   public ArtifactQuery getFromArtifactType(IArtifactType artifactType, IOseeBranch branch);

   public ArtifactQuery getFromArtifactTypes(Collection<? extends IArtifactType> artifactTypes, IOseeBranch branch);

   public ArtifactQuery getFromBranch(IOseeBranch branch);

   public ArtifactQuery getFromArtifactTypeAndCriteria(IArtifactType artifactType, IOseeBranch branch, List<? extends AbstractArtifactSearchCriteria> criteria);

   public ArtifactQuery getFromCriteria(IOseeBranch branch, List<? extends AbstractArtifactSearchCriteria> criteria);

   public ArtifactQuery getFromCriteria(IOseeBranch branch, AbstractArtifactSearchCriteria... criteria);

   public ArtifactQuery getFromRelated(Artifact artifact, IRelationType relationType, RelationSide relationSide);

   public ArtifactQuery getFromRelationType(IRelationType relationType, RelationSide relationSide, IOseeBranch branch);

   //

   /**
    * Searches for artifacts having attributes which contain matching keywords entered in the query string.
    * <p>
    * Special characters such as (<b><code>' '</code>, <code>!</code>, <code>"</code>, <code>#</code>, <code>$</code>,
    * <code>%</code>, <code>(</code>, <code>)</code>, <code>*</code>, <code>+</code>, <code>,</code>, <code>-</code>,
    * <code>.</code>, <code>/</code>, <code>:</code>, <code>;</code>, <code>&lt;</code>, <code>&gt;</code>,
    * <code>?</code>, <code>@</code>, <code>[</code>, <code>\</code>, <code>]</code>, <code>^</code>, <code>{</code>,
    * <code>|</code>, <code>}</code>, <code>~</code>, <code>_</code></b>) are assumed to be word separators.
    * </p>
    * <p>
    * For example:
    * <ul>
    * <li><b>'<code>hello.world</code>'</b> will be translated to <b>'<code>hello</code>'</b> and <b>'<code>world</code>
    * '</b>. The search will match attributes with <b>'<code>hello</code>'</b> and <b>'<code>world</code>'</b> keywords.
    * </li>
    * </ul>
    * </p>
    * 
    * @param queryString keywords to match
    * @param matchWordOrder <b>true</b> ensures the query string words exist in order; <b>false</b> matches words in any
    * order
    * @param nameOnly <b>true</b> searches in name attributes only; <b>false</b> includes all tagged attribute types
    * @param allowDeleted <b>true</b> includes deleted artifacts in results; <b>false</b> omits deleted artifacts
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public List<Artifact> getArtifactListFromAttributeKeywords(IOseeBranch branch, String queryString, boolean isMatchWordOrder, DeletionFlag deletionFlag, boolean isCaseSensitive, IAttributeType... attributeTypes);

   /**
    * Searches for keywords in attributes and returning match location information such as artifact where match was
    * found, attribute containing the match and match location in attribute data.
    * 
    * @see #getArtifactsFromAttributeWithKeywords
    * @param findAllMatchLocations when set to <b>true</b> returns all match locations instead of just returning the
    * first one. When returning all match locations, search performance may be slow.
    */
   public List<ArtifactMatch> getArtifactMatchesFromAttributeKeywords(SearchRequest searchRequest);

}
