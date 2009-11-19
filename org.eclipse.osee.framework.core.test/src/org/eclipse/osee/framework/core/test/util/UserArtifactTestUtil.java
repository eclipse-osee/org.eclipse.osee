/*
 * Created on Nov 19, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.test.util;

import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public class UserArtifactTestUtil implements IBasicArtifact<Object> {

   private String name;
   private String guid;
   private int artId;

   public UserArtifactTestUtil(String name, String guid, int artId) {
      super();
      this.name = name;
      this.guid = guid;
      this.artId = artId;
   }

   @Override
   public int getArtId() {
      return artId;
   }

   @Override
   public Object getFullArtifact() throws OseeCoreException {
      return null;
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public String getName() {
      return name;
   }

}
