/*
 * Created on Sep 27, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.mock;

import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.display.presenter.internal.WebProgramsPresenter.IArtifactQuery;
import org.eclipse.osee.display.presenter.internal.WebProgramsPresenter.IFakeArtifact;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;

public class MockArtifactQueryService implements IArtifactQuery {

   @Override
   public IFakeArtifact getArtifactFromToken(IArtifactToken art, IOseeBranch branch) throws OseeCoreException {
      FakeArtifact base = new FakeArtifact("base", GUID.create());

      FakeArtifact prg1 = new FakeArtifact("prog1", GUID.create());
      FakeArtifact prg1Team = new FakeArtifact("progTeam1", GUID.create());

      FakeArtifact version1 = new FakeArtifact("version1", GUID.create());
      FakeArtifact version2 = new FakeArtifact("version2", GUID.create());
      FakeArtifact version3 = new FakeArtifact("version3", GUID.create());
      FakeArtifact version4 = new FakeArtifact("version4", GUID.create());

      version1.setSoleAttributeFromString(AtsAttributeTypes.BaselineBranchGuid, GUID.create());
      version2.setSoleAttributeFromString(AtsAttributeTypes.BaselineBranchGuid, GUID.create());
      version3.setSoleAttributeFromString(AtsAttributeTypes.BaselineBranchGuid, GUID.create());
      version4.setSoleAttributeFromString(AtsAttributeTypes.BaselineBranchGuid, GUID.create());

      prg1Team.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, version1);
      prg1.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, prg1Team);

      FakeArtifact prg2 = new FakeArtifact("prog2", GUID.create());
      FakeArtifact prg2Team = new FakeArtifact("prog2Team", GUID.create());
      prg2Team.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, version1);
      prg2Team.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, version2);
      prg2Team.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, version4);
      prg2.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, prg2Team);

      FakeArtifact prg3 = new FakeArtifact("prog3", GUID.create());
      FakeArtifact prg3Team = new FakeArtifact("prog3Team", GUID.create());
      prg3Team.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, version3);
      prg3Team.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, version1);
      prg3.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, prg3Team);

      base.addRelation(CoreRelationTypes.Universal_Grouping__Members, prg1);
      base.addRelation(CoreRelationTypes.Universal_Grouping__Members, prg2);
      base.addRelation(CoreRelationTypes.Universal_Grouping__Members, prg3);

      return base;
   }
}
