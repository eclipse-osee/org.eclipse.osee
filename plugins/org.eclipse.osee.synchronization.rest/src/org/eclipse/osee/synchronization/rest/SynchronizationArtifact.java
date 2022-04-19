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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.rest.forest.AttributeDefinitionGrove;
import org.eclipse.osee.synchronization.rest.forest.AttributeDefinitionGroveThing;
import org.eclipse.osee.synchronization.rest.forest.AttributeValueGrove;
import org.eclipse.osee.synchronization.rest.forest.AttributeValueGroveThing;
import org.eclipse.osee.synchronization.rest.forest.CommonObjectGroveThing;
import org.eclipse.osee.synchronization.rest.forest.CommonObjectTypeGrove;
import org.eclipse.osee.synchronization.rest.forest.CommonObjectTypeGroveThing;
import org.eclipse.osee.synchronization.rest.forest.DataTypeDefinitionGrove;
import org.eclipse.osee.synchronization.rest.forest.DataTypeDefinitionGroveThing;
import org.eclipse.osee.synchronization.rest.forest.EnumValueGrove;
import org.eclipse.osee.synchronization.rest.forest.EnumValueGroveThing;
import org.eclipse.osee.synchronization.rest.forest.Forest;
import org.eclipse.osee.synchronization.rest.forest.HeaderGrove;
import org.eclipse.osee.synchronization.rest.forest.HeaderGroveThing;
import org.eclipse.osee.synchronization.rest.forest.SpecObjectGrove;
import org.eclipse.osee.synchronization.rest.forest.SpecObjectGroveThing;
import org.eclipse.osee.synchronization.rest.forest.SpecObjectTypeGroveThing;
import org.eclipse.osee.synchronization.rest.forest.SpecTypeGroveThing;
import org.eclipse.osee.synchronization.rest.forest.SpecificationGrove;
import org.eclipse.osee.synchronization.rest.forest.SpecificationGroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.osee.synchronization.rest.nativedatatype.NativeDataTypeKey;
import org.eclipse.osee.synchronization.rest.nativedatatype.NativeDataTypeKeyFactory;
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

   private final Map<Long, CommonObjectTypeContainer> commonObjectTypeContainerMap;

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

      this.rootList = rootList;
      this.synchronizationArtifactBuilder = synchronizationArtifactBuilder;
      this.orcsApi = rootList.getOrcsApi();
      this.commonObjectTypeContainerMap = new HashMap<>();
      this.forest = new Forest();
      this.nativeDataTypeKeyFactory = new NativeDataTypeKeyFactory();
   }

   /**
    * Factory method to create a empty Synchronization Artifact.
    *
    * @param rootList a list of the OSEE artifacts to be included in the Synchronization Artifact.
    * @return an empty {@link SynchronizationArtifact}.
    * @throws UnknownArtifactTypeExceptionImpl when a {@link SynchronizationArtifactBuilder} could not be created for
    * the artifact type.
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

      var headerGroveThing = (HeaderGroveThing) this.forest.createGroveThing(IdentifierType.HEADER);
      headerGroveThing.setOrcsApi(this.orcsApi);
      headerGroveThing.setRootListImpl(this.rootList);
      ((HeaderGrove) this.forest.getGrove(IdentifierType.HEADER)).add(headerGroveThing);

      //@formatter:off
      /*
       * This step completes the following:
       *    1) Gather the OSEE native artifacts for each of the Synchronization Artifact
       *       document roots.
       *    2) Populate the Specification Grove with SpecificationGroveThings
       *    3) Populate the Spec Object Grove with both SpecificationGroveThings and SpecObjectGroveThings
       *    4) Create SpecTypeGroveThings and populate the SpecTypeGrove for each unique specification type
       *    5) Create SpecObjectTpeGroveThings and populate the SpecObjectTypeGrove for each unique spec object type
       */
      //@formatter:on

      this.rootList.forEach(this::processRootArtifact);

      /*
       * Create the Attribute Definitions for all of the unique SpecificationGroveThing Type and SpecObjectGroveThing
       * Types.
       */

      this.processCommonObjectTypeContainerMap();

      /*
       * Create the Data Type Definitions needed for the Attribute Definitions.
       */

      this.processAttributeDefinitionGrove();

      /*
       * Create the Attribute Value things needed for the SpecificationGroveThing and Spec Object things.
       */

      this.processSpecObjectGrove();

      /*
       * Create foreign things for all of the native things in each grove. The forest stream is an ordered stream. Grove
       * streams are unordered.
       */

      //@formatter:off
      this.forest.stream().forEach
         (
            ( grove ) -> this.getSynchronizationArtifactBuilder().getConverter( grove.getType() ).ifPresent
                            (
                              ( converter ) -> grove.streamDeep().forEach( converter::accept )
                            )
         );
      //@formatter:on

      /*
       * Assemble the final Synchronization Artifact.
       */

      this.synchronizationArtifactBuilder.build(this);
   }

   /**
    * Finds in the {@link #commonObjectTypeContainerMap} or creates a new {@link SpecTypeGroveThing} or
    * {@link SpecObjectTypeGroveThing} for the OSEE artifact type.
    *
    * @param identifierType the {@link IdentifierType} indicating whether to get a {@link SpecTypeGroveThing} or
    * {@link SpecObjectTypeGroveThing} thing.
    * @param artifactTypeToken the OSEE artifact type.
    * @return the found or newly created {@link SpecTypeGroveThing} or {@link SpecObjectTypeGroveThing} Synchronization
    * Artifact thing for the provided {@link SpecificationGroveThing} or {@link SpecObjectGroveThing}.
    */

   private CommonObjectTypeGroveThing createOrGetCommonObjectType(IdentifierType identifierType, ArtifactTypeToken artifactTypeToken) {

      var commonObjectTypeContainer = this.commonObjectTypeContainerMap.get(artifactTypeToken.getId());

      if ((commonObjectTypeContainer != null) && commonObjectTypeContainer.hasType(identifierType)) {
         return commonObjectTypeContainer.get(identifierType);
      }

      CommonObjectTypeGroveThing commonObjectTypeGroveThing = this.forest.createGroveThing(identifierType);
      commonObjectTypeGroveThing.setNativeThings(artifactTypeToken);

      if (commonObjectTypeContainer == null) {
         commonObjectTypeContainer = new CommonObjectTypeContainer(commonObjectTypeGroveThing);
         this.commonObjectTypeContainerMap.put(artifactTypeToken.getId(), commonObjectTypeContainer);
      } else {
         commonObjectTypeContainer.add(commonObjectTypeGroveThing);
      }

      ((CommonObjectTypeGrove) this.forest.getGrove(identifierType)).add(commonObjectTypeGroveThing);

      return commonObjectTypeGroveThing;
   }

   /**
    * Creates a Synchronization Artifact {@link SpecificationGroveThing} or {@link SpecObjectGroveThing} and the
    * associated {@link SpecTypeGroveThing} or {@link SpecObjectTypeGroveThing} things from an OSEE artifact.
    *
    * @param artifactReadable the native OSEE object to create an {@link SpecificationGroveThing} or
    * {@link SpecObjectGroveThing} for.
    * @param identifierType indicates the type of Synchronization Artifact things to be created.
    * @return the {@link CommonObjectGroveThing} for the Synchronization Artifact {@link SpecificationGroveThing}
    * or @{link SpecObjectGroveThing} that was created.
    */

   private CommonObjectGroveThing createCommonObject(ArtifactReadable artifactReadable, IdentifierType identifierType, SpecificationGroveThing specificationGroveThing, CommonObjectGroveThing parentCommonObjectGroveThing) {

      CommonObjectTypeGroveThing commonObjectTypeGroveThing;

      //@formatter:off
      CommonObjectGroveThing commonObjectGroveThing =
         identifierType == IdentifierType.SPECIFICATION
            ? this.forest.createGroveThing(identifierType)
            : new SpecObjectGroveThing( specificationGroveThing, parentCommonObjectGroveThing );
      //@formatter:on

      commonObjectGroveThing.setNativeThings(artifactReadable);

      var artifactTypeToken = artifactReadable.getArtifactType();

      commonObjectTypeGroveThing = this.createOrGetCommonObjectType(
         identifierType == IdentifierType.SPEC_OBJECT ? IdentifierType.SPEC_OBJECT_TYPE : IdentifierType.SPECIFICATION_TYPE,
         artifactTypeToken);

      commonObjectGroveThing.setCommonObjectType(commonObjectTypeGroveThing);

      return commonObjectGroveThing;
   }

   /**
    *
    */

   public Forest getForest() {
      return this.forest;
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
    * @param indent the hierarchy level for debug messages
    * @param artifactReadable the OSEE native artifact to be added
    * @param specificationIdentifier the {@link Identifier} of the Synchronization Artifact SpecificationGroveThing the
    * OSEE artifact is to be added to.
    * @param parentIdentifier the {@link Identifier} of the Synchronization Artifact SpecificationGroveThing or
    * SpecObjectGroveThing that is the hierarchical parent of the OSEE artifact to be added.
    */

   private void processArtifactReadable(int indent, ArtifactReadable artifactReadable, SpecificationGroveThing specificationGroveThing, CommonObjectGroveThing parentCommonObjectGroveThing) {
      //@formatter:off

      //Synchronization Artifact DOM Building

      var specObject =
         this.createCommonObject
            (
               artifactReadable,
               IdentifierType.SPEC_OBJECT,
               specificationGroveThing,
               parentCommonObjectGroveThing
            );

      ((SpecObjectGrove) this.forest.getGrove(IdentifierType.SPEC_OBJECT)).add(specObject);

      artifactReadable.getChildren().forEach( ( childArtifactReadable ) ->

         this.processArtifactReadable
            (
               indent + 1,
               childArtifactReadable,
               specificationGroveThing,
               specObject
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
      AttributeDefinitionGrove attributeDefinitionGrove = this.forest.getGrove( IdentifierType.ATTRIBUTE_DEFINITION );
      DataTypeDefinitionGrove  dataTypeDefinitionGrove  = this.forest.getGrove( IdentifierType.DATA_TYPE_DEFINITION );

      attributeDefinitionGrove.streamDeep().forEach( ( attributeDefinitionGroveThing ) -> {

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
                         var newDataTypeDefinitionGroveThing = (DataTypeDefinitionGroveThing) this.forest.createGroveThing( IdentifierType.DATA_TYPE_DEFINITION ).setNativeThings( nativeDataTypeKey );

                         if( attributeTypeToken.isEnumerated() )
                         {
                            this.processEnumeratedDataTypeDefinition( newDataTypeDefinitionGroveThing, attributeTypeToken );
                         }

                        return dataTypeDefinitionGrove.add( newDataTypeDefinitionGroveThing );
                      }
                   );

         ((AttributeDefinitionGroveThing) attributeDefinitionGroveThing).setDataTypeDefinition( (DataTypeDefinitionGroveThing) dataTypeDefinitionGroveThing );



      });
   }
   //@formatter:on

   /**
    * Creates the Synchronization Artifact {@link AttributeDefinitionGroveThing} things for the Synchronization
    * Artifact's {@link SpecTypeGroveThing} and {@link SpecObjectTypeGroveThing} things.
    */

   private void processCommonObjectTypeContainerMap() {

      AttributeDefinitionGrove attributeDefinitionGrove = this.forest.getGrove(IdentifierType.ATTRIBUTE_DEFINITION);

      this.commonObjectTypeContainerMap.values().forEach(commonObjectTypeContainer -> {

         var commonObjectTypes = commonObjectTypeContainer.get();

         var artifactTypeToken = commonObjectTypeContainer.getArtifactTypeToken();

         var attributeTypeTokenList = artifactTypeToken.getValidAttributeTypes();

         attributeTypeTokenList.forEach(attributeTypeToken -> {

            //Attributes are unique (contained by) to Specifications and Spec-Objects.

            commonObjectTypes.forEach(commonObjectTypeGroveThing -> {

               var attributeDefinitionGroveThing =
                  (AttributeDefinitionGroveThing) this.forest.createGroveThing(IdentifierType.ATTRIBUTE_DEFINITION,
                     commonObjectTypeGroveThing);

               attributeDefinitionGroveThing.setNativeThings(artifactTypeToken, attributeTypeToken);

               commonObjectTypeGroveThing.add(attributeDefinitionGroveThing);

               attributeDefinitionGrove.add(attributeDefinitionGroveThing);
            });
         });
      });
   }

   /**
    * Creates the Synchronization Artifact {@link EnumValueGroveThing} things for enumerated data type definitions.
    *
    * @param dataTypeDefinitionGroveThing the Synchronization Artifact {@link DataTypeDefinitionGroveThing} that
    * contains the enumerated values.
    * @param attributeTypeToken the native {@link AttributeTypeToken} that defines the enumeration members.
    */

   private void processEnumeratedDataTypeDefinition(DataTypeDefinitionGroveThing dataTypeDefinitionGroveThing, AttributeTypeToken attributeTypeToken) {

      assert (attributeTypeToken.isEnumerated());

      var enumValueGrove = this.forest.getGrove(IdentifierType.ENUM_VALUE);

      //@formatter:off
      attributeTypeToken.toEnum().getEnumValues().forEach(
         (enumToken) ->
         {
            dataTypeDefinitionGroveThing.addEnumerationMember
               (
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
      SpecificationGrove specificationGrove = this.forest.getGrove(IdentifierType.SPECIFICATION);
      SpecObjectGrove specObjectGrove = this.forest.getGrove(IdentifierType.SPEC_OBJECT);

      //Get the native OSEE root object for the specification
      BranchId branchId = root.getBranchId();
      ArtifactId artifactId = root.getArtifactId();

      ResultSet<ArtifactReadable> artifactReadableSet =
         orcsApi.getQueryFactory().fromBranch(branchId).andId(artifactId).getResults();

      ArtifactReadable rootArtifactReadable = artifactReadableSet.getExactlyOne();

      //Start a specification with the OSEE root object

      var specification = (SpecificationGroveThing) this.createCommonObject(rootArtifactReadable,
         IdentifierType.SPECIFICATION, null, null);

      specificationGrove.add(specification);
      specObjectGrove.add(specification);

      //Add the children of the root OSEE object to the Synchronization Artifact
      //@formatter:off
      rootArtifactReadable.getChildren().forEach( ( childArtifactReadable ) ->

         this.processArtifactReadable
            (
               1,
               childArtifactReadable,
               specification,
               specification
            )

      );
      //@formatter:on

   }

   /**
    * Create the Attribute Value things needed for the SpecificationGroveThing and Spec Object things
    */

   //@formatter:off
   private void processSpecObjectGrove() {

      SpecObjectGrove     specObjectGrove     = this.forest.getGrove(IdentifierType.SPEC_OBJECT);
      AttributeValueGrove attributeValueGrove = this.forest.getGrove(IdentifierType.ATTRIBUTE_VALUE);
      EnumValueGrove      enumValueGrove      = this.forest.getGrove(IdentifierType.ENUM_VALUE);

      /*
       * The SpecObjectGrove contains SpecificationGroveThings as the root things and SpecObjectGroveThings things as
       * children of the SpecificationGroveThings.
       */

      specObjectGrove.streamDeep().forEach
         (
            ( groveThing ) ->
            {
               var commonObjectGroveThing = (CommonObjectGroveThing) groveThing;
               var nativeArtifactReadable = (ArtifactReadable) commonObjectGroveThing.getNativeThing();

               /*
                * The CommonObjectTypeGroveThing contains a list of the attributes that are defined for the
                * SpecificationGroveThing or SpecObjectGroveThing thing
                */

               var commonObjectTypeGroveThing = commonObjectGroveThing.getCommonObjectType();

               commonObjectTypeGroveThing.streamAttributeDefinitions().forEach
                  (
                     ( attributeDefinitionGroveThing ) ->
                     {

                        var nativeAttributeTypeToken = (AttributeTypeToken) attributeDefinitionGroveThing.getNativeThing();

                        try {

                           /*
                            * Create a new empty AttributeValueGroveThing; attach the parent SpecificationGroveThing or SpecObjectGroveThing; and attach the
                            * AttributeDefinitionGroveThing for the value.
                            */

                           var attributeValueGroveThing = (AttributeValueGroveThing) this.forest.createGroveThing(IdentifierType.ATTRIBUTE_VALUE);

                           attributeValueGroveThing.setParent(commonObjectGroveThing);
                           attributeValueGroveThing.setAttributeDefinition(attributeDefinitionGroveThing);

                           /*
                            * Attach the OSEE native attribute value to the attributeValueGroveThing
                            */

                           if (((NativeDataTypeKey) attributeDefinitionGroveThing.getDataTypeDefinition().getNativeThing()).isEnumerated()) {

                              /*
                               * Enumerations may have multiple values and also require a reference to the data type definition for the
                               * enumeration member(s).
                               */

                              var nativeAttributeValueList = nativeArtifactReadable.getAttributeValues( nativeAttributeTypeToken );
                              var enumValueGroveThingList  = new ArrayList<GroveThing>( nativeAttributeValueList.size() );
                              var nativeAttributeTypeEnum  = nativeAttributeTypeToken.toEnum();

                              nativeAttributeValueList.forEach
                                 (
                                    ( oseeAttributeValueEnumerationMemberString ) ->
                                    {
                                       var ordinal             = nativeAttributeTypeEnum.getEnumOrdinal( (String) oseeAttributeValueEnumerationMemberString );
                                       var enumValueGroveThing = enumValueGrove.getByNativeKeys( nativeAttributeTypeToken.getId(), ordinal );

                                       enumValueGroveThingList.add( enumValueGroveThing.get() );
                                    }
                                 );

                              /*
                               * For enumerations the native thing is a list (possibly empty) of references to the enumeration member
                               * data type definitions.
                               */

                              attributeValueGroveThing.setNativeThings( enumValueGroveThingList );
                           }
                           else
                           {
                              /*
                               * Non-enumerated value. A single value for the attribute is expected. An exception will be thrown if the
                               * attribute does not have a value or has more than one value.
                               */

                              var nativeAttributeValue = nativeArtifactReadable.getSoleAttributeValue( nativeAttributeTypeToken );

                              attributeValueGroveThing.setNativeThings( nativeAttributeValue );
                           }

                           /*
                            * The attributeValueGroveThing is added to the grove as the last thing so that any exceptions that might occur during
                            * the attribute value processing will just result in the attribute value being skipped instead of an incomplete attributeValueGroveThing
                            * having been added to the grove.
                            */

                           attributeValueGrove.add(attributeValueGroveThing);

                        } catch (Exception e) {
                           /*
                            * Just skip the attribute for now if a value is not available or multiple values are available.
                            */
                        }

                     }
                  );

            }
         );
   }
   //@formatter:on

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
         .append( "\n" )
         .append( "\n" )
         .append( "\n" )
         .append( indent0 ).append( "DOM Dump:" ).append( "\n" )
         ;
      //@formatter:on

      this.forest.stream().forEach(grove -> ((ToMessage) grove).toMessage(1, outMessage));

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
