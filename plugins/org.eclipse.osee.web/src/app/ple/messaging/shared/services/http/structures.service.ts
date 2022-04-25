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
import { relation, transaction } from '../../../../../transactions/transaction';
import { TransactionBuilderService } from '../../../../../transactions/transaction-builder.service';
import { apiURL } from 'src/environments/environment';
import { OSEEWriteApiResponse } from '../../types/ApiWriteResponse';
import { structure } from '../../types/structure';
import { ARTIFACTTYPEID } from '../../../../../types/constants/ArtifactTypeId.enum';
import { TransactionService } from '../../../../../transactions/transaction.service';

@Injectable({
  providedIn: 'root'
})
export class StructuresService {

  constructor (private http: HttpClient, private builder: TransactionBuilderService, private transactionService: TransactionService) { }
  
  getStructures(branchId: string) {
    return this.http.get<Required<structure>[]>(apiURL+ "/mim/branch/"+branchId+"/structures/filter")
  }
  getFilteredStructures(filter: string, branchId: string, messageId:string,subMessageId:string,connectionId:string) {
    return this.http.get<Required<structure>[]>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages/" + subMessageId + "/structures/filter/" + filter);
  }
  getStructure(branchId: string, messageId: string, subMessageId: string, structureId: string,connectionId:string, filter?:string) {
    return this.http.get<Required<structure>>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages/" + subMessageId + "/structures/"+structureId+(filter!==undefined?'/'+filter:''));
  }

  createSubMessageRelation(subMessageId:string,structureId?:string, afterArtifact?:string) {
    let relation: relation = {
      typeName: "Interface SubMessage Content",
      sideA: subMessageId,
      sideB:structureId,
      afterArtifact: afterArtifact || 'end'
    }
    return of(relation);
  }
  createStructure(body: Partial<structure>, branchId: string, relations:relation[]) {
    return of(this.builder.createArtifact(body, ARTIFACTTYPEID.STRUCTURE, relations, undefined, branchId, "Create Structure"));
  }
  changeStructure(body: Partial<structure>, branchId: string) {
    return of(this.builder.modifyArtifact(body, undefined, branchId, "Change Structure"));
  }
  addRelation(branchId:string,relation:relation) {
    return of(this.builder.addRelation(relation.typeName,undefined,relation.sideA as string,relation.sideB as string,undefined,undefined,branchId,'Relating SubMessage'))
  }
  performMutation(transaction:transaction) {
    return this.transactionService.performMutation(transaction)
  }
  deleteSubmessageRelation(branchId:string,submessageId:string,structureId:string) {
    return of(this.builder.deleteRelation("Interface SubMessage Content",undefined,submessageId,structureId,undefined,undefined,branchId,"Unrelating submessage from message"))
  }
  deleteStructure(branchId:string,structureId: string) {
    return of(this.builder.deleteArtifact(structureId,undefined,branchId,'Deleting structure'))
  }
}
