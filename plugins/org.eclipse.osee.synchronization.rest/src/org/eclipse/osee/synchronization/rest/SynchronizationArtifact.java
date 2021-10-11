/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.synchronization.rest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.rest.forest.Forest;
import org.eclipse.osee.synchronization.rest.forest.Grove;
import org.eclipse.osee.synchronization.rest.forest.GroveThing;
import org.eclipse.osee.synchronization.rest.forest.denizens.ArtifactTypeTokens;
import org.eclipse.osee.synchronization.rest.forest.denizens.NativeDataTypeKey;
import org.eclipse.osee.synchronization.rest.forest.denizens.NativeDataTypeKeyFactory;
import org.eclipse.osee.synchronization.rest.forest.denizens.NativeHeader;
import org.eclipse.osee.synchronization.rest.forest.denizens.SpecRelationArtifactReadable;
import org.eclipse.osee.synchronization.rest.forest.denizens.SpecterSpecObjectArtifactReadable;
import org.eclipse.osee.synchronization.rest.forest.denizens.UnknownAttributeTypeTokenException;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ToMessage;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * Class builds a Synchronization Artifact from the OSEE artifact trees specified by branch identifier and artifact
 * identifier pairs.
 *
 * @author Loren K. Ashley
 */

public class SynchronizationArtifact implements ToMessage {

   /**
    * A {@link Map} of the supported {@link SynchronizationArtifactBuilder} classes by their artifact type
    * {@link String} identifiers.
    */

   private static Map<String, Class<?>> synchronizationArtifactBuilderClassMap;

   /*
    * Find the available {@link SynchronizationArtifactBuilder} classes.
    */

   static {

      SynchronizationArtifact.synchronizationArtifactBuilderClassMap = new HashMap<>();

      var bundleContext = FrameworkUtil.getBundle(SynchronizationArtifact.class).getBundleContext();
      var bundle = bundleContext.getBundle();
      var bundleSymbolicNamePath = bundle.getSymbolicName().replace('.', '/');
      var bundleWiring = bundle.adapt(BundleWiring.class);
      var classLoader = bundleWiring.getClassLoader();
      var resources = bundleWiring.listResources(bundleSymbolicNamePath, "*.class", BundleWiring.LISTRESOURCES_RECURSE);

      resources.forEach(resource -> {
         try {
            var className = resource.substring(0, resource.indexOf('.')).replace('/', '.');
            var theClass = classLoader.loadClass(className);
            var isSynchronizationArtifactBuilder = theClass.getAnnotation(IsSynchronizationArtifactBuilder.class);
            if (isSynchronizationArtifactBuilder != null) {
               SynchronizationArtifact.synchronizationArtifactBuilderClassMap.put(
                  isSynchronizationArtifactBuilder.artifactType(), theClass);
            }
         } catch (Exception e) {
            /*
             * Eat exceptions in the static initializer. If the Synchronization Artifact Build implementations are not
             * found, an UnknownSynchronizationArtifactTypeException will be thrown when trying to create an instance of
             * this class.
             */
         }
      });

   }

   /**
    * Data structures used to hold the Synchronization Artifact DOM.
    */

   private final Forest forest;

   /**
    * Handle to the OSEE ORCS API used to obtain OSEE artifacts.
    */

   private final OrcsApi orcsApi;

   /**
    * Map used to collect unique OSEE artifact types
    */

   private final CommonObjectTypeContainerMap commonObjectTypeContainerMap;

   /**
    * Saves the processing instructions
    */

   private final RootList rootList;

   /**
    * The {@link SynchronizationArtifactBuilder} to be used for this Synchronization Artifact.
    */

   private final SynchronizationArtifactBuilder synchronizationArtifactBuilder;

   /**
    * Each Synchronization Artifact needs it's own factory for producing {@link NativeDataTypeKey} objects since each
    * Synchronization Artifact may have a unique set of enumerated data types.
    */

   private final NativeDataTypeKeyFactory nativeDataTypeKeyFactory;

   /**
    * Creates a new empty SynchronizationArtifact.
    *
    * @param rootList the Synchronization Artifact building instructions.
    * @param synchronizationArtifactBuilder the {@link SynchronizationArtifactBuilder} for the type of Synchronization
    * Artifact to be built.
    */

   private SynchronizationArtifact(RootList rootList, SynchronizationArtifactBuilder synchronizationArtifactBuilder) {

      //@formatter:off
      assert
            Objects.nonNull(rootList)
         && Objects.nonNull(rootList.getOrcsApi())
         && Objects.nonNull(synchronizationArtifactBuilder);
      //@formatter:on

      IdentifierType.resetIdentifierCounts();
      this.rootList = rootList;
      this.synchronizationArtifactBuilder = synchronizationArtifactBuilder;
      this.orcsApi = rootList.getOrcsApi();
      this.commonObjectTypeContainerMap = new CommonObjectTypeContainerMap();
      this.forest = new Forest();
      this.nativeDataTypeKeyFactory = new NativeDataTypeKeyFactory();
   }

