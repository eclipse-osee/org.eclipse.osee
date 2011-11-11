/*
 * Created on Sep 27, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.display.presenter.mocks;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.presenter.WebProgramsPresenter.IFakeArtifact;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

public class FakeArtifact implements IFakeArtifact {

   private final Map<IRelationTypeSide, Collection<IFakeArtifact>> relationMap =
      new HashMap<IRelationTypeSide, Collection<IFakeArtifact>>();
   private final Map<IAttributeType, String> attributeMap = new HashMap<IAttributeType, String>();
   private final String guid;

   public FakeArtifact(String name, String guid) {
      attributeMap.put(CoreAttributeTypes.Name, name);
      this.guid = guid;
   }

   public void addRelation(IRelationTypeSide relationType, IFakeArtifact artifact) {
      Collection<IFakeArtifact> related;
      if (relationMap.containsKey(relationType)) {
         related = relationMap.get(relationType);
      } else {
         related = new LinkedList<IFakeArtifact>();
      }
      related.add(artifact);
      relationMap.put(relationType, related);
   }

   @Override
   public List<IFakeArtifact> getRelatedArtifacts(IRelationTypeSide relationSide) {
      return new LinkedList<IFakeArtifact>(relationMap.get(relationSide));
   }

   @Override
   public IFakeArtifact getRelatedArtifact(IRelationTypeSide relationSide) {
      if (relationMap.containsKey(relationSide)) {
         return relationMap.get(relationSide).iterator().next();
      } else {
         return null;
      }
   }

   @Override
   public String getName() {
      return attributeMap.get(CoreAttributeTypes.Name);
   }

   @Override
   public String getGuid() {
      return guid;
   }

   public void setSoleAttributeFromString(IAttributeType type, String value) {
      attributeMap.put(type, value);
   }

}
