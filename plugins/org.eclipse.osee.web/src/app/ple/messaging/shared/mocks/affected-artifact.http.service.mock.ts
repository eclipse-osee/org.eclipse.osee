import { of } from 'rxjs';
import { AffectedArtifactService } from '../services/http/affected-artifact.service';

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
export const affectedArtifactHttpServiceMock: Partial<AffectedArtifactService> = {
  getEnumSetsByEnums(branchId: string | number, enumId:string|number) {
    return of([]);
  },

  getPlatformTypesByEnumSet(branchId: string | number, enumSetId:string|number) {
    return of([]);
  },

  getElementsByType(branchId: string | number, typeId:string|number) {
    return of([]);
  },

  getStructuresByElement(branchId: string | number, elementId:string|number) {
    return of([]);
  },

  getSubMessagesByStructure(branchId: string | number, structureId:string|number) {
    return of([]);
  },

  getMessagesBySubMessage(branchId: string | number, subMessageId:string|number) {
    return of([]);
  }
}

export const warningArtifacts=[
  {
    id: '10',
    name:'affected1'
  },
  {
    id: '11',
    name:'affected2'
  },
]
export const affectedArtifactHttpServiceWithWarningResultsMock: Partial<AffectedArtifactService> = {
  getEnumSetsByEnums(branchId: string | number, enumId:string|number) {
    return of(warningArtifacts);
  },

  getPlatformTypesByEnumSet(branchId: string | number, enumSetId:string|number) {
    return of(warningArtifacts);
  },

  getElementsByType(branchId: string | number, typeId:string|number) {
    return of(warningArtifacts);
  },

  getStructuresByElement(branchId: string | number, elementId:string|number) {
    return of(warningArtifacts);
  },

  getSubMessagesByStructure(branchId: string | number, structureId:string|number) {
    return of(warningArtifacts);
  },

  getMessagesBySubMessage(branchId: string | number, subMessageId:string|number) {
    return of(warningArtifacts);
  }
}