   /**
    * Factory method to create a empty Synchronization Artifact.
    *
    * @param rootList a list of the OSEE artifacts to be included in the Synchronization Artifact.
    * @return an empty {@link SynchronizationArtifact}.
    * @throws UnknownArtifactTypeException when a {@link SynchronizationArtifactBuilder} could not be created for the
    * artifact type.
    * @throws NullPointerException when the {@link RootList} parameter or it's {@link OrcsApi} is <code>null</code>.
    */

   public static SynchronizationArtifact create(RootList rootList) throws UnknownSynchronizationArtifactTypeException {

      Objects.requireNonNull(rootList);
      Objects.requireNonNull(rootList.getOrcsApi());

      var synchronizationArtifactType = rootList.getSynchronizationArtifactType();

      if (!SynchronizationArtifact.synchronizationArtifactBuilderClassMap.containsKey(synchronizationArtifactType)) {
         throw new UnknownSynchronizationArtifactTypeException(synchronizationArtifactType);
      }

      var synchronizationArtifactBuilder =
         SynchronizationArtifact.getSynchronizationArtifactBuilder(synchronizationArtifactType);

      SynchronizationArtifact synchronizationArtifact =
         new SynchronizationArtifact(rootList, synchronizationArtifactBuilder);

      return synchronizationArtifact;
   }

   /**
    * Gets the {@link SynchronizationArtifactBuilder} for the type of Synchronization Artifact to be built.
    *
    * @param artifactType the type of Synchronization Artifact to be built.
    * @return the {@link SynchronizationArtifactBuilder} to build the Synchronization Artifact with.
    * @throws UnknownSynchronizationArtifactTypeException when a {@link SynchronizationArtifactBuilder} could not be
    * created for the artifact type.
    */

   private static SynchronizationArtifactBuilder getSynchronizationArtifactBuilder(String artifactType) throws UnknownSynchronizationArtifactTypeException {
      try {
         //@formatter:off
         return
            (SynchronizationArtifactBuilder) SynchronizationArtifact.synchronizationArtifactBuilderClassMap
               .get( artifactType )
               .getConstructor( (Class<?>[]) null )
               .newInstance( (Object[]) null );
         //@formatter:on
      } catch (Exception e) {
         throw new UnknownSynchronizationArtifactTypeException(artifactType, e);
      }
   }

   /**
    * Builds the Synchronization Artifact according to the instructions provided to the constructor.
    */

   public void build() {

      /*
       * Create the HeaderGroveThing
       */

      this.forest.getGrove(IdentifierType.HEADER).add(
         this.forest.createGroveThing(IdentifierType.HEADER).setNativeThings(
            new NativeHeader(1L, this.orcsApi, this.rootList)));

      //@formatter:off
      /*
       * This step completes the following:
       *    1) Gather the OSEE native artifacts for each of the Synchronization Artifact
       *       document roots.
       *    2) Populate the Specification Grove with SpecificationGroveThings
       *    3) Populate the Spec Object Grove with both SpecificationGroveThings and SpecObjectGroveThings
       *    4) Create SpecificationTypeGroveThings and populate the SpecificationTypeGrove for each unique specification type
       *    5) Create SpecObjectTypeGroveThings and populate the SpecObjectTypeGrove for each unique spec object type
       *    6) Create SpecRelationTypeGroveThings and populate the SpecRelationTypeGrove for each unique spec relation type
       */
      //@formatter:on

      this.rootList.forEach(this::processRootArtifact);

      /*
       * Create the Spec Relationships needed for the Spec Objects.
       */

      this.processSpecObjectGroveForRelationships();

      /*
       * Create the Attribute Definitions for all of the unique Specification Type, Spec Object Type, and Spec Relation
       * Type GroveThings
       */

      this.processCommonObjectTypeContainerMap();

      /*
       * Create the Data Type Definitions needed for the Attribute Definitions.
       */

      this.processAttributeDefinitionGrove();

      /*
       * Create the Attribute Value things needed for the Specification, Spec Object, and Spec Relation things.
       */

      this.processCommonObjectTypeGroveThings();

      /*
       * Create foreign things for all of the native things in each grove. The forest stream is an ordered stream. Grove
       * streams are unordered.
       */

      //@formatter:off
      this.forest.stream().forEach
         (
            ( grove ) -> this.getSynchronizationArtifactBuilder().getConverter( grove.getType() ).ifPresent
                            (
                              ( converter ) -> grove.stream().filter( groveThing -> groveThing.getIdentifier().getType().equals( grove.getType() ) ).forEach( converter::accept )
                            )
         );
      //@formatter:on

      /*
       * Assemble the final Synchronization Artifact.
       */

      this.synchronizationArtifactBuilder.build(this);
   }

