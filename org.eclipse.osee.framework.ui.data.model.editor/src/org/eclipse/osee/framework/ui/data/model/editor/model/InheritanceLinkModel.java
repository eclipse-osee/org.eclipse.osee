/*
 * Created on Feb 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.data.model.editor.model;

/**
 * @author Roberto E. Escobar
 */
public class InheritanceLinkModel extends ConnectionModel<ArtifactDataType> {

   public InheritanceLinkModel() {
      super();
   }

   public InheritanceLinkModel(ArtifactDataType source, ArtifactDataType target) {
      super(source, target);
   }
}
