import { Injectable } from '@angular/core';
import { combineLatest, from } from 'rxjs';
import { share, debounceTime, distinctUntilChanged, switchMap, repeatWhen, tap, shareReplay, take, filter, map } from 'rxjs/operators';
import { ApplicabilityListService } from '../../shared/services/http/applicability-list.service';
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
    )),
    shareReplay(1)
  )
  
  private _allMessages = combineLatest(this.BranchId,this.connectionId).pipe(
    share(),
    switchMap(x => this.messageService.getFilteredMessages("", x[0],x[1]).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
    ))
  )

  private _applics = this.ui.BranchId.pipe(
    share(),
    switchMap(id => this.applicabilityService.getApplicabilities(id).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
      shareReplay(1),
    )),
    shareReplay(1),
  )
  constructor(private messageService: MessagesService, private subMessageService: SubMessagesService, private ui: UiService, private applicabilityService: ApplicabilityListService) { }

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
  get applic() {
    return this._applics;
  }

  partialUpdateSubMessage(body: Partial<subMessage>, messageId: string) {
    return this.subMessageService.changeSubMessage(this.BranchId.getValue(), body).pipe(
      take(1),
      switchMap((transaction) => this.subMessageService.performMutation(this.BranchId.getValue(), this.connectionId.getValue(), messageId, transaction).pipe(
        tap(() => {
          this.ui.updateMessages = true;
        })
      ))
    )
  }

  partialUpdateMessage(body: Partial<message>) {
    return this.messageService.changeMessage(this.BranchId.getValue(), body).pipe(
      take(1),
      switchMap((transaction) => this.messageService.performMutation(this.BranchId.getValue(), this.connectionId.getValue(), transaction).pipe(
        tap(() => {
          this.ui.updateMessages = true;
        })
      ))
    )
  }

  relateSubMessage(messageId: string, subMessageId: string) {
    return this.messageService.getMessage(this.BranchId.getValue(), messageId, this.connectionId.getValue()).pipe(
      take(1),
      switchMap((foundMessage) => this.subMessageService.createMessageRelation(foundMessage.id,subMessageId).pipe(
        take(1),
        switchMap((relation) => this.subMessageService.addRelation(this.BranchId.getValue(), relation).pipe(
          take(1),
          switchMap((transaction) => this.subMessageService.performMutation(this.BranchId.getValue(), this.connectionId.getValue(), messageId, transaction).pipe(
            tap(() => {
              this.ui.updateMessages = true;
            })
          ))
        ))
      ))
    )
  }

  createSubMessage(body: subMessage, messageId: string) {
    return this.messageService.getMessage(this.BranchId.getValue(),messageId,this.connectionId.getValue()).pipe(
    switchMap((message)=>this.subMessageService.createMessageRelation(message.name).pipe(
      take(1),
      switchMap((relation) => this.subMessageService.createSubMessage(this.BranchId.getValue(), body, [relation]).pipe(
        take(1),
        switchMap((transaction) => this.subMessageService.performMutation(this.BranchId.getValue(), this.connectionId.getValue(), messageId, transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      )) 
    ))
    )
  }

  createMessage(body: message) {
    return combineLatest([this.BranchId, this.connectionId]).pipe(
      take(1),
      switchMap(([branch, connectionId]) => this.messageService.getConnectionName(branch, connectionId).pipe(
        take(1),
        switchMap((connectionName) => this.messageService.createConnectionRelation(connectionName).pipe(
          take(1),
          switchMap((relation) => this.messageService.createMessage(branch, body, [relation]).pipe(
            take(1),
            switchMap((transaction) => this.messageService.performMutation(branch, connectionId, transaction).pipe(
              tap(() => {
              this.ui.updateMessages = true;
              })
            ))
          ))
        ))
      ))
    )
  }
}