   /**
    * Creates a {@link GroveThing} for a Specification or Spec Object and also creates the associated Specification Type
    * or Spec Object Type {@link GroveThing} from an OSEE artifact.
    *
    * @param nativeArtifactReadable the native OSEE artifact.
    * @param identifierType set to {@link IdentifierType#SPECIFICATION} or {@link IdentifierType#SPEC_OBJECT}.
    * @param specificationGroveThing when creating a Spec Object set to the {@link GroveThing} of the Specification the
    * Spec Object will belong to.
    * @param parentCommonObjectGroveThing when creating a Spec Object set to the hierarchical parent {@link GroveThing}
    * of the Spec Object.
    * @return the {@link GroveThing} for the Synchronization Artifact Specification of Spec Object that was created.
    */

   private GroveThing createCommonObject(ArtifactReadable nativeArtifactReadable, IdentifierType identifierType, GroveThing specificationGroveThing, GroveThing parentCommonObjectGroveThing) {

      //@formatter:off
      assert
            (    identifierType.equals( IdentifierType.SPECIFICATION )
              && Objects.isNull( specificationGroveThing )
              && Objects.isNull( parentCommonObjectGroveThing ) )
         || (    identifierType.equals( IdentifierType.SPEC_OBJECT )
              && Objects.nonNull( specificationGroveThing )
              && Objects.nonNull( parentCommonObjectGroveThing )
            );

      var commonObjectGroveThing =
         identifierType == IdentifierType.SPECIFICATION
            ? this.forest.createGroveThing( identifierType )
            : this.forest.createGroveThing( identifierType, specificationGroveThing, parentCommonObjectGroveThing );

      commonObjectGroveThing.setNativeThings( nativeArtifactReadable );

      commonObjectGroveThing.setLinkScalar
         (
            identifierType.getAssociatedType(),
            this.getOrCreateCommonObjectType( nativeArtifactReadable.getArtifactType(), identifierType.getAssociatedType() )
         );

      return commonObjectGroveThing;
      //@formatter:on
   }

   /**
    * Creates a Spec Relation to link a Spec Object with another Spec Object or a Specification.
    *
    * @param sourceSpecObjectGroveThing the Spec Object or Specification for the source side of the relationship
    * @param targetSpecObjectGroveThing the Spec Object or Specification for the target side of the relationship
    * @param nativeRelationTypeToken the type of relationship to create.
    */

   private void createRelationship(GroveThing sourceSpecObjectGroveThing, GroveThing targetSpecObjectGroveThing, RelationTypeToken nativeRelationTypeToken) {

      var specRelationGrove = this.getForest().getGrove(IdentifierType.SPEC_RELATION);
      var specRelationTypeGroveThing = this.getSpecRelationTypeGroveThing(nativeRelationTypeToken.getId());
      var nativeSpecRelationTypeArtifactTypeToken = (ArtifactTypeToken) specRelationTypeGroveThing.getNativeThing();

      /*
       * If relationship has already been created, nothing left to do
       */

      if (specRelationGrove.containsByNativeKeys(
         ((ArtifactReadable) sourceSpecObjectGroveThing.getNativeThing()).getId(),
         ((ArtifactReadable) targetSpecObjectGroveThing.getNativeThing()).getId(),
         nativeSpecRelationTypeArtifactTypeToken.getId())) {
         return;
      }

      var artifact = new SpecRelationArtifactReadable(nativeSpecRelationTypeArtifactTypeToken,
         nativeRelationTypeToken.getSideName(RelationSide.SIDE_A),
         nativeRelationTypeToken.getSideName(RelationSide.SIDE_B), nativeRelationTypeToken.getMultiplicity());

      var specRelationGroveThing = this.getForest().createGroveThing(IdentifierType.SPEC_RELATION);

      specRelationGroveThing.setNativeThings(sourceSpecObjectGroveThing.getNativeThing(),
         targetSpecObjectGroveThing.getNativeThing(), artifact);
      specRelationGroveThing.setLinkScalar(IdentifierType.SPEC_RELATION_TYPE, specRelationTypeGroveThing);
      specRelationGroveThing.setLinkVectorElement(IdentifierTypeGroup.RELATABLE_OBJECT, sourceSpecObjectGroveThing);
      specRelationGroveThing.setLinkVectorElement(IdentifierTypeGroup.RELATABLE_OBJECT, targetSpecObjectGroveThing);

      specRelationGrove.add(specRelationGroveThing);
   }

   /**
    * Creates the Spec Relations for the specified relationship side between the specified Spec Object and the related
    * Spec Object or Specification. When the related thing is a Specification, a Specter Spec Object is created to
    * represent the Specification for the relationship end point.
    * <p>
    * <h2>Side A</h2>
    * <ul>
    * <li>The <code>specObjectGroveThing</code> is the source Spec Object of the relationship.</li>
    * <li>The referenced Spec Object is the target Spec Object of the relationship.</li>
    * </ul>
    * <h2>Side B</h2>
    * <ul>
    * <li>The <code>specObjectGroveThing</code> is the target Spec Object of the relationship.</li>
    * <li>The referenced Spec Object is the source Spec Object of the relationship.</li>
    * </ul>
    *
    * @param relationSide the {@link RelationSide} to create relationships for.
    * @param specObjectGroveThing the Spec Object {@link GroveThing} to create relationships for.
    * @param nativeRelationTypeToken the relationship type to create relationships for.
    */

