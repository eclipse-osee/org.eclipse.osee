import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiURL } from 'src/environments/environment';
import { StructureApiResponse } from '../types/ApiResponse';
import { element } from '../types/element';

@Injectable({
  providedIn: 'root'
})
export class ElementService {

  constructor (private http: HttpClient) { }
  
  partialUpdateElement(body: Partial<element>, branchId: string, messageId: string,subMessageId: string, structureId:string):Observable<StructureApiResponse> {
    return this.http.patch<StructureApiResponse>(apiURL + "/mim/branch/" + branchId + "/messages/" + messageId + "/submessages/"+ subMessageId+"/structures/"+structureId+"/elements", body);
  }

  createNewElement(body: Partial<element>, branchId: string, messageId: string,subMessageId: string, structureId:string):Observable<StructureApiResponse> {
    return this.http.post<StructureApiResponse>(apiURL + "/mim/branch/" + branchId + "/messages/" + messageId + "/submessages/"+ subMessageId+"/structures/"+structureId+"/elements", body);
  }
  relateElement(branchId: string,messageId: string, subMessageId: string, structureId: string, elementId: string) {
    return this.http.patch<StructureApiResponse>(apiURL + "/mim/branch/" + branchId + "/messages/" + messageId + "/submessages/"+ subMessageId+"/structures/"+structureId+"/elements/"+elementId, null);
  }

  relateElementToPlatformType(branchId: string, messageId: string,subMessageId: string, structureId:string,elementId:string, typeId:string):Observable<StructureApiResponse> {
    return this.http.patch<StructureApiResponse>(apiURL + "/mim/branch/" + branchId + "/messages/" + messageId + "/submessages/"+ subMessageId+"/structures/"+structureId+"/elements/"+elementId+"/setType/"+typeId, null);
  }
}
