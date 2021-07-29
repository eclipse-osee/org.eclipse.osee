import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiURL } from 'src/environments/environment';
import { MessageApiResponse } from '../types/ApiResponse';
import { message } from '../types/messages';

@Injectable({
  providedIn: 'root'
})
export class MessagesService {

  constructor (private http: HttpClient) { }
  
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
   * Adds a message to a branch
   * @param body message to add
   * @param branchId branch to look for messages on
   * @returns api response of whether or not the insertion was successful
   */
  addMessage(body: message, branchId:string,connectionId:string):Observable<MessageApiResponse> {
    return this.http.post<MessageApiResponse>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages", body);
  }

  /**
   * Updates part of a message with new contents
   * @param body message contents to add, requires id
   * @param branchId branch to look for messages on
   * @returns api response of whether or not the element was updated successfully
   */
  partialUpdateMessage(body: Partial<message>, branchId: string,connectionId:string):Observable<MessageApiResponse> {
    return this.http.patch<MessageApiResponse>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages", body);
  }

  /**
   * Removes a message from a branch
   * @param branchId branch to look for contents on
   * @param messageId id of message to delete
   * @returns api response of whether or not the message was removed
   */
  removeMessage(branchId: string, messageId: string,connectionId:string):Observable<MessageApiResponse> {
    return this.http.delete<MessageApiResponse>(apiURL + "/mim/branch/" + branchId + "/connections/"+connectionId+"/messages/"+messageId);
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

}
