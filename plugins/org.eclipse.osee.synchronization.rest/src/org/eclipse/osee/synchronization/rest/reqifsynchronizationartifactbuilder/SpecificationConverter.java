
package org.eclipse.osee.synchronization.rest.reqifsynchronizationartifactbuilder;

import java.util.Objects;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.synchronization.rest.forest.SpecificationGroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.Specification;

/**
 * Class contains the converter method to create the ReqIf {@link Specification} things from the native OSEE
 * {@link ArtifactReadable} things.
 *
 * @author Loren K. Ashley
 */

public class SpecificationConverter {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private SpecificationConverter() {
   }

   /**
    * Converter method for {@link SpecificationGroveThing}s. This method creates the foreign ReqIF {@link Specification}
    * from the native {@link ArtifactReadable}.
    *
    * @param groveThing the {@link SpecificaitionGroveThing} to be converted.
    */

   static void convert(GroveThing groveThing) {
      assert Objects.nonNull(groveThing) && (groveThing instanceof SpecificationGroveThing);

      var specification = (SpecificationGroveThing) groveThing;
      var nativeArtifactReadable = (ArtifactReadable) groveThing.getNativeThing();

      var reqifSpecification = ReqIF10Factory.eINSTANCE.createSpecification();

      //@formatter:off
      var description = new StringBuilder( 512 )
                           .append( "OSEE " ).append( nativeArtifactReadable.getName() ).append( " SpecificationGroveThing")
                           .toString();
      //@formatter:on

      reqifSpecification.setDesc(description);
      reqifSpecification.setIdentifier(specification.getIdentifier().toString());
      reqifSpecification.setLastChange(ReqIFSynchronizationArtifactBuilder.lastChangeEpoch);
      reqifSpecification.setLongName(nativeArtifactReadable.getName());

      specification.setForeignThing(reqifSpecification);
   }
}

/* EOF */
