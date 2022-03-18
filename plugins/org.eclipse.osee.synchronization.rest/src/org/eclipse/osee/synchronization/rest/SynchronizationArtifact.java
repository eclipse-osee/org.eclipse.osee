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
import java.util.EnumMap;
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
import org.eclipse.osee.synchronization.util.EnumSupplierMap;
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
    * Handle to the OSEE ORCS API used to obtain OSEE artifacts.
    */

   private final OrcsApi orcsApi;

   /**
    * A {@link Map} of the Synchronization Artifact {@link Grove} objects by {@link IdentifierType}.
    */

   private final Map<IdentifierType, Grove> groveMap;

   /**
    * A {@link Map} for factory functions to create the {@link GroveThing} objects associated with each
    * {@link IdentifierType}.
    */

   private final EnumSupplierMap<IdentifierType, GroveThing> groveThingFactoryMap;

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
    * Creates a new empty SynchronizationArtifact.
    *
    * @param rootList the Synchronization Artifact building instructions.
    * @param synchronizationArtifactBuilder the {@link SynchronizationArtifactBuilder} for the type of Synchronization
    * Artifact to be built.
    */

   private SynchronizationArtifact(RootList rootList, SynchronizationArtifactBuilder synchronizationArtifactBuilder) {

      assert Objects.nonNull(rootList) && Objects.nonNull(synchronizationArtifactBuilder);

      this.rootList = rootList;
      this.synchronizationArtifactBuilder = synchronizationArtifactBuilder;

      this.orcsApi = rootList.getOrcsApi();
      assert Objects.nonNull(this.orcsApi);

      this.commonObjectTypeContainerMap = new HashMap<>();

      var groveMap = new EnumMap<IdentifierType, Grove>(IdentifierType.class) {

         /**
          * Serialization identifier
          */

         private static final long serialVersionUID = 1L;

         /**
          * Extracts the grove type from the grove and uses that as the map key to store the grove by.
          *
          * @param grove the {@link Grove} to be saved in the map.
          */

         public void put(Grove grove) {
            this.put(grove.getType(), grove);
         }
      };

      this.groveMap = groveMap;
      groveMap.put(new AttributeDefinitionGrove());
      groveMap.put(new AttributeValueGrove());
      groveMap.put(new DataTypeDefinitionGrove());
      groveMap.put(new HeaderGrove());
      groveMap.put(new SpecObjectGrove());
      groveMap.put(new SpecObjectTypeGrove());
      groveMap.put(new SpecificationGrove());
      groveMap.put(new SpecTypeGrove());

      this.groveThingFactoryMap = new EnumSupplierMap<>(IdentifierType.class);
      this.groveThingFactoryMap.put(IdentifierType.ATTRIBUTE_DEFINITION, AttributeDefinitionGroveThing::new);
      this.groveThingFactoryMap.put(IdentifierType.ATTRIBUTE_VALUE, AttributeValueGroveThing::new);
      this.groveThingFactoryMap.put(IdentifierType.DATA_TYPE_DEFINITION, DataTypeDefinitionGroveThing::new);
      this.groveThingFactoryMap.put(IdentifierType.HEADER, HeaderGroveThing::new);
      this.groveThingFactoryMap.put(IdentifierType.SPEC_OBJECT, SpecObjectGroveThing::new);
      this.groveThingFactoryMap.put(IdentifierType.SPEC_OBJECT_TYPE, SpecObjectTypeGroveThing::new);
      this.groveThingFactoryMap.put(IdentifierType.SPECIFICATION, SpecificationGroveThing::new);
      this.groveThingFactoryMap.put(IdentifierType.SPECIFICATION_TYPE, SpecTypeGroveThing::new);
   }

   /**
    * Factory method to create a empty Synchronization Artifact.
    *
    * @param rootList a list of the OSEE artifacts to be included in the Synchronization Artifact.
    * @return an empty {@link SynchronizationArtifact}.
    * @throws UnknownArtifactTypeExceptionImpl when a {@link SynchronizationArtifactBuilder} could not be created for
    * the artifact type.
    */

   public static SynchronizationArtifact create(RootList rootList) throws UnknownSynchronizationArtifactTypeException {

      assert Objects.nonNull(rootList) && (rootList instanceof RootList);

      var rootListImpl = rootList;

      var synchronizationArtifactType = rootListImpl.getSynchronizationArtifactType();

      if (!SynchronizationArtifact.synchronizationArtifactBuilderClassMap.containsKey(synchronizationArtifactType)) {
         throw new UnknownSynchronizationArtifactTypeException(synchronizationArtifactType);
      }

      var synchronizationArtifactBuilder =
         SynchronizationArtifact.getSynchronizationArtifactBuilder(synchronizationArtifactType);

      SynchronizationArtifact synchronizationArtifact =
         new SynchronizationArtifact(rootListImpl, synchronizationArtifactBuilder);

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
       * Create the header
       */

      var header = (HeaderGroveThing) this.createGroveThing(IdentifierType.HEADER);
      header.setOrcsApi(this.orcsApi);
      header.setRootListImpl(this.rootList);
      ((HeaderGrove) this.getGrove(IdentifierType.HEADER)).add(header);

      /*
       * Gather the OSEE artifacts for each SpecificationGroveThing and their SpecObjects.
       */

      this.rootList.forEach(this::processRootArtifact);

      /*
       * Create the Attribute Definitions for all of the unique SpecificationGroveThing Type and SpecObjectGroveThing Types.
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
       * Create foreign things for all of the native things in each grove.
       */

      this.groveMap.values().forEach(grove -> grove.createForeignThings(this.getSynchronizationArtifactBuilder()));

      /*
       * Assemble the final Synchronization Artifact.
       */

      this.synchronizationArtifactBuilder.build(this);
   }

   /**
    * Finds in the {@link #commonObjectTypeContainerMap} or creates a new {@link SpecTypeGroveThing} or {@link SpecObjectTypeGroveThing} for
    * the OSEE artifact type.
    *
    * @param identifierType the {@link IdentifierType} indicating whether to get a {@link SpecTypeGroveThing} or
    * {@link SpecObjectTypeGroveThing} thing.
    * @param artifactTypeToken the OSEE artifact type.
    * @return the found or newly created {@link SpecTypeGroveThing} or {@link SpecObjectTypeGroveThing} Synchronization Artifact thing for
    * the provided {@link SpecificationGroveThing} or {@link SpecObjectGroveThing}.
    */

   private CommonObjectTypeGroveThing createOrGetCommonObjectType(IdentifierType identifierType, ArtifactTypeToken artifactTypeToken) {

      var commonObjectTypeContainer = this.commonObjectTypeContainerMap.get(artifactTypeToken.getId());

      if ((commonObjectTypeContainer != null) && commonObjectTypeContainer.hasType(identifierType)) {
         return commonObjectTypeContainer.get(identifierType);
      }

      CommonObjectTypeGroveThing commonObjectTypeGroveThing = this.createGroveThing(identifierType);
      commonObjectTypeGroveThing.setNativeThing(artifactTypeToken);

      if (commonObjectTypeContainer == null) {
         commonObjectTypeContainer = new CommonObjectTypeContainer(commonObjectTypeGroveThing);
         this.commonObjectTypeContainerMap.put(artifactTypeToken.getId(), commonObjectTypeContainer);
      } else {
         commonObjectTypeContainer.add(commonObjectTypeGroveThing);
      }

      ((CommonObjectTypeGrove) this.getGrove(identifierType)).add(commonObjectTypeGroveThing);

      return commonObjectTypeGroveThing;
   }

   /**
    * Creates a Synchronization Artifact {@link SpecificationGroveThing} or {@link SpecObjectGroveThing} and the associated {@link SpecTypeGroveThing}
    * or {@link SpecObjectTypeGroveThing} things from an OSEE artifact.
    *
    * @param artifactReadable the native OSEE object to create an {@link SpecificationGroveThing} or {@link SpecObjectGroveThing} for.
    * @param identifierType indicates the type of Synchronization Artifact things to be created.
    * @return the {@link CommonObjectGroveThing} for the Synchronization Artifact {@link SpecificationGroveThing} or @{link SpecObjectGroveThing} that
    * was created.
    */

   private CommonObjectGroveThing createCommonObject(ArtifactReadable artifactReadable, IdentifierType identifierType) {
      CommonObjectTypeGroveThing commonObjectTypeGroveThing;
      CommonObjectGroveThing commonObjectGroveThing = this.createGroveThing(identifierType);
      commonObjectGroveThing.setNativeThing(artifactReadable);

      var artifactTypeToken = artifactReadable.getArtifactType();

      commonObjectTypeGroveThing = this.createOrGetCommonObjectType(
         identifierType == IdentifierType.SPEC_OBJECT ? IdentifierType.SPEC_OBJECT_TYPE : IdentifierType.SPECIFICATION_TYPE,
         artifactTypeToken);

      commonObjectGroveThing.setCommonObjectType(commonObjectTypeGroveThing);

      return commonObjectGroveThing;
   }

   /**
    * Gets the {@link Grove} for the Synchronization Artifact {@link GroveThing}s specified by
    * <code>identifierType</code>.
    *
    * @param identifierType specifies the {@link Grove} to get.
    * @return the {@link Grove} for the specified {@link GroveThings}.
    */

   @SuppressWarnings("unchecked")
   public <T extends Grove> T getGrove(IdentifierType identifierType) {
      return (T) this.groveMap.get(identifierType);
   }

   /**
    * Creates a new Synchronization Artifact thing with a unique {@link Identifier} of the class associated with the
    * {@link IdentifierType}.
    *
    * @param identifierType the type of object to create.
    * @return a new Synchronization Artifact thing.
    */

   @SuppressWarnings("unchecked")
   <T extends GroveThing> T createGroveThing(IdentifierType identifierType) {
      return (T) this.groveThingFactoryMap.get(identifierType);
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
    * @param specificationIdentifier the {@link Identifier} of the Synchronization Artifact SpecificationGroveThing the OSEE
    * artifact is to be added to.
    * @param parentIdentifier the {@link Identifier} of the Synchronization Artifact SpecificationGroveThing or SpecObjectGroveThing that is
    * the hierarchical parent of the OSEE artifact to be added.
    */

   private void processArtifactReadable(int indent, ArtifactReadable artifactReadable, Identifier specificationIdentifier, Identifier parentIdentifier) {

      //Synchronization Artifact DOM Building

      var specObject = this.createCommonObject(artifactReadable, IdentifierType.SPEC_OBJECT);
      var specObjectIdentifier = specObject.getGroveThingKey();

      ((SpecObjectGrove) this.getGrove(IdentifierType.SPEC_OBJECT)).add(specificationIdentifier, parentIdentifier,
         specObject);

      artifactReadable.getChildren().forEach(childArtifactReadable -> this.processArtifactReadable(indent + 1,
         childArtifactReadable, specificationIdentifier, specObjectIdentifier));
   }

   /**
    * Creates the Synchronization Artifact {@link DataTypeDefintion} things for each SpecificationGroveThing or SpecObjectGroveThing
    * {@link AttributeDefinitionGroveThing} that is needed for the Synchronization Artifact.
    */

   private void processAttributeDefinitionGrove() {
      AttributeDefinitionGrove attributeDefinitionGrove = this.getGrove(IdentifierType.ATTRIBUTE_DEFINITION);
      DataTypeDefinitionGrove dataTypeDefinitionGrove = this.getGrove(IdentifierType.DATA_TYPE_DEFINITION);

      attributeDefinitionGrove.stream().forEach(attributeDefinition -> {

         var attributeTypeToken = (AttributeTypeToken) attributeDefinition.getNativeThing();
         NativeDataType nativeDataType = NativeDataType.classifyNativeDataType(attributeTypeToken);

         var dataTypeDefinition = dataTypeDefinitionGrove.getByNativeKey(nativeDataType.getId()).orElse(
            new DataTypeDefinitionGroveThing().setNativeThing(nativeDataType));

         ((AttributeDefinitionGroveThing) attributeDefinition).setDataTypeDefinition((DataTypeDefinitionGroveThing) dataTypeDefinition);

      });
   }

   /**
    * Creates the Synchronization Artifact {@link AttributeDefinitionGroveThing} things for the Synchronization Artifact's
    * {@link SpecTypeGroveThing} and {@link SpecObjectTypeGroveThing} things.
    */

   private void processCommonObjectTypeContainerMap() {

      AttributeDefinitionGrove attributeDefinitionGrove = this.getGrove(IdentifierType.ATTRIBUTE_DEFINITION);

      this.commonObjectTypeContainerMap.values().forEach(commonObjectTypeContainer -> {

         var commonObjectTypes = commonObjectTypeContainer.get();

         var artifactTypeToken = commonObjectTypeContainer.getArtifactTypeToken();

         var attributeTypeTokenList = artifactTypeToken.getValidAttributeTypes();

         attributeTypeTokenList.forEach(attributeTypeToken -> {

            //Attributes are unique (contained by) to Specifications and Spec-Objects.

            commonObjectTypes.forEach(commonObjectType -> {

               var attributeDefinition = new AttributeDefinitionGroveThing();
               attributeDefinition.setNativeThing(attributeTypeToken);
               attributeDefinition.setParent(commonObjectType);

               commonObjectType.add(attributeDefinition);

               attributeDefinitionGrove.add(attributeDefinition);
            });
         });
      });
   }

   /**
    * Gathers the OSEE artifacts needed for a SpecificationGroveThing in the Synchronization Artifact.
    *
    * @param root the root OSEE artifact for a SpecificationGroveThing in the Synchronization Artifact.
    */

   private void processRootArtifact(Root root) {
      SpecificationGrove specificationGrove = this.getGrove(IdentifierType.SPECIFICATION);
      SpecObjectGrove specObjectGrove = this.getGrove(IdentifierType.SPEC_OBJECT);

      //Get the native OSEE root object for the specification
      BranchId branchId = root.getBranchId();
      ArtifactId artifactId = root.getArtifactId();

      ResultSet<ArtifactReadable> artifactReadableSet =
         orcsApi.getQueryFactory().fromBranch(branchId).andId(artifactId).getResults();

      ArtifactReadable rootArtifactReadable = artifactReadableSet.getExactlyOne();

      //Start a specification with the OSEE root object

      var specification = (SpecificationGroveThing) this.createCommonObject(rootArtifactReadable, IdentifierType.SPECIFICATION);
      var specificationIdentifier = specification.getGroveThingKey();

      specificationGrove.add(specification);
      specObjectGrove.add(specification);

      //Add the children of the root OSEE object to the Synchronization Artifact

      rootArtifactReadable.getChildren().forEach(childArtifactReadable -> this.processArtifactReadable(1,
         childArtifactReadable, specificationIdentifier, specificationIdentifier));

   }

   /**
    * Create the Attribute Value things needed for the SpecificationGroveThing and Spec Object things
    */

   private void processSpecObjectGrove() {

      /*
       * The SpecObjectGrove contains Specifications as the root things and SpecObjectGroveThing things as children of the
       * Specifications.
       */

      SpecObjectGrove specObjectGrove = this.getGrove(IdentifierType.SPEC_OBJECT);
      AttributeValueGrove attributeValueGrove = this.getGrove(IdentifierType.ATTRIBUTE_VALUE);

      specObjectGrove.stream().forEach(groveThing -> {

         var commonObject = (CommonObjectGroveThing) groveThing;
         var artifactReadable = (ArtifactReadable) commonObject.getNativeThing();

         /*
          * The CommonObjectTypeGroveThing contains a list of the attributes that are defined for the SpecificationGroveThing or SpecObjectGroveThing
          * thing
          */

         var commonObjectType = commonObject.getCommonObjectType();

         commonObjectType.streamAttributeDefinitions().forEach(attributeDefinition -> {

            var attributeTypeToken = (AttributeTypeToken) attributeDefinition.getNativeThing();

            /*
             * This assumes that there is only one attribute value for each attribute definition
             */
            try {
               var oseeAttributeValue = artifactReadable.getSoleAttributeValue(attributeTypeToken);

               var attributeValue = (AttributeValueGroveThing) this.createGroveThing(IdentifierType.ATTRIBUTE_VALUE);

               attributeValue.setParent(commonObject);
               attributeValue.setAttributeDefinition(attributeDefinition);
               attributeValue.setNativeThing(oseeAttributeValue);

               attributeValueGrove.add(attributeValue);
            } catch (Exception e) {
               /*
                * Just skip the attribute for now if a value is not available or multiple values are available.
                */
            }

         });

      });
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
         .append( "\n" )
         .append( "\n" )
         .append( "\n" )
         .append( indent0 ).append( "DOM Dump:" ).append( "\n" )
         ;
      //@formatter:on

      this.groveMap.values().forEach(grove -> ((ToMessage) grove).toMessage(1, outMessage));

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
