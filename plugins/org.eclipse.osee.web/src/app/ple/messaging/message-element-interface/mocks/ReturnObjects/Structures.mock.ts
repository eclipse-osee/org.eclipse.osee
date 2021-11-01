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
import { structure } from "../../types/structure";

export const structuresMock:structure[] = [{
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
    description: 'description',
    interfaceMaxSimultaneity: '0',
    interfaceMinSimultaneity: '1',
    interfaceTaskFileType: 1,
    interfaceStructureCategory: 'Category 1'
}]