/*
 * Created on May 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.relation;

import java.sql.SQLException;

/**
 * @author b1528444
 */
public class RelationTypeSide implements IRelationEnumeration {

   private RelationType type;
   private RelationSide side;

   public RelationTypeSide(RelationType type, RelationSide side) {
      this.type = type;
      this.side = side;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getRelationType()
    */
   @Override
   public RelationType getRelationType() throws SQLException {
      return type;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getSide()
    */
   @Override
   public RelationSide getSide() {
      return side;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getSideName(org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   @Override
   public String getSideName() throws SQLException {
      return type.getSideName(side);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getTypeName()
    */
   @Override
   public String getTypeName() {
      return type.getTypeName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#isSideA()
    */
   @Override
   public boolean isSideA() {
      return side == RelationSide.SIDE_A;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#isThisType(org.eclipse.osee.framework.skynet.core.relation.RelationLink)
    */
   @Override
   public boolean isThisType(RelationLink link) {
      return link.getRelationType() == type;
   }
}
