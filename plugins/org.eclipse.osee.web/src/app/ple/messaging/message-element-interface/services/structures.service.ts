import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiURL } from 'src/environments/environment';
import { StructureApiResponse } from '../types/ApiResponse';
import { structure } from '../types/structure';

@Injectable({
  providedIn: 'root'
})
export class StructuresService {

  constructor (private http: HttpClient) { }
  
  getFilteredStructures(filter: string, branchId: string, messageId:string,subMessageId:string,connectionId:string): Observable<structure[]> {
    return this.http.get<structure[]>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages/" + subMessageId + "/structures/filter/" + filter);
  }
  createStructure(body:Partial<structure>,branchId: string,messageId: string, subMessageId: string,connectionId:string) {
    return this.http.post<StructureApiResponse>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages/" + subMessageId + "/structures", body);
  }
  relateStructure(branchId: string, messageId: string, subMessageId: string, structureId: string,connectionId:string) {
    return this.http.patch<StructureApiResponse>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages/" + subMessageId + "/structures/"+structureId,null);
  }
  partialUpdateStructure(body: Partial<structure>, branchId: string, messageId: string,subMessageId: string,connectionId:string):Observable<StructureApiResponse> {
    return this.http.patch<StructureApiResponse>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages/"+ subMessageId+"/structures", body);
  }
}
