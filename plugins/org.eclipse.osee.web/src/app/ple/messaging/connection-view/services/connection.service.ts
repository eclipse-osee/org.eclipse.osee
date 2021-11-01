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
import { of } from 'rxjs';
import { relation, transaction } from '../../../../transactions/transaction';
import { TransactionBuilderService } from '../../../../transactions/transaction-builder.service';
import { apiURL } from 'src/environments/environment';
import { OSEEWriteApiResponse } from '../../shared/types/ApiWriteResponse';
import { connection } from '../../shared/types/connection';
import { ARTIFACTTYPEID } from '../../shared/constants/ArtifactTypeId.enum';


@Injectable({
  providedIn: 'root'
})
export class ConnectionService {

  constructor (private http: HttpClient, private builder: TransactionBuilderService) { }
  
  /**
   * 
   * @param nodeId Id of node to create a connection-node relationship
   * @param type 0=primary 1=secondary
   */
  createNodeRelation(nodeId:string,type:boolean, connectionId?:string) {
    if (type) {
      let relation: relation = {
        typeName: 'Interface Connection Secondary Node',
        sideB: nodeId,
        sideA:connectionId
      }
      return of(relation);
    }
    let relation: relation = {  
      typeName: 'Interface Connection Primary Node',
      sideB: nodeId,
      sideA:connectionId
    }
    return of(relation)
  }

  createConnection(branchId:string,connection:connection,relations:relation[]) {
    return of(this.builder.createArtifact(connection, ARTIFACTTYPEID.CONNECTION, relations, undefined, branchId, "Create Connection and Relate to Node(s): " + relations[0].sideB + " , " + relations[1].sideB));
  }

  changeConnection(branchId: string, connection: Partial<connection>) {
    return of(this.builder.modifyArtifact(connection,undefined,branchId,"Change connection attributes"));
  }
  deleteRelation(branchId:string,relation:relation,transaction?:transaction) {
    return of(this.builder.deleteRelation(relation.typeName,undefined,relation.sideA as string,relation.sideB as string,undefined,transaction,branchId,'Relating Element'))
  }
  performMutation(branchId:string,body:transaction) {
    return this.http.post<OSEEWriteApiResponse>(apiURL+'/orcs/txs',body)
  }
}
