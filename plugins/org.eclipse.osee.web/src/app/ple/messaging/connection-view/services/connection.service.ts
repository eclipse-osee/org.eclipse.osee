import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiURL } from 'src/environments/environment';
import { OSEEWriteApiResponse } from '../../shared/types/ApiWriteResponse';
import { connection } from '../types/connection';


@Injectable({
  providedIn: 'root'
})
export class ConnectionService {

  constructor (private http: HttpClient) { }
  
  getConnections(branchId: string) {
    return this.http.get<connection[]>(apiURL + '/mim/branch/' + branchId + '/connections/');
  }

  addConnection(branchId: string, body: any) {
    return this.http.post<OSEEWriteApiResponse>(apiURL + '/mim/branch/' + branchId + '/connections/', body);
  }

  replaceConnection(branchId: string, body: any) {
    return this.http.put<OSEEWriteApiResponse>(apiURL + '/mim/branch/' + branchId + '/connections/', body);
  }

  updateConnection(branchId: string, body: Partial<connection>) {
    return this.http.patch<OSEEWriteApiResponse>(apiURL + '/mim/branch/' + branchId + '/connections/', body);
  }

  getConnection(branchId: string,connectionId:string) {;
    return this.http.get<connection>(apiURL + '/mim/branch/' + branchId + '/connections/' + connectionId);
  }
  
  deleteConnection(branchId: string,connectionId:string) {
    return this.http.delete<OSEEWriteApiResponse>(apiURL + '/mim/branch/' + branchId + '/connections/' + connectionId);
  }

  createConnection(branchId: string,nodeId:string,type:string, body: connection) {
    return this.http.post<OSEEWriteApiResponse>(apiURL + '/mim/branch/' + branchId + '/nodes/' + nodeId + '/connections/' + type, body);
  }

  relateConnection(branchId: string,nodeId:string,type:string,connectionId:string, body: connection) {
    return this.http.patch<OSEEWriteApiResponse>(apiURL + '/mim/branch/' + branchId + '/nodes/' + nodeId + '/connections/' + connectionId +"/"+ type, body);
  }

  unrelateConnection(branchId: string,nodeId:string,id:string) {
    return this.http.delete<OSEEWriteApiResponse>(apiURL + '/mim/branch/' + branchId + '/nodes/' + nodeId + '/connections/' + id);
  }
}
