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
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceElementArrayApi;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.MimApi;
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

   private InterfaceMessageApi interfaceMessageApi;

   private InterfaceSubMessageApi interfaceSubMessageApi;

   private InterfaceStructureApi interfaceStructureApi;

   private InterfaceElementApi interfaceElementApi;

   private InterfaceElementArrayApi interfaceElementArrayApi;

   private InterfacePlatformTypeApi interfacePlatformApi;

   private InterfaceNodeViewApi interfaceNodeViewApi;

   private InterfaceConnectionViewApi interfaceConnectionViewApi;

   public void bindOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
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

      this.interfaceMessageApi = new InterfaceMessageApiImpl(orcsApi);
      this.interfaceSubMessageApi = new InterfaceSubMessageApiImpl(orcsApi);
      this.interfacePlatformApi = new InterfacePlatformTypeApiImpl(orcsApi);
      this.interfaceStructureApi = new InterfaceStructureApiImpl(orcsApi);
      this.interfaceElementApi = new InterfaceElementApiImpl(orcsApi);
      this.interfaceElementArrayApi = new InterfaceElementArrayApiImpl(orcsApi);
      this.interfaceNodeViewApi = new InterfaceNodeViewApiImpl(orcsApi);
      this.interfaceConnectionViewApi = new InterfaceConnectionViewApiImpl(orcsApi);
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
   public InterfaceElementArrayApi getInterfaceElementArrayApi() {
      return this.interfaceElementArrayApi;
   }

   @Override
   public InterfaceNodeViewApi getInterfaceNodeViewApi() {
      return this.interfaceNodeViewApi;
   }

   @Override
   public InterfaceConnectionViewApi getInterfaceConnectionViewApi() {
      return this.interfaceConnectionViewApi;
   }

}
