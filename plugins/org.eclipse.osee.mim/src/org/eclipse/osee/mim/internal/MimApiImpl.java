/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim.internal;

import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.mim.CrossReferenceApi;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceDifferenceReportApi;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceEnumerationApi;
import org.eclipse.osee.mim.InterfaceEnumerationSetApi;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceMessagePeriodicityApi;
import org.eclipse.osee.mim.InterfaceMessageTypeApi;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.InterfaceRateApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceStructureCategoryApi;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.InterfaceUnitApi;
import org.eclipse.osee.mim.InterfaceValidationApi;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimArtifactsApi;
import org.eclipse.osee.mim.MimPeerReviewApi;
import org.eclipse.osee.mim.MimReportsApi;
import org.eclipse.osee.mim.TransportTypeApi;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeBoolean;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeCharacter;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeDouble;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeEnumeration;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeFloat;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeGeneric;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeHex;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeInteger;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeLong;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeLongLong;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeOctet;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeShort;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeUnsignedInteger;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeUnsignedLong;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeUnsignedLongLong;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeUnsignedShort;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Audrey E Denk
 */
public class MimApiImpl implements MimApi {

   private final ConcurrentHashMap<Long, InterfaceLogicalTypeGeneric> logicalTypes = new ConcurrentHashMap<>();

   private OrcsApi orcsApi;
   private AtsApi atsApi;
   private InterfaceMessageApi interfaceMessageApi;
   private InterfaceSubMessageApi interfaceSubMessageApi;
   private InterfaceStructureApi interfaceStructureApi;
   private InterfaceElementApi interfaceElementApi;
   private InterfacePlatformTypeApi interfacePlatformApi;
   private InterfaceNodeViewApi interfaceNodeViewApi;
   private InterfaceConnectionViewApi interfaceConnectionViewApi;
   private InterfaceEnumerationApi interfaceEnumerationApi;
   private InterfaceEnumerationSetApi interfaceEnumerationSetApi;
   private InterfaceDifferenceReportApi interfaceDifferenceReportApi;
   private InterfaceUnitApi interfaceUnitApi;
   private InterfaceRateApi interfaceRateApi;
   private InterfaceMessageTypeApi interfaceMessageTypeApi;
   private TransportTypeApi transportTypeApi;
   private CrossReferenceApi crossReferenceApi;
   private InterfaceValidationApi interfaceValidationApi;
   private MimArtifactsApi mimArtifactsApi;
   private MimReportsApi mimReportsApi;
   private InterfaceStructureCategoryApi interfaceStructureCategoryApi;
   private InterfaceMessagePeriodicityApi interfaceMessagePeriodicityApi;
   private MimPeerReviewApi mimPeerReviewApi;

   public void bindOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void bindAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public void start() {
      InterfaceLogicalTypeGeneric booleanType = new InterfaceLogicalTypeBoolean();
      InterfaceLogicalTypeGeneric characterType = new InterfaceLogicalTypeCharacter();
      InterfaceLogicalTypeGeneric doubleType = new InterfaceLogicalTypeDouble();
      InterfaceLogicalTypeGeneric enumType = new InterfaceLogicalTypeEnumeration();
      InterfaceLogicalTypeGeneric floatType = new InterfaceLogicalTypeFloat();
      InterfaceLogicalTypeGeneric hexType = new InterfaceLogicalTypeHex();
      InterfaceLogicalTypeGeneric integerType = new InterfaceLogicalTypeInteger();
      InterfaceLogicalTypeGeneric longType = new InterfaceLogicalTypeLong();
      InterfaceLogicalTypeGeneric longDoubleType = new InterfaceLogicalTypeDouble();
      InterfaceLogicalTypeGeneric longLongType = new InterfaceLogicalTypeLongLong();
      InterfaceLogicalTypeGeneric octetType = new InterfaceLogicalTypeOctet();
      InterfaceLogicalTypeGeneric shortType = new InterfaceLogicalTypeShort();
      InterfaceLogicalTypeGeneric unsignedIntegerType = new InterfaceLogicalTypeUnsignedInteger();
      InterfaceLogicalTypeGeneric unsignedLongType = new InterfaceLogicalTypeUnsignedLong();
      InterfaceLogicalTypeGeneric unsignedLongLongType = new InterfaceLogicalTypeUnsignedLongLong();
      InterfaceLogicalTypeGeneric unsignedShortType = new InterfaceLogicalTypeUnsignedShort();

      logicalTypes.put(booleanType.getId(), booleanType);
      logicalTypes.put(characterType.getId(), characterType);
      logicalTypes.put(doubleType.getId(), doubleType);
      logicalTypes.put(enumType.getId(), enumType);
      logicalTypes.put(floatType.getId(), floatType);
      logicalTypes.put(hexType.getId(), hexType);
      logicalTypes.put(integerType.getId(), integerType);
      logicalTypes.put(longType.getId(), longType);
      logicalTypes.put(longDoubleType.getId(), longDoubleType);
      logicalTypes.put(longLongType.getId(), longLongType);
      logicalTypes.put(octetType.getId(), octetType);
      logicalTypes.put(shortType.getId(), shortType);
      logicalTypes.put(unsignedIntegerType.getId(), unsignedIntegerType);
      logicalTypes.put(unsignedLongType.getId(), unsignedLongType);
      logicalTypes.put(unsignedLongLongType.getId(), unsignedLongLongType);
      logicalTypes.put(unsignedShortType.getId(), unsignedShortType);

      this.transportTypeApi = new TransportTypeApiImpl(orcsApi);
      this.interfaceSubMessageApi = new InterfaceSubMessageApiImpl(orcsApi);
      this.interfaceNodeViewApi = new InterfaceNodeViewApiImpl(orcsApi);
      this.interfaceConnectionViewApi = new InterfaceConnectionViewApiImpl(orcsApi);
      this.interfaceMessageApi =
         new InterfaceMessageApiImpl(orcsApi, this.getInterfaceNodeViewApi(), this.getInterfaceConnectionViewApi());
      this.interfacePlatformApi = new InterfacePlatformTypeApiImpl(orcsApi);
      this.interfaceElementApi = new InterfaceElementApiImpl(orcsApi, this.interfacePlatformApi);
      this.interfaceStructureApi = new InterfaceStructureApiImpl(orcsApi, this.getInterfaceConnectionViewApi(),
         this.getInterfaceElementApi(), this.interfaceMessageApi);
      this.interfaceEnumerationApi = new InterfaceEnumerationApiImpl(orcsApi);
      this.interfaceEnumerationSetApi = new InterfaceEnumerationSetApiImpl(orcsApi, interfaceEnumerationApi);
      this.interfaceUnitApi = new InterfaceUnitApiImpl(orcsApi);
      this.interfaceRateApi = new InterfaceRateApiImpl(orcsApi);
      this.interfaceMessageTypeApi = new InterfaceMessageTypeApiImpl(orcsApi);
      this.interfaceDifferenceReportApi = new InterfaceDifferenceReportApiImpl(orcsApi);

      this.mimArtifactsApi = new MimArtifactsApiImpl(orcsApi);
      this.mimReportsApi = new MimReportsApiImpl(orcsApi);

      this.crossReferenceApi = new CrossReferenceApiImpl(orcsApi);
      this.interfaceValidationApi =
         new InterfaceValidationApiImpl(this.interfaceConnectionViewApi, this.interfaceStructureApi, orcsApi);
      this.interfaceStructureCategoryApi = new InterfaceStructureCategoryApiImpl(orcsApi);
      this.interfaceMessagePeriodicityApi = new InterfaceMessagePeriodicityApiImpl(orcsApi);
      this.mimPeerReviewApi = new MimPeerReviewApiImpl(orcsApi, atsApi);
   }