   private void createRelationshipsForSide(RelationSide relationSide, GroveThing specObjectGroveThing, RelationTypeToken nativeRelationTypeToken) {
      //@formatter:off

      assert
            Objects.nonNull( relationSide )
         && Objects.nonNull( specObjectGroveThing )
         && Objects.nonNull( nativeRelationTypeToken );


      var nativeRelationTypeSide  = new RelationTypeSide( nativeRelationTypeToken, relationSide );

      /*
       * Select relationship creation lambda according to the relationship side
       */

      Consumer<GroveThing> relationshipCreator =
         relationSide.equals( RelationSide.SIDE_A )
            ? ( relatedSpecObjectGroveThing ) -> this.createRelationship( specObjectGroveThing,        relatedSpecObjectGroveThing, nativeRelationTypeToken )
            : ( relatedSpecObjectGroveThing ) -> this.createRelationship( relatedSpecObjectGroveThing, specObjectGroveThing,        nativeRelationTypeToken );

      /*
       * Get all the related native ArtifactReadables for the side of the relationship type
       */

      ((ArtifactReadable) specObjectGroveThing.getNativeThing()).getRelated( nativeRelationTypeSide ).getList().stream()

         /*
          * Get the native identifier for each related native ArtifactReadable
          */

         .map( ArtifactReadable::getId )

         /*
          * Look up or create a new Spec Object GroveThing representing the related native ArtifactReadable
          */

         .map( this::getSpecObjectGroveThingOrGetOrCreateSpecterSpecObjectGroveThing )

         /*
          * For each SpecObjectGroveThing that is related by the relationship type for the side, create a relationship
          */

         .forEach( relationshipCreator );
      //@formatter:on
   }

   /**
    * Gets the data containers for the Synchronization Artifact.
    *
    * @return the {@link Forest}.
    */

   public Forest getForest() {
      return this.forest;
   }

   /**
    * Creates or gets the {@link GroveThing} for the Specification Type, Spec Object Type, or Spec Relation Type
    * associated with the {@link ArtifactTypeToken}. When a new {@link GroveThing} is created it will be added to it's
    * corresponding grove.
    *
    * @param nativeArtifactTypeToken the OSEE Artifact Type
    * @param identifierType the type of {@link GroveThing} to get or create.
    * @return the newly created or existing associated {@link GroveThing}.
    */

   private GroveThing getOrCreateCommonObjectType(ArtifactTypeToken nativeArtifactTypeToken, IdentifierType identifierType) {
      return this.commonObjectTypeContainerMap.get(nativeArtifactTypeToken, identifierType).orElseGet(() -> {
         var newCommonObjectTypeGroveThing = this.forest.createGroveThing(identifierType);
         newCommonObjectTypeGroveThing.setNativeThings(nativeArtifactTypeToken);
         this.forest.getGrove(identifierType).add(newCommonObjectTypeGroveThing);
         this.commonObjectTypeContainerMap.put(newCommonObjectTypeGroveThing);
         return newCommonObjectTypeGroveThing;
      });
   }

   /**
    * When the Specter Spec Object {@link Grove} contains a Specter Spec Object {@link GroveThing} with the native key,
    * that Specter Spec Object is obtained; otherwise, a new Specter Spec Object is created for the native key. When a
    * new Specter Spec Object is created, it is linked with an existing or new Spec Object Type {@link GroveThing}
    * defining the attributes for Specter Spec Objects. If a new Specter Spec Object or Spec Object Type is created they
    * are added to their respective groves.
    *
    * @param specterId the {@link ArtifactReadable} identifier to obtain or create a Specter Spec Object for.
    * @param specterNameSupplier a {@link Supplier} used to create a name for the Specter Spec Object. The supplier is
    * only used when a new Specter Spec Object is created.
    * @return the obtained or created Specter Spec Object {@link GroveThing}.
    */

   private GroveThing getOrCreateSpecterSpecObjectGroveThing(Long specterId, Supplier<String> specterNameSupplier) {

      var orcsTokenService = this.orcsApi.tokenService();

      //@formatter:off
      return
         this.getForest().getGrove( IdentifierType.SPECTER_SPEC_OBJECT ).getByNativeKeys( specterId )
            .orElseGet
               (
                  () ->
                  {
                     var specterSpecObjectGroveThing = this.getForest().createGroveThing( IdentifierType.SPECTER_SPEC_OBJECT );

                     specterSpecObjectGroveThing.setNativeThings
                        (
                           new SpecterSpecObjectArtifactReadable
                           (
                             ArtifactTypeTokens.createSpecterSpecObjectArtifactTypeToken( orcsTokenService ),
                             specterId,
                             specterNameSupplier.get()
                           )
                        );

                     this.getForest().getGrove( IdentifierType.SPECTER_SPEC_OBJECT ).add( specterSpecObjectGroveThing );

                     specterSpecObjectGroveThing.setLinkScalar
                        (
                           IdentifierType.SPEC_OBJECT_TYPE,
                           this.getOrCreateCommonObjectType
                              (
                                ArtifactTypeTokens.createSpecterSpecObjectArtifactTypeToken( orcsTokenService ),
                                IdentifierType.SPEC_OBJECT_TYPE
                              )
                        );

                     return specterSpecObjectGroveThing;
                  }
               );
      //@formatter:on
   }

