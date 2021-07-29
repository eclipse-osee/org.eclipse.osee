import { Injectable } from '@angular/core';
import { combineLatest } from 'rxjs';
import { share, debounceTime, distinctUntilChanged, switchMap, repeatWhen, tap } from 'rxjs/operators';
import { message } from '../types/messages';
import { subMessage } from '../types/sub-messages';
import { MessagesService } from './messages.service';
import { SubMessagesService } from './sub-messages.service';
import { UiService } from './ui.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentMessagesService {

  private _messages = combineLatest(this.ui.filter,this.BranchId,this.connectionId).pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    switchMap(x => this.messageService.getFilteredMessages(x[0], x[1],x[2]).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
    ))
  )
  
  private _allMessages = combineLatest(this.BranchId,this.connectionId).pipe(
    share(),
    switchMap(x => this.messageService.getFilteredMessages("", x[0],x[1]).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
    ))
  )

  constructor(private messageService: MessagesService, private subMessageService: SubMessagesService, private ui: UiService) { }

  get messages() {
    return this._messages;
  }

  get allMessages() {
    return this._allMessages;
  }
  
  set filter(filter:string) {
    this.ui.filterString = filter;
  }

  set branch(id: string) {
    this.ui.BranchIdString = id;
  }

  get BranchId() {
    return this.ui.BranchId;
  }

  set connection(id: string) {
    this.ui.connectionIdString = id;
  }

  get connectionId() {
    return this.ui.connectionId;
  }

  partialUpdateSubMessage(body:Partial<subMessage>,messageId:string) {
    return this.subMessageService.partialUpdateSubMessage(body, this.BranchId.getValue(), messageId,this.connectionId.getValue()).pipe(
      tap(() => {
        this.ui.updateMessages = true;
      })
    );
  }

  partialUpdateMessage(body: Partial<message>) {
    return this.messageService.partialUpdateMessage(body, this.BranchId.getValue(),this.connectionId.getValue()).pipe(
      tap(() => {
        this.ui.updateMessages = true;
      })
    );
  }

  relateSubMessage(messageId:string,subMessageId:string) {
    return this.subMessageService.relateSubMessage(this.BranchId.getValue(), messageId, subMessageId,this.connectionId.getValue()).pipe(
      tap(() => {
        this.ui.updateMessages = true;
      })
    );
  }

  createSubMessage(body:subMessage,messageId:string) {
    return this.subMessageService.addSubMessage(body, this.BranchId.getValue(), messageId,this.connectionId.getValue()).pipe(
      tap(() => {
        this.ui.updateMessages = true;
      })
    );
  }

  createMessage(body:message) {
    return this.messageService.addMessage(body,this.BranchId.getValue(),this.connectionId.getValue()).pipe(
      tap(() => {
        this.ui.updateMessages = true;
      })
    );
  }
}