   @Override
   public ConcurrentHashMap<Long, InterfaceLogicalTypeGeneric> getLogicalTypes() {
      return logicalTypes;
   }

   @Override
   public OrcsApi getOrcsApi() {
      return this.orcsApi;
   }

   @Override
   public AtsApi getAtsApi() {
      return this.atsApi;
   }

   @Override
   public InterfaceMessageApi getInterfaceMessageApi() {
      return this.interfaceMessageApi;
   }

   @Override
   public InterfacePlatformTypeApi getInterfacePlatformTypeApi() {
      return this.interfacePlatformApi;
   }

   @Override
   public InterfaceSubMessageApi getInterfaceSubMessageApi() {
      return this.interfaceSubMessageApi;
   }

   @Override
   public InterfaceStructureApi getInterfaceStructureApi() {
      return this.interfaceStructureApi;
   }

   @Override
   public InterfaceElementApi getInterfaceElementApi() {
      return this.interfaceElementApi;
   }

   @Override
   public InterfaceNodeViewApi getInterfaceNodeViewApi() {
      return this.interfaceNodeViewApi;
   }

   @Override
   public InterfaceConnectionViewApi getInterfaceConnectionViewApi() {
      return this.interfaceConnectionViewApi;
   }

   @Override
   public InterfaceEnumerationApi getInterfaceEnumerationApi() {
      return this.interfaceEnumerationApi;
   }

   @Override
   public InterfaceEnumerationSetApi getInterfaceEnumerationSetApi() {
      return this.interfaceEnumerationSetApi;
   }

   @Override
   public InterfaceDifferenceReportApi getInterfaceDifferenceReportApi() {
      return this.interfaceDifferenceReportApi;
   }

   @Override
   public MimArtifactsApi getMimArtifactsApi() {
      return this.mimArtifactsApi;
   }

   @Override
   public MimReportsApi getMimReportsApi() {
      return this.mimReportsApi;
   }

   @Override
   public MimPeerReviewApi getMimPeerReviewApi() {
      return this.mimPeerReviewApi;
   }

   @Override
   public TransportTypeApi getTransportTypeApi() {
      return this.transportTypeApi;
   }

   @Override
   public CrossReferenceApi getCrossReferenceApi() {
      return this.crossReferenceApi;
   }

   @Override
   public InterfaceUnitApi getInterfaceUnitApi() {
      return this.interfaceUnitApi;
   }

   /**
    * @return the interfaceRateApi
    */
   @Override
   public InterfaceRateApi getInterfaceRateApi() {
      return interfaceRateApi;
   }

   @Override
   public InterfaceMessageTypeApi getInterfaceMessageTypeApi() {
      return interfaceMessageTypeApi;
   }

   @Override
   public InterfaceValidationApi getInterfaceValidationApi() {
      return interfaceValidationApi;
   }

   @Override
   public InterfaceStructureCategoryApi getInterfaceStructureCategoryApi() {
      return this.interfaceStructureCategoryApi;
   }

   @Override
   public InterfaceMessagePeriodicityApi getInterfaceMessagePeriodicityApi() {
      return this.interfaceMessagePeriodicityApi;
   }

}