   /**
    * Gets the Spec Object or Specter Spec Object for a relationship end point by the native OSEE Artifact Identifier as
    * follows:
    * <ul>
    * <li>When the Spec Object {@link Grove} contains a Spec Object {@link GroveThing} with the native key, that Spec
    * Object is obtained.
    * <li>
    * <li>When the Spec Object {@link Grove} contains a Specification {@link GroveThing} with the native key, a Specter
    * Spec Object representing the Specification {@link GroveThing} is obtained.</li>
    * <li>When the Spec Object {@link Grove} does not contain an entry with the native key, a Specter Spec Object
    * representing the OSEE Artifact specified by the native key is obtained.</li>
    * <ul>
    * Specter Spec Objects are obtained from the Specter Spec Object {@link Grove} using the native key. When the
    * Specter Spec Object {@link Grove} does not contain an entry for the native key, a new Specter Spec Object is
    * created and added to the grove.
    *
    * @param nativeKey the {@link ArtifactReadable} identifier to obtain a Spec Object or Specter Spec Object
    * {@link GroveThing} with.
    * @return the obtained or created Spec Object or Specter Spec Object {@link GroveThing}.
    */

   private GroveThing getSpecObjectGroveThingOrGetOrCreateSpecterSpecObjectGroveThing(Object nativeKey) {

      //@formatter:off
      return
         this
            .getForest()
            .getGrove( IdentifierType.SPEC_OBJECT )
            .getByNativeKeys( nativeKey )
            .map( ( commonObject ) ->
                  {
                     if( commonObject.isType( IdentifierType.SPECIFICATION ) )
                     {
                        var specterId = ((NamedId) commonObject.getNativeThing()).getId();

                        Supplier<String> specterNameSupplier = () -> new StringBuilder( 512 )
                                                                            .append( "Specter Spec Object For Specification: " )
                                                                            .append( ((NamedId) commonObject.getNativeThing()).getName() )
                                                                            .toString();
                        return
                           this.getOrCreateSpecterSpecObjectGroveThing
                              (
                                 specterId,
                                 specterNameSupplier
                              );
                     }

                     return commonObject;
                  }
                )
            .orElseGet
               (
                  () ->
                  {
                     var specterId = (Long) nativeKey;

                     Supplier<String> specterNameSupplier = () -> ((Long) nativeKey).toString();

                     return
                        this.getOrCreateSpecterSpecObjectGroveThing
                           (
                              specterId,
                              specterNameSupplier
                           );
                  }
               );

      //@formatter:on
   }

   /**
    * Gets a {@link GroveThing} for a Spec Relation Type for the Spec Relation Type Grove using the native key.
    *
    * @param nativeKey the {@link RelationTypeToken} identifier to look up the Spec Relation Type {@link GroveThing}
    * with.
    * @return the {@link GroveThing} specified by the native key.
    * @throws NoSuchElementException when the Spec Relation Type {@link Grove} does not contain a {@link GroveThing}
    * associated with the provided key.
    */

   private GroveThing getSpecRelationTypeGroveThing(Object nativeKey) {

      return this.getForest().getGrove(IdentifierType.SPEC_RELATION_TYPE).getByNativeKeysOrElseThrow(nativeKey);
   }

   /**
    * Gets the {@link SynchronizationArtifactBuilder} for the artifact type this {@link SynchronizationArtifact} was
    * created for.
    *
    * @return the {@link SynchronizationArtifactBuilder} for the artifact type.
    */

   SynchronizationArtifactBuilder getSynchronizationArtifactBuilder() {
      return this.synchronizationArtifactBuilder;
   }

   /**
    * Recursively adds an OSEE artifact and its hierarchical children to the Synchronization Artifact.
    *
    * @param artifactReadable the OSEE native artifact to be added
    * @param specificationIdentifier the {@link Identifier} of the Synchronization Artifact SpecificationGroveThing the
    * OSEE artifact is to be added to.
    * @param parentIdentifier the {@link Identifier} of the Synchronization Artifact SpecificationGroveThing or
    * SpecObjectGroveThing that is the hierarchical parent of the OSEE artifact to be added.
    */

