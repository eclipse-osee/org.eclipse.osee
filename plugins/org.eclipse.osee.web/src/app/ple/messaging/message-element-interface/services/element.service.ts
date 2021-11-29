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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { relation, transaction } from '../../../../transactions/transaction';
import { TransactionBuilderService } from '../../../../transactions/transaction-builder.service';
import { apiURL } from 'src/environments/environment';
import { OSEEWriteApiResponse } from '../../shared/types/ApiWriteResponse';
import { element } from '../types/element';
import { ARTIFACTTYPEID } from '../../../../types/constants/ArtifactTypeId.enum';

@Injectable({
  providedIn: 'root'
})
export class ElementService {

  constructor (private http: HttpClient, private builder: TransactionBuilderService) { }
  
  getElement(branchId: string,messageId: string, subMessageId: string, structureId: string, elementId: string,connectionId:string) {
    return this.http.get<element>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages/"+ subMessageId+"/structures/"+structureId+"/elements/"+elementId);
  }

  createStructureRelation(structureId:string,elementId?:string) {
    let relation: relation = {
      typeName: "Interface Structure Content",
      sideA: structureId,
      sideB:elementId
    }
    return of(relation);
  }
  createPlatformTypeRelation(platformTypeId: string,elementId?:string) {
    let relation: relation = {
      typeName: "Interface Element Platform Type",
      sideB: platformTypeId,
      sideA:elementId
    }
    return of(relation);
  }
  createElement(body: Partial<element>, branchId: string, relations: relation[]) {
    if (body.interfaceElementIndexEnd === 0 || body.interfaceElementIndexStart === 0) {
      delete body.interfaceElementIndexEnd;
      delete body.interfaceElementIndexStart;
      return of(this.builder.createArtifact(body, ARTIFACTTYPEID.ELEMENT, relations, undefined, branchId, "Create Element"));
    } else {
      delete body.interfaceElementAlterable;
      return of(this.builder.createArtifact(body, ARTIFACTTYPEID.ELEMENT_ARRAY, relations, undefined, branchId, "Create Element"));
    }
  }
  changeElement(body: Partial<element>, branchId: string) {
    return of(this.builder.modifyArtifact(body, undefined, branchId, "Change Element"));
  }
  addRelation(branchId:string,relation:relation,transaction?:transaction) {
    return of(this.builder.addRelation(relation.typeName,undefined,relation.sideA as string,relation.sideB as string,undefined,transaction,branchId,'Relating Element'))
  }

  deleteRelation(branchId:string,relation:relation,transaction?:transaction) {
    return of(this.builder.deleteRelation(relation.typeName,undefined,relation.sideA as string,relation.sideB as string,undefined,transaction,branchId,'Relating Element'))
  }

  deleteElement(branchId:string,elementId: string) {
    return of(this.builder.deleteArtifact(elementId,undefined,branchId,'Deleting element'))
  }
  performMutation(body: transaction, branchId: string, messageId: string, subMessageId: string, structureId: string, connectionId: string) {
    return this.http.post<OSEEWriteApiResponse>(apiURL + "/orcs/txs", body);
  }
}
