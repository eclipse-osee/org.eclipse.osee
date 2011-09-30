/*
 * Created on Oct 5, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.display.presenter;

import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.StringOperator;

public class ArtifactProviderImpl implements ArtifactProvider {

   private final QueryFactory factory;

   public ArtifactProviderImpl(QueryFactory factory) {
      this.factory = factory;
   }

   @Override
   public ReadableArtifact getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) throws OseeCoreException {
      return factory.fromArtifact(branch, token).build(LoadLevel.FULL).getOneOrNull();
   }

   @Override
   public ReadableArtifact getArtifactByGuid(IOseeBranch branch, String guid) throws OseeCoreException {
      return factory.fromGuidOrHrid(branch, guid).build(LoadLevel.FULL).getOneOrNull();
   }

   @Override
   public List<ReadableArtifact> getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase) throws OseeCoreException {
      if (nameOnly) {
         return factory.fromBranch(branch).and(CoreAttributeTypes.Name, StringOperator.CONTAINS, CaseType.IGNORE_CASE,
            searchPhrase).build(LoadLevel.FULL).getList();
      } else {
         return null;
      }
   }
}