   private void processArtifactReadable(ArtifactReadable artifactReadable, GroveThing specificationGroveThing, GroveThing parentCommonObjectGroveThing) {
      //@formatter:off

      var specRelationTypeGrove = this.getForest().getGrove(IdentifierType.SPEC_RELATION_TYPE);

      //Synchronization Artifact DOM Building

      var specObjectGroveThing =
         this.createCommonObject
            (
               artifactReadable,
               IdentifierType.SPEC_OBJECT,
               specificationGroveThing,
               parentCommonObjectGroveThing
            );

      this.getForest().getGrove( IdentifierType.SPEC_OBJECT ).add( specObjectGroveThing );

      /*
       * Scan the artifact's relationship types looking for ones that haven't been encountered before. For new relationship
       * types, create the SpecRelationTypeGroveThings. The relationships are not processed until after all of the SpecObjectGroveThings
       * have been created so that the relationship end points can be found.
       */

      artifactReadable.getExistingRelationTypes().stream()
         .filter
            (
              ( nativeRelationTypeToken  ) -> !specRelationTypeGrove.containsByNativeKeys( nativeRelationTypeToken.getId() )
            )
         .forEach
            (
               ( nativeRelationTypeToken ) -> this.getOrCreateCommonObjectType
                                                 (
                                                    ArtifactTypeTokens.createSpecRelationTypeArtifactTypeToken( nativeRelationTypeToken ),
                                                    IdentifierType.SPEC_RELATION_TYPE
                                                 )
            );


      /*
       * Recursively process child artifacts
       */

      artifactReadable.getChildren()
         .forEach
            (
               ( childArtifactReadable ) -> this.processArtifactReadable
                                               (
                                                 childArtifactReadable,
                                                 specificationGroveThing,
                                                 specObjectGroveThing
                                               )
            );
      //@formatter:on
   }

   /**
    * Creates the Synchronization Artifact {@link DataTypeDefintion} things for each SpecificationGroveThing or
    * SpecObjectGroveThing {@link AttributeDefinitionGroveThing} that is needed for the Synchronization Artifact.
    */

   //@formatter:off
   private void processAttributeDefinitionGrove() {
      var attributeDefinitionGrove = this.forest.getGrove( IdentifierType.ATTRIBUTE_DEFINITION );
      var dataTypeDefinitionGrove  = this.forest.getGrove( IdentifierType.DATA_TYPE_DEFINITION );

      attributeDefinitionGrove.stream().forEach( ( attributeDefinitionGroveThing ) -> {

         var attributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

         var nativeDataTypeKey = this.nativeDataTypeKeyFactory.createOrGetKey( attributeTypeToken );

         var dataTypeDefinitionGroveThing =
            dataTypeDefinitionGrove
               .getByNativeKeys
                   (
                      nativeDataTypeKey
                   )
               .orElseGet
                   (
                      () ->
                      {
                         var newDataTypeDefinitionGroveThing = this.forest.createGroveThing( IdentifierType.DATA_TYPE_DEFINITION ).setNativeThings( nativeDataTypeKey );

                         if( attributeTypeToken.isEnumerated() )
                         {
                            this.processEnumeratedDataTypeDefinition( newDataTypeDefinitionGroveThing, attributeTypeToken );
                         }

                        return dataTypeDefinitionGrove.add( newDataTypeDefinitionGroveThing );
                      }
                   );

         attributeDefinitionGroveThing.setLinkScalar( IdentifierType.DATA_TYPE_DEFINITION, dataTypeDefinitionGroveThing );

      });
   }
   //@formatter:on

   /**
    * Creates the Synchronization Artifact {@link AttributeDefinitionGroveThing} things for the Synchronization
    * Artifact's {@link SpecificationTypeGroveThing} and {@link SpecObjectTypeGroveThing} things.
    */

   private void processCommonObjectTypeContainerMap() {
      //@formatter:off

      var attributeDefinitionGrove = this.forest.getGrove( IdentifierType.ATTRIBUTE_DEFINITION );

      this.commonObjectTypeContainerMap.stream()
         .forEach
            (
               ( commonObjectTypeGroveThing ) ->
               {
                  var artifactTypeToken = (ArtifactTypeToken) commonObjectTypeGroveThing.getNativeThing();

                  artifactTypeToken.getValidAttributeTypes()
                     .forEach
                        (
                           ( attributeTypeToken ) ->
                           {
                              var attributeDefinitionGroveThing = this.getForest().createGroveThing( IdentifierType.ATTRIBUTE_DEFINITION, commonObjectTypeGroveThing );

                              //attributeDefinitionGroveThing.setNativeThings( commonObjectTypeGroveThing.getIdentifier().getType(), artifactTypeToken, attributeTypeToken );
                              attributeDefinitionGroveThing.setNativeThings( attributeTypeToken );

                              commonObjectTypeGroveThing.setLinkVectorElement( IdentifierType.ATTRIBUTE_DEFINITION, attributeDefinitionGroveThing );

                              attributeDefinitionGrove.add( attributeDefinitionGroveThing );
                           }
                        );
               }
            );
      //@formatter:on
   }

   /**
    * Creates the Synchronization Artifact {@link EnumValueGroveThing} things for enumerated data type definitions.
    *
    * @param dataTypeDefinitionGroveThing the Synchronization Artifact {@link DataTypeDefinitionGroveThing} that
    * contains the enumerated values.
    * @param attributeTypeToken the native {@link AttributeTypeToken} that defines the enumeration members.
    */

