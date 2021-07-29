import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiURL } from 'src/environments/environment';
import { MessageApiResponse } from '../types/ApiResponse';
import { subMessage } from '../types/sub-messages';

@Injectable({
  providedIn: 'root'
})
export class SubMessagesService {

  constructor (private http: HttpClient) { }
  
  addSubMessage(body: subMessage, branchId: string, messageId: string,connectionId:string):Observable<MessageApiResponse> {
    return this.http.post<MessageApiResponse>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages", body);
  }

  partialUpdateSubMessage(body: Partial<subMessage>, branchId: string, messageId: string,connectionId:string):Observable<MessageApiResponse> {
    return this.http.patch<MessageApiResponse>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages", body);
  }

  relateSubMessage(branchId: string, messageId: string, subMessageId:string,connectionId:string):Observable<MessageApiResponse> {
    return this.http.patch<MessageApiResponse>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages/" + subMessageId, null);
  }

  unRelateSubMessage(branchId: string, messageId: string, subMessageId:string,connectionId:string):Observable<MessageApiResponse> {
    return this.http.delete<MessageApiResponse>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/" + messageId + "/submessages/" + subMessageId);
  }

  getSubMessage(branchId: string, messageId: string, subMessageId:string,connectionId:string):Observable<subMessage> {
    return this.http.get<subMessage>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages//" + messageId + "/submessages/" + subMessageId);
  }
}
