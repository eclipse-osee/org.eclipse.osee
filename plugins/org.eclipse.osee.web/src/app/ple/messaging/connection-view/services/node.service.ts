import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { of } from 'rxjs';
import { transaction } from '../../../../transactions/transaction';
import { TransactionBuilderService } from '../../../../transactions/transaction-builder.service';
import { apiURL } from 'src/environments/environment';
import { OSEEWriteApiResponse } from '../../shared/types/ApiWriteResponse';
import { node } from '../../shared/types/node';
import { ARTIFACTTYPEID } from '../../shared/constants/ArtifactTypeId.enum';

@Injectable({
  providedIn: 'root'
})
export class NodeService {

  constructor (private http: HttpClient,private builder: TransactionBuilderService) { }
  
  getNodes(branchId: string) {
    return this.http.get<node[]>(apiURL + '/mim/branch/' + branchId + '/nodes/');
  }

  getNode(branchId: string,nodeId:string) {
    return this.http.get<node>(apiURL + '/mim/branch/' + branchId + '/nodes/'+nodeId);
  }

  changeNode(branchId: string, node: Partial<node>) {
    return of(this.builder.modifyArtifact(node, undefined, branchId, "Update Node"));
  }

  deleteArtifact(branchId: string, artId:string) {
    return of(this.builder.deleteArtifact(artId, undefined, branchId, 'Delete Node'));
  }

  createNode(branchId: string, node: Partial<node>) {
    return of(this.builder.createArtifact(node, ARTIFACTTYPEID.NODE, [], undefined, branchId, "Create Node"));
  }
  performMutation(branchId:string,body:transaction) {
    return this.http.post<OSEEWriteApiResponse>(apiURL+'/orcs/txs',body)
  }
}