   private void processEnumeratedDataTypeDefinition(GroveThing dataTypeDefinitionGroveThing, AttributeTypeToken attributeTypeToken) {

      assert (attributeTypeToken.isEnumerated());

      var enumValueGrove = this.forest.getGrove(IdentifierType.ENUM_VALUE);

      //@formatter:off
      attributeTypeToken.toEnum().getEnumValues().forEach(
         (enumToken) ->
         {
            dataTypeDefinitionGroveThing.setLinkVectorElement
               (
                  IdentifierType.ENUM_VALUE,
                  enumValueGrove.add
                     (
                       this.forest.createGroveThing(IdentifierType.ENUM_VALUE).setNativeThings(attributeTypeToken,enumToken)
                     )
               );
         }) ;
      //@formatter:on
   }

   /**
    * Gathers the OSEE artifacts needed for a SpecificationGroveThing in the Synchronization Artifact.
    *
    * @param root the root OSEE artifact for a SpecificationGroveThing in the Synchronization Artifact.
    */

   private void processRootArtifact(Root root) {

      /*
       * Get the native OSEE root object for the specification
       */

      //@formatter:off
      ResultSet<ArtifactReadable> artifactReadableSet =
         orcsApi
            .getQueryFactory()
            .fromBranch( root.getBranchId() )
            .andId( root.getArtifactId() )
            .getResults();
      //@formatter:on

      var rootArtifactReadable = artifactReadableSet.getExactlyOne();

      /*
       * Start a specification with the OSEE root object
       */

      var specificationGroveThing =
         this.createCommonObject(rootArtifactReadable, IdentifierType.SPECIFICATION, null, null);

      this.getForest().getGrove(IdentifierType.SPECIFICATION).add(specificationGroveThing);
      this.getForest().getGrove(IdentifierType.SPEC_OBJECT).add(specificationGroveThing);

      /*
       * Add the children of the root OSEE object to the Synchronization Artifact
       */

      //@formatter:off
      rootArtifactReadable.getChildren()
         .forEach
            (
               ( childArtifactReadable ) -> this.processArtifactReadable
                                               (
                                                 childArtifactReadable,
                                                 specificationGroveThing,
                                                 specificationGroveThing
                                               )
            );
      //@formatter:on

   }

   /**
    * Create the Attribute Value things needed for the Specification, Spec Object, and Spec Relation things
    */

