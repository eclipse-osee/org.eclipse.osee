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
import { MessageApiResponse } from '../types/ApiResponse';
import { subMessage } from '../types/sub-messages';
import { ARTIFACTTYPEID } from '../../../../types/constants/ArtifactTypeId.enum';
import { TransactionService } from '../../../../transactions/transaction.service';

@Injectable({
  providedIn: 'root'
})
export class SubMessagesService {

  constructor (private http: HttpClient, private builder: TransactionBuilderService, private transactionService: TransactionService) { }

  getSubMessage(branchId: string, connectionId: string, messageId: string, subMessageId: string) {
    return this.http.get<subMessage>(apiURL + "/mim/branch/" + branchId + "/connections/" + connectionId + "/messages/" + messageId + "/submessages/" + subMessageId);
  }
  createMessageRelation(messageId:string,subMessageId?:string, afterArtifact?:string) {
    let relation: relation = {
      typeName: 'Interface Message SubMessage Content',
      sideA: messageId,
      sideB: subMessageId,
      afterArtifact: afterArtifact || 'end'
    }
    return of(relation);
  }
  changeSubMessage(branchId: string, submessage: Partial<subMessage>) {
    return of(this.builder.modifyArtifact(submessage, undefined, branchId, "Update SubMessage"));
  }

  addRelation(branchId:string,relation:relation) {
    return of(this.builder.addRelation(relation.typeName,undefined,relation.sideA as string,relation.sideB as string,undefined,undefined,branchId,'Relating SubMessage'))
  }
  deleteRelation(branchId:string,relation:relation) {
    return of(this.builder.deleteRelation(relation.typeName,undefined,relation.sideA as string,relation.sideB as string,undefined,undefined,branchId,'Relating SubMessage'))
  }
  createSubMessage(branchId: string, submessage: Partial<subMessage>, relations: relation[]) {
    return of(this.builder.createArtifact(submessage, ARTIFACTTYPEID.SUBMESSAGE, relations, undefined, branchId, "Create SubMessage"));
  }
  deleteSubMessage(branchId: string, submessageId: string) {
    return of(this.builder.deleteArtifact(submessageId,undefined,branchId,'Deleting Submessage'))
  }
  performMutation(branchId:string,connectionId:string,messageId:string,body:transaction) {
    return this.transactionService.performMutation(body)
  }
}
