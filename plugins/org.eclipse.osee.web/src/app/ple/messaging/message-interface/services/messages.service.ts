import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { relation, transaction } from '../../../../transactions/transaction';
import { TransactionBuilderService } from '../../../../transactions/transaction-builder.service';
import { apiURL } from 'src/environments/environment';
import { OSEEWriteApiResponse } from '../../shared/types/ApiWriteResponse';
import { MessageApiResponse } from '../types/ApiResponse';
import { message } from '../types/messages';
import { connection } from '../../shared/types/connection';
import { map } from 'rxjs/operators';
import { ARTIFACTTYPEID } from '../../shared/constants/ArtifactTypeId.enum';

@Injectable({
  providedIn: 'root'
})
export class MessagesService {

  constructor (private http: HttpClient, private builder: TransactionBuilderService) { }
  
  /**
   * Gets an array of messages based on a filter condition
   * @param filter parameter to filter out messages that don't meet criteria
   * @param branchId branch to look for messages on
   * @returns Observable of an array of messages matching filter condition
   */
  getFilteredMessages(filter: string, branchId: string,connectionId:string):Observable<message[]> {
    return this.http.get<message[]>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/filter/" + filter);
  }


  /**
   * Finds a specific message
   * @param branchId branch to look for contents on
   * @param messageId id of message to find
   * @returns message contents, if found
   */
  getMessage(branchId: string, messageId: string,connectionId:string):Observable<message> {
    return this.http.get<message>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/"+messageId);
  }
  private getConnection(branchId: string,connectionId:string) {
    return this.http.get<connection>(apiURL + "/mim/branch/" + branchId + "/connections/" + connectionId);
  }
  getConnectionName(branchId: string, connectionId: string) {
    return this.getConnection(branchId, connectionId).pipe(
      map(x=>x.name)
    )
  }
  createConnectionRelation(connectionId:string) {
    let relation: relation = {
      typeName: 'Interface Connection Content',
      sideA:connectionId
    }
    return of(relation);
  }
  changeMessage(branchId: string, message: Partial<message>) {
    return of(this.builder.modifyArtifact(message, undefined, branchId, "Update Message"));
  }

  createMessage(branchId: string, message: Partial<message>,relations:relation[]) {
    return of(this.builder.createArtifact(message, ARTIFACTTYPEID.MESSAGE, relations, undefined, branchId, "Create Message"));
  }
  performMutation(branchId:string,connectionId:string,body:transaction) {
    return this.http.post<OSEEWriteApiResponse>(apiURL+'/orcs/txs',body)
  }
}