   private void processCommonObjectTypeGroveThings() {
      //@formatter:off

      var attributeValueGrove    = this.forest.getGrove(IdentifierType.ATTRIBUTE_VALUE);
      var enumValueGrove         = this.forest.getGrove(IdentifierType.ENUM_VALUE);

      /*
       * The SpecObjectGrove contains all of the SpecificationGroveThings and SpecObjectGroveThings. A combined stream from
       * the Spec Object Grove and the Spec Relation Grove will contain all the CommonObjectGroveThing objects needed for the
       * Synchronization Artifact.
       */

      Arrays.stream
         (
            new IdentifierType[]
            {
               IdentifierType.SPEC_OBJECT,
               IdentifierType.SPEC_RELATION,
               IdentifierType.SPECTER_SPEC_OBJECT
            }
         )
         .map( this.getForest()::getGrove )
         .flatMap( Grove::stream )
         .forEach
         (
            ( groveThing ) ->
            {
               var nativeArtifactReadable = (ArtifactReadable) groveThing.getNativeThing();

               /*
                * The CommonObjectTypeGroveThing contains a list of the attributes that are defined for the
                * SpecificationGroveThing or SpecObjectGroveThing thing
                */

               groveThing.getLinkScalar( groveThing.getIdentifier().getType().getAssociatedType() ).ifPresentOrElse
                  (
                     ( commonObjectTypeGroveThing ) ->
                     {

                        commonObjectTypeGroveThing.streamLinks(IdentifierType.ATTRIBUTE_DEFINITION).forEach
                           (
                              ( attributeDefinitionGroveThing ) ->
                              {

                                 var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

                                 /*
                                  * Create a new empty AttributeValueGroveThing; attach the parent SpecificationGroveThing or
                                  * SpecObjectGroveThing; and attach the AttributeDefinitionGroveThing for the value.
                                  */

                                 var attributeValueGroveThing = this.forest.createGroveThing(IdentifierType.ATTRIBUTE_VALUE, groveThing);

                                 attributeValueGroveThing.setLinkScalar(IdentifierType.ATTRIBUTE_DEFINITION, attributeDefinitionGroveThing);

                                 /*
                                  * Attach the OSEE native attribute value to the attributeValueGroveThing
                                  */

                                 try
                                 {
                                 if( ( (NativeDataTypeKey) attributeDefinitionGroveThing.getLinkScalar( IdentifierType.DATA_TYPE_DEFINITION).get().getNativeThing()).isEnumerated() )
                                 {

                                    /*
                                     * Enumerations may have multiple values and also require a reference to the data type
                                     * definition for the enumeration member(s).
                                     */

                                    var nativeAttributeValueList = nativeArtifactReadable.getAttributeValues(nativeAttributeTypeToken);
                                    var enumValueGroveThingList  = new ArrayList<GroveThing>(nativeAttributeValueList.size());
                                    var nativeAttributeTypeEnum  = nativeAttributeTypeToken.toEnum();

                                    nativeAttributeValueList.forEach
                                       (
                                          ( oseeAttributeValueEnumerationMemberString ) ->
                                          {
                                             var ordinal             = nativeAttributeTypeEnum.getEnumOrdinal( (String) oseeAttributeValueEnumerationMemberString );
                                             var enumValueGroveThing = enumValueGrove.getByNativeKeys( nativeAttributeTypeToken.getId(), ordinal );

                                             enumValueGroveThingList.add(enumValueGroveThing.get());
                                          }
                                       );

                                    /*
                                     * For enumerations the native thing is a list (possibly empty) of references to the
                                     * enumeration member data type definitions.
                                     */

                                    attributeValueGroveThing.setNativeThings(enumValueGroveThingList);

                                 }
                                 else
                                 {
                                    /*
                                     * Non-enumerated value. A single value for the attribute is expected. An exception will
                                     * be thrown if the attribute does not have a value or has more than one value.
                                     */

                                    var nativeAttributeValue = nativeArtifactReadable.getSoleAttributeValue(nativeAttributeTypeToken);

                                    attributeValueGroveThing.setNativeThings(nativeAttributeValue);
                                 }

                                 /*
                                  * The attributeValueGroveThing is added to the grove as the last thing so that any
                                  * exceptions that might occur during the attribute value processing will just result in the
                                  * attribute value being skipped instead of an incomplete attributeValueGroveThing having
                                  * been added to the grove.
                                  */

                                 attributeValueGrove.add(attributeValueGroveThing);
                                 }
                                 catch( UnknownAttributeTypeTokenException uatte )
                                 {
                                    throw uatte;
                                 }
                                 catch( MultipleAttributesExist mae )
                                 {
                                    //eat it
                                 }
                                 catch( AttributeDoesNotExist adne )
                                 {
                                    //eat it
                                 }
                                 catch( Exception e )
                                 {
                                    throw new RuntimeException( "\nUnexpected exception accessing the attribute values of an ArtifactReadable.\n", e );
                                 }
                              }
                           );
                     },

                     () ->
                     {
                        var message = new StringBuilder(1024);

                        message
                           .append( "\n" )
                           .append( "CommonObjectGroveThing does not have expected link to a CommonObjectTypeGroveThing." ).append( "\n" )
                           .append( "   Identifier: " ).append( groveThing.getIdentifier() ).append( "\n" )
                           ;

                        groveThing.toMessage( 1, message );

                        throw new RuntimeException(message.toString());
                     }
                  );
            }
         );
      //@formatter:on
   }

   /**
    * Creates the Relationship things needed for Spec Objects.
    */

   private void processSpecObjectGroveForRelationships() {
      //@formatter:off
      this.getForest().getGrove( IdentifierType.SPEC_OBJECT ).stream()

         /*
          * Filter out Specifications
          */

         .filter
            (
               ( groveThing ) -> groveThing.isType( IdentifierType.SPEC_OBJECT )
            )

         /*
          * For each SpecObject in the grove, regardless of the Specification
          */

         .forEach
            (
               ( specObjectGroveThing ) ->

                  /*
                   * For each type of relationship that has at least one relationship defined
                   */

                  ((ArtifactReadable) specObjectGroveThing.getNativeThing()).getExistingRelationTypes()
                     .forEach
                        (
                           ( nativeRelationTypeToken ) ->
                           {
                              this.createRelationshipsForSide( RelationSide.SIDE_A, specObjectGroveThing, nativeRelationTypeToken );
                              this.createRelationshipsForSide( RelationSide.SIDE_B, specObjectGroveThing, nativeRelationTypeToken );
                           }
                        )
            );
      //@formatter:on
   }

   /**
    * Creates an {@link InputStream} the Synchronization Artifact can be read from.
    *
    * @return an {@link InputStream} containing the Synchronization Artifact.
    */

   public InputStream serialize() {
      return this.synchronizationArtifactBuilder.serialize();
   }

   /**
    * Generates a debugging message with a summary of the parts composing the Synchronization Artifact. There is no
    * contract for the message contents or structure.<br>
    * <br>
    * {@inheritDoc}
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {

      StringBuilder outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      String indent0 = IndentedString.indentString(0);

      //@formatter:off
      outMessage
         .append( indent0 ).append( "Root List:" ).append( "\n" )
         ;

      this.rootList.toMessage( indent + 1, outMessage );
      //@formatter:on

      return outMessage;
   }

   /**
    * Returns the message generated by the method {@link #toMessage} with an indent level of zero. {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}

/* EOF */
