import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { relation, transaction } from '../../../../transactions/transaction';
import { TransactionBuilderService } from '../../../../transactions/transaction-builder.service';
import { apiURL } from 'src/environments/environment';
import { OSEEWriteApiResponse } from '../../shared/types/ApiWriteResponse';
import { MessageApiResponse } from '../types/ApiResponse';
import { subMessage } from '../types/sub-messages';
import { ARTIFACTTYPEID } from '../../shared/constants/ArtifactTypeId.enum';

@Injectable({
  providedIn: 'root'
})
export class SubMessagesService {

  constructor (private http: HttpClient, private builder: TransactionBuilderService) { }

  createMessageRelation(messageId:string,subMessageId?:string) {
    let relation: relation = {
      typeName: 'Interface Message SubMessage Content',
      sideA: messageId,
      sideB: subMessageId
    }
    return of(relation);
  }
  changeSubMessage(branchId: string, submessage: Partial<subMessage>) {
    return of(this.builder.modifyArtifact(submessage, undefined, branchId, "Update SubMessage"));
  }

  addRelation(branchId:string,relation:relation) {
    return of(this.builder.addRelation(relation.typeName,undefined,relation.sideA as string,relation.sideB as string,undefined,undefined,'10','Relating SubMessage'))
  }
  createSubMessage(branchId: string, submessage: Partial<subMessage>, relations: relation[]) {
    return of(this.builder.createArtifact(submessage, ARTIFACTTYPEID.SUBMESSAGE, relations, undefined, branchId, "Create SubMessage"));
  }
  performMutation(branchId:string,connectionId:string,messageId:string,body:transaction) {
    return this.http.post<OSEEWriteApiResponse>(apiURL+'/orcs/txs',body)
  }
}
