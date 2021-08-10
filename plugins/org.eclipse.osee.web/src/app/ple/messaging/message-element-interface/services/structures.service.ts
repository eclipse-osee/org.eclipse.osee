import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { relation, transaction } from '../../../../transactions/transaction';
import { TransactionBuilderService } from '../../../../transactions/transaction-builder.service';
import { apiURL } from 'src/environments/environment';
import { OSEEWriteApiResponse } from '../../shared/types/ApiWriteResponse';
import { structure } from '../types/structure';
import { ARTIFACTTYPEID } from '../../shared/constants/ArtifactTypeId.enum';

@Injectable({
  providedIn: 'root'
})
export class StructuresService {

  constructor (private http: HttpClient, private builder: TransactionBuilderService) { }
  
  getFilteredStructures(filter: string, branchId: string, messageId:string,subMessageId:string,connectionId:string): Observable<structure[]> {
    return this.http.get<structure[]>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages/" + subMessageId + "/structures/filter/" + filter);
  }
  getStructure(branchId: string, messageId: string, subMessageId: string, structureId: string,connectionId:string) {
    return this.http.get<structure>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages/" + subMessageId + "/structures/"+structureId);
  }

  createSubMessageRelation(subMessageId:string,structureId?:string) {
    let relation: relation = {
      typeName: "Interface SubMessage Content",
      sideA: subMessageId,
      sideB:structureId
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
    return of(this.builder.addRelation(relation.typeName,undefined,relation.sideA as string,relation.sideB as string,undefined,undefined,'10','Relating SubMessage'))
  }
  performMutation(branchId: string, messageId:string,subMessageId:string,connectionId:string,transaction:transaction): Observable<OSEEWriteApiResponse> {
    return this.http.post<OSEEWriteApiResponse>(apiURL + "/orcs/txs",transaction);
  }
}
