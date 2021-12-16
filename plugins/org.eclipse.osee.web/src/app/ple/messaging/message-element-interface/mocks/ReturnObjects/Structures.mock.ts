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
import { structure, structureWithChanges } from "../../types/structure";

export const structuresMock:Required<structure>[] = [{
    id: '1',
    name: 'name',
    elements: [{
      id: '1',
      name: 'name2',
      description: 'description2',
      notes: 'notes',
      interfaceElementIndexEnd: 1,
      interfaceElementIndexStart: 0,
      interfaceElementAlterable: true,
      platformTypeName2: 'boolean',
      platformTypeId:9
  }],
    numElements: 1,
    sizeInBytes:0,
    bytesPerSecondMaximum: 0,
    bytesPerSecondMinimum: 0,
    applicability:{id:'1',name:'Base'},
    description: 'description',
    interfaceMaxSimultaneity: '0',
    interfaceMinSimultaneity: '1',
    interfaceTaskFileType: 1,
    interfaceStructureCategory: 'Category 1'
}]

export const structuresMockWithChanges: structureWithChanges = {
  id: '1', 
  name: 'name',
  elements: [
    {
      id: '1',
      name: 'name2',
      description: 'description2',
      notes: 'notes',
      interfaceElementIndexEnd: 1,
      interfaceElementIndexStart: 0,
      interfaceElementAlterable: true,
      platformTypeName2: 'boolean',
      platformTypeId:9
    }
  ],
  numElements: 1,
  sizeInBytes:0,
  bytesPerSecondMaximum: 0,
  bytesPerSecondMinimum: 0,
  applicability:{id:'1',name:'Base'},
  description: 'description',
  interfaceMaxSimultaneity: '0',
  interfaceMinSimultaneity: '1',
  interfaceTaskFileType: 1,
  interfaceStructureCategory: 'Category 1',
  added: false,
  deleted: false,
  hasElementChanges:false,
  changes: {
    name: {
      previousValue: 'a',
      currentValue: 'b',
      transactionToken:{id:'1234',branchId:'8'}
      }  
  }
}