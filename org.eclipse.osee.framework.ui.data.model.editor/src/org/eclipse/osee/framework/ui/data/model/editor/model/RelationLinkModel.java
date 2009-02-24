/*
 * Created on Feb 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.data.model.editor.model;

/**
 * @author Roberto E. Escobar
 */
public class RelationLinkModel extends ConnectionModel<ArtifactDataType> {

   private RelationDataType relation;

   public RelationLinkModel() {
      super();
      relation = null;
   }

   public RelationLinkModel(RelationDataType relation, ArtifactDataType aSide, ArtifactDataType bSide) {
      super(aSide, bSide);
      this.relation = relation;
   }

   public void setASide(ArtifactDataType aSide) {
      setSource(aSide);
   }

   public void setBSide(ArtifactDataType bSide) {
      setTarget(bSide);
   }

   public ArtifactDataType getASide() {
      return getSource();
   }

   public ArtifactDataType getBSide() {
      return getTarget();
   }

   public void setRelation(RelationDataType relation) {
      this.relation = relation;
   }

   public RelationDataType getRelation() {
      return relation;
   }
}
