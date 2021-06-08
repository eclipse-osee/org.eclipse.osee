import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiURL } from 'src/environments/environment';
import { message } from '../../message-interface/types/messages';

@Injectable({
  providedIn: 'root'
})
export class MessagesService {

  constructor (private http: HttpClient) { }
  
  /**
   * Gets an array of messages based on a filter condition
   * @param branchId branch to look for messages on
   * @returns Observable of an array of messages matching filter condition
   */
   getMessages(branchId: string):Observable<message[]> {
    return this.http.get<message[]>(apiURL + "/mim/branch/" + branchId + "/messages");
  }
}
