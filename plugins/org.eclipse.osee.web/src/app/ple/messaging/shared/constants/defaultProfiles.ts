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
export const defaultEditElementProfile = [
  'name',
  'description',
  'platformTypeName2',
  'interfaceElementIndexStart',
  'interfaceElementIndexEnd',
  'logicalType',
  'interfacePlatformTypeDefaultValue',
  'interfacePlatformTypeMinval',
  'interfacePlatformTypeMaxval',
  'beginWord',
  'endWord',
  'beginByte',
  'endByte',
  'interfaceElementAlterable',
  'interfacePlatformTypeDescription',
  'notes',
  'applicability',
  'units',
];

export const defaultViewElementProfile = [
  'name',
  'description',
  'logicalType',
  'interfacePlatformTypeDescription',
  'notes',
];

export const defaultEditStructureProfile = [
  'name',
  'description',
  'interfaceMinSimultaneity',
  'interfaceMaxSimultaneity',
  'interfaceTaskFileType',
  'interfaceStructureCategory',
  'numElements',
  'sizeInBytes',
  'bytesPerSecondMinimum',
  'bytesPerSecondMaximum',
  'applicability',
  'txRate',
];

export const defaultViewStructureProfile = [
  'name',
  'description',
  'interfaceMinSimultaneity',
  'interfaceMaxSimultaneity',
  'interfaceTaskFileType',
  'interfaceStructureCategory',
  'numElements',
  'sizeInBytes',
];
