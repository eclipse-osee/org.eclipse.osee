import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Node,Edge } from '@swimlane/ngx-graph';
import { apiURL } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GraphService {

  constructor (private http: HttpClient) { }
  
  getNodes(id: string) {
    return this.http.get<{nodes:Node[],edges:Edge[]}>(apiURL+'/mim/branch/'+id+"/graph")
  }
}
