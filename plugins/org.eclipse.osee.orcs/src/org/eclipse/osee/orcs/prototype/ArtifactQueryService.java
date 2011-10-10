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
package org.eclipse.osee.orcs.prototype;

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
public abstract class ArtifactQueryService {

   /**
    * Index based Attribute Value search (Quick-Search)<br>
    * --> Whole-word-token search<br>
    * --> Only attribute types that are tagged<br>
    * --> Offline/Large and Inline/Small Attribute Values<br>
    * - Case sensitive and insensitive (with second pass)<br>
    * - Match Word Order<br>
    * - Return all matches <br>
    * Database (ArtifactQuery methods and criteria)<br>
    * --> All in-DB stored attribute types are available searched<br>
    * --> Only Inline/Small Attribute Values<br>
    * --> Complex /Combined Searches<br>
    * - Case sensitive searches (without second pass) - could add case insensitive<br>
    * - Only match word order Criteria Searching<br>
    * <br>
    * Old Complex criteria searching (Search Extension/Wizard)<br>
    * - Should be able to be replaced with ArtifactQuery methods
    */

   public abstract ArtifactQueryOptions getFromToken(IArtifactToken artifactToken, IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromUuids(Collection<Integer> artifactIds, IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromArtifacts(Collection<? extends Artifact> artifacts);

   public abstract ArtifactQueryOptions getFromGuidOrHrid(String guidOrHrid, IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromGuidOrHrids(List<String> guidOrHrids, IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromName(String artifactName, IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromArtifactTypeAndName(IArtifactType artifactType, String artifactName, IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromArtifactTypeAndAttribute(IArtifactType artifactType, IAttributeType attributeType, String attributeValue, IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromArtifactTypeAndAttribute(IArtifactType artifactType, IAttributeType attributeType, Collection<String> attributeValues, IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromAttribute(IAttributeType attributeType, String attributeValue, IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromAttribute(Collection<String> attributeValues, String attributeValue, IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromArtifactTypeAllBranches(IArtifactType artifactType);

   public abstract ArtifactQueryOptions getFromArtifactType(IArtifactType artifactType, IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromArtifactTypes(Collection<? extends IArtifactType> artifactTypes, IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromBranch(IOseeBranch branch);

   public abstract ArtifactQueryOptions getFromArtifactTypeAndCriteria(IArtifactType artifactType, IOseeBranch branch, List<? extends AbstractArtifactSearchCriteria> criteria);

   public abstract ArtifactQueryOptions getFromCriteria(IOseeBranch branch, List<? extends AbstractArtifactSearchCriteria> criteria);

   public abstract ArtifactQueryOptions getFromCriteria(IOseeBranch branch, AbstractArtifactSearchCriteria... criteria);

   public abstract ArtifactQueryOptions getFromRelated(Artifact artifact, IRelationType relationType, RelationSide relationSide);

   public abstract ArtifactQueryOptions getFromRelationType(IRelationType relationType, RelationSide relationSide, IOseeBranch branch);

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
   public abstract List<Artifact> getArtifactListFromAttributeKeywords(IOseeBranch branch, String queryString, boolean isMatchWordOrder, DeletionFlag deletionFlag, boolean isCaseSensitive, IAttributeType... attributeTypes);

   /**
    * Searches for keywords in attributes and returning match location information such as artifact where match was
    * found, attribute containing the match and match location in attribute data.
    * 
    * @see #getArtifactsFromAttributeWithKeywords
    * @param findAllMatchLocations when set to <b>true</b> returns all match locations instead of just returning the
    * first one. When returning all match locations, search performance may be slow.
    */
   //   public abstract List<ArtifactMatch> getArtifactMatchesFromAttributeKeywords(SearchRequest searchRequest);

}
