import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiURL } from 'src/environments/environment';
import { OSEEWriteApiResponse } from '../../shared/types/ApiWriteResponse';
import { node } from '../types/node';

@Injectable({
  providedIn: 'root'
})
export class NodeService {

  constructor (private http: HttpClient) { }
  
  getNodes(branchId: string) {
    return this.http.get<node[]>(apiURL + '/mim/branch/' + branchId + '/nodes/');
  }

  createNode(branchId: string,body:node) {
    return this.http.post<OSEEWriteApiResponse>(apiURL + '/mim/branch/' + branchId + '/nodes/', body);
  }

  replaceNode(branchId: string,body:node) {
    return this.http.put<OSEEWriteApiResponse>(apiURL + '/mim/branch/' + branchId + '/nodes/', body);
  }

  patchNode(branchId: string,body:Partial<node>) {
    return this.http.patch<OSEEWriteApiResponse>(apiURL + '/mim/branch/' + branchId + '/nodes/', body);
  }

  getNode(branchId: string,nodeId:string) {
    return this.http.get<node>(apiURL + '/mim/branch/' + branchId + '/nodes/'+nodeId);
  }

  deleteNode(branchId: string,nodeId:string) {
    return this.http.delete<OSEEWriteApiResponse>(apiURL + '/mim/branch/' + branchId + '/nodes/'+nodeId);
  }
}
