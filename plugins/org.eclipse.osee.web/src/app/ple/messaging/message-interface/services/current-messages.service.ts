/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { Injectable } from '@angular/core';
import { combineLatest, from, iif, of } from 'rxjs';
import { share, debounceTime, distinctUntilChanged, switchMap, repeatWhen, tap, shareReplay, take, filter, map, reduce } from 'rxjs/operators';
import { transaction } from 'src/app/transactions/transaction';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { ApplicabilityListService } from '../../shared/services/http/applicability-list.service';
import { MimPreferencesService } from '../../shared/services/http/mim-preferences.service';
import { settingsDialogData } from '../../shared/types/settingsdialog';
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

  private _preferences = combineLatest([this.ui.BranchId, this.userService.getUser()]).pipe(
    share(),
    filter(([id, user]) => id !== "" && id !== '-1' && id!=='0'),
    switchMap(([id, user]) => this.preferenceService.getUserPrefs(id, user).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
      shareReplay(1)
    )),
    shareReplay(1)
  )

  private _branchPrefs = combineLatest([this.ui.BranchId, this.userService.getUser()]).pipe(
    share(),
    switchMap(([branch,user]) => this.preferenceService.getBranchPrefs(user).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
      switchMap((branchPrefs) => from(branchPrefs).pipe(
        filter((pref) => !pref.includes(branch + ":")),
        reduce((acc, curr) => [...acc, curr], [] as string[]),
      )),
      shareReplay(1) 
    )),
    shareReplay(1),
  )

  constructor(private messageService: MessagesService, private subMessageService: SubMessagesService, private ui: UiService, private applicabilityService: ApplicabilityListService, private preferenceService: MimPreferencesService, private userService: UserDataAccountService) { }

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

  get preferences() {
    return this._preferences;
  }

  get BranchPrefs() {
    return this._branchPrefs;
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

  deleteMessage(messageId: string) {
    return this.BranchId.pipe(
      switchMap((branchId) => this.messageService.deleteMessage(branchId, messageId).pipe(
        switchMap((transaction) => this.messageService.performMutation(branchId, '', transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    )
  }

  removeMessage(messageId: string) {
    return combineLatest([this.connectionId, this.BranchId]).pipe(
      switchMap(([connectionId, branchId]) => this.messageService.createConnectionRelation(connectionId, messageId).pipe(
        switchMap((relation) => this.messageService.deleteRelation(branchId, relation).pipe(
          switchMap((transaction) => this.messageService.performMutation(branchId, connectionId, transaction).pipe(
            tap(() => {
              this.ui.updateMessages = true;
            })
          ))
        ))
      ))
    )
  }

  removeSubMessage(submessageId: string, messageId:string) {
    return this.BranchId.pipe(
      switchMap((branchId) => this.subMessageService.createMessageRelation(messageId, submessageId).pipe(
        switchMap((relation) => this.subMessageService.deleteRelation(branchId, relation).pipe(
          switchMap((transaction) => this.subMessageService.performMutation(branchId, '', '', transaction).pipe(
            tap(() => {
              this.ui.updateMessages = true;
            })
          ))
        ))
      ))
    )
  }
  deleteSubMessage(submessageId: string) {
    return this.BranchId.pipe(
      switchMap((branchId) => this.subMessageService.deleteSubMessage(branchId, submessageId).pipe(
        switchMap((transaction) => this.subMessageService.performMutation(branchId, '', '', transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    )
  }

  updatePreferences(preferences: settingsDialogData) {
    return this.createUserPreferenceBranchTransaction(preferences.editable).pipe(
      take(1),
      switchMap((transaction) => this.messageService.performMutation(this.BranchId.getValue(), '', transaction).pipe(
        take(1),
        tap(() => {
          this.ui.updateMessages = true
        })
      )
      )
    )
  }

  private createUserPreferenceBranchTransaction(editMode:boolean) {
    return combineLatest(this.preferences, this.BranchId, this.BranchPrefs).pipe(
      take(1),
      switchMap(([prefs, branch, branchPrefs]) =>
        iif(
        () => prefs.hasBranchPref,
          of<transaction>(
            {
              branch: "570",
              txComment: 'Updating MIM User Preferences',
              modifyArtifacts:
                [
                  {
                    id: prefs.id,
                    setAttributes:
                      [
                        { typeName: "MIM Branch Preferences", value: [...branchPrefs, `${branch}:${editMode}`] }
                      ],
                  }
                ]
            }
          ),
          of<transaction>(
            {
              branch: "570",
              txComment: "Updating MIM User Preferences",
              modifyArtifacts:
                [
                  {
                    id: prefs.id,
                    addAttributes:
                      [
                        { typeName: "MIM Branch Preferences", value: `${branch}:${editMode}` }
                      ]
                  }
                ]
              }
          ),
        )
      ))
  }
}
