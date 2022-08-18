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
import { BehaviorSubject, combineLatest, from, iif, of, Subject } from 'rxjs';
import { share, debounceTime, distinctUntilChanged, switchMap, repeatWhen, tap, shareReplay, take, filter, map, reduce, takeUntil, mergeMap, scan } from 'rxjs/operators';
import { transaction, transactionToken } from 'src/app/transactions/transaction';
import { ApplicabilityListUIService } from '../../shared/services/ui/applicability-list-ui.service';
import { PreferencesUIService } from '../../shared/services/ui/preferences-ui.service';
import { applic } from '../../../../types/applicability/applic';
import { settingsDialogData } from '../../shared/types/settingsdialog';
import { message, messageWithChanges } from '../types/messages';
import { subMessage, subMessageWithChanges } from '../types/sub-messages';
import { MessagesService } from './messages.service';
import { SubMessagesService } from './sub-messages.service';
import { MessageUiService } from './ui.service';
import { changeInstance, changeTypeEnum, itemTypeIdRelation } from '../../../../types/change-report/change-report.d';
import { ARTIFACTTYPEID } from '../../../../types/constants/ArtifactTypeId.enum';
import { ATTRIBUTETYPEID } from '../../../../types/constants/AttributeTypeId.enum';
import { BranchInfoService } from 'src/app/ple-services/http/branch-info.service';
import { RelationTypeId } from '../../../../types/constants/RelationTypeId.enum';
import { SideNavService } from 'src/app/shared-services/ui/side-nav.service';
import { ConnectionNode } from '../types/connection-nodes';
import { CurrentBranchInfoService } from '../../../../ple-services/httpui/current-branch-info.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentMessagesService {

  private _messagesList = combineLatest([this.ui.filter, this.BranchId, this.connectionId]).pipe(
    filter(([filter,branchId,connection])=>connection!==''),
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    switchMap(x => this.messageService.getFilteredMessages(x[0], x[1],x[2]).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
    )),
    shareReplay({ bufferSize: 1, refCount: true }),
  )
  

  private _messages = combineLatest([this.ui.isInDiff, this._messagesList]).pipe(
    switchMap(([diffState, messageList]) => iif(() => diffState,
      this.differences.pipe(
        filter((val)=>val!==undefined),
        switchMap((differences) => of(this.parseIntoMessagesAndSubmessages(differences as changeInstance[], messageList)).pipe(
          switchMap((messagesWithDifferences) => from(messagesWithDifferences).pipe(
            mergeMap((message) =>
              iif(
                () => (message as messageWithChanges).deleted,
                this.getMessageFromParent(message.id).pipe(
                  switchMap((parentMessage) => this.mergeMessages(message as messageWithChanges, parentMessage))
                ),
                of(message)
              ).pipe(
                mergeMap(message => from(message.subMessages).pipe(
                  mergeMap((submessage) =>
                    iif(
                      () => (submessage as subMessageWithChanges).deleted,
                      this.getSubMessageFromParent(message.id, submessage.id || '').pipe(
                        switchMap((parentSubMessage)=>this.mergeSubMessage(submessage as subMessageWithChanges,parentSubMessage))
                      ),//deleted submessage
                      of(submessage)//not deleted submessage
                  )
                  )
                )),
                //find deleted sub message details of all messages and merge their contents with parent branch details
                //merge back into array and set message.subMessages to it
                reduce((acc, curr) => [...acc, curr], [] as (subMessage | subMessageWithChanges)[]),
                switchMap((submessagearray)=>this.mergeSubmessagesIntoMessage(message,submessagearray)) 
              ),
            ),
          )),
        )),
        scan((acc, curr) => [...acc, curr], [] as (message | messageWithChanges)[]),
        map((array) => array.sort((a, b) => Number(a.id) - Number(b.id))),
        //find deleted messages and merge their contents with parent branch details
      ),
      of(messageList)
    )),
  )
  private _allMessages = combineLatest([this.BranchId,this.connectionId]).pipe(
    share(),
    switchMap(x => this.messageService.getFilteredMessages("", x[0],x[1]).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
    ))
  )

  private _connectionNodes = combineLatest([this.BranchId, this.connectionId]).pipe(
    switchMap(([branchId, connectionId]) => this.messageService.getConnectionNodes(branchId, connectionId).pipe(
      shareReplay({ bufferSize: 1, refCount: true })
    ))
  )

  private _done = new Subject<boolean>();
  private _differences = new BehaviorSubject<changeInstance[] | undefined>(undefined);
  
  private _expandedRows = new BehaviorSubject<(message | messageWithChanges)[]>([]);
  private _expandedRowsDecreasing = new BehaviorSubject<boolean>(false);
  constructor(private messageService: MessagesService, private subMessageService: SubMessagesService, private ui: MessageUiService, private applicabilityService: ApplicabilityListUIService, private preferenceService: PreferencesUIService,private branchInfoService: CurrentBranchInfoService, private sideNavService: SideNavService) { }

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

  set branchId(value: string) {
    this.ui.BranchIdString = value;
  }

  set connection(id: string) {
    this.ui.connectionIdString = id;
  }

  get connectionId() {
    return this.ui.connectionId;
  }

  set messageId(value:string) {
    this.ui.messageId = value;
  }

  set subMessageId(value: string) {
    this.ui.subMessageId = value;
  }

  set submessageToStructureBreadCrumbs(value: string) {
    this.ui.subMessageToStructureBreadCrumbs = value;
  }

  set singleStructureId(value: string) {
    this.ui.singleStructureId = value;
  }

  get connectionNodes() {
    return this._connectionNodes;
  }

  get applic() {
    return this.applicabilityService.applic;
  }

  get preferences() {
    return this.preferenceService.preferences;
  }

  get BranchPrefs() {
    return this.preferenceService.BranchPrefs;
  }

  get sideNavContent() {
    return this.sideNavService.sideNavContent;
  }

  set sideNav(value: { opened: boolean, field: string, currentValue: string | number | applic|boolean, previousValue?: string | number | applic| boolean,transaction?:transactionToken, user?: string, date?: string }) {
    this.sideNavService.sideNav = value;
  }

  set DiffMode(value: boolean) {
    this.ui.DiffMode = value;
  }
  get isInDiff() {
    return this.ui.isInDiff;
  }

  get differences() {
    return this._differences;
  }
  set difference(value: changeInstance[]) {
    this._differences.next(value);
  }

  set branchType(value: string) {
    this.ui.typeValue = value;
  }

  get expandedRows() {
    return this._expandedRows.asObservable();
  }
  
  get expandedRowsDecreasing() {
    return this._expandedRowsDecreasing
  }

  set addExpandedRow(value: message|messageWithChanges) {
    if (this._expandedRows.getValue().map(s=>s.id).indexOf(value.id) === -1) {
      const temp = this._expandedRows.getValue();
      temp.push(value);
      this._expandedRows.next(temp)
    }
    this._expandedRowsDecreasing.next(false)
  }

  set removeExpandedRow(value: message|messageWithChanges) {
    if (this._expandedRows.getValue().map(s=>s.id).indexOf(value.id) > -1) {
      const temp = this._expandedRows.getValue();
      temp.splice(this._expandedRows.getValue().indexOf(value), 1);
      this._expandedRows.next(temp);
    }
    this._expandedRowsDecreasing.next(true)
  }

  private mergeMessages(message: messageWithChanges, parentMessage: message) {
    message.name = parentMessage.name;
    message.description = parentMessage.description;
    message.interfaceMessageNumber = parentMessage.interfaceMessageNumber;
    message.interfaceMessagePeriodicity = parentMessage.interfaceMessagePeriodicity;
    message.interfaceMessageRate = parentMessage.interfaceMessageRate;
    message.interfaceMessageType = parentMessage.interfaceMessageType;
    message.interfaceMessageWriteAccess = parentMessage.interfaceMessageWriteAccess;
    return of(message)
  }

  private mergeSubMessage(submessage: subMessageWithChanges, parentSubMessage: subMessage) {
    submessage.name = parentSubMessage.name;
    submessage.description = parentSubMessage.description;
    submessage.interfaceSubMessageNumber = parentSubMessage.description;
    submessage.applicability = parentSubMessage.applicability;
    return of(submessage);
  }
  private mergeSubmessagesIntoMessage(message: message | messageWithChanges, submessages: (subMessage | subMessageWithChanges)[]) {
    message.subMessages = submessages;
    return of(message);
  }
  getMessageFromParent(messageId: string) {
    return combineLatest([this.branchInfoService.currentBranchDetail, this.connectionId]).pipe(
      take(1),
      switchMap(([details, connectionId]) => this.messageService.getMessage(details.parentBranch.id, messageId, connectionId))
    );
  }

  getSubMessageFromParent(messageId: string, subMessageId: string) {
    return combineLatest([this.branchInfoService.currentBranchDetail, this.connectionId]).pipe(
      take(1),
      switchMap(([details, connectionId]) => this.subMessageService.getSubMessage(details.parentBranch.id, connectionId, messageId, subMessageId))
    );
  }
  partialUpdateSubMessage(body: Partial<subMessage>, messageId: string) {
    return combineLatest([this.BranchId, this.connectionId]).pipe(
      take(1),
      switchMap(([branch,connection])=>this.subMessageService.changeSubMessage(branch, body).pipe(
        take(1),
        switchMap((transaction) => this.subMessageService.performMutation(branch, connection, messageId, transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    );
  }

  partialUpdateMessage(body: Partial<message>) {
    return this.BranchId.pipe(
      take(1),
      switchMap(branchId => this.messageService.changeMessage(branchId, body).pipe(
        take(1),
        switchMap((transaction) => this.messageService.performMutation(transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    );
  }

  relateSubMessage(messageId: string, subMessageId: string, afterSubMessage?: string) {
    return combineLatest([this.BranchId, this.connectionId]).pipe(
      take(1),
      switchMap(([branch, connection]) => this.messageService.getMessage(branch, messageId, connection).pipe(
        take(1),
        switchMap((foundMessage) => this.subMessageService.createMessageRelation(foundMessage.id, subMessageId, afterSubMessage).pipe(
          take(1),
          switchMap((relation) => this.subMessageService.addRelation(branch, relation).pipe(
            take(1),
            switchMap((transaction) => this.subMessageService.performMutation(branch, connection, messageId, transaction).pipe(
              tap(() => {
                this.ui.updateMessages = true;
              })
            ))
          ))
        ))
      ))
    );
  }

  createSubMessage(body: subMessage, messageId: string, afterSubMessage?: string) {
    return combineLatest([this.BranchId, this.connectionId]).pipe(
      take(1),
      switchMap(([branch, connection]) => this.subMessageService.createMessageRelation(messageId, undefined, afterSubMessage).pipe(
        take(1),
        switchMap((relation) => this.subMessageService.createSubMessage(branch, body, [relation]).pipe(
          take(1),
          switchMap((transaction) => this.subMessageService.performMutation(branch, connection, messageId, transaction).pipe(
            tap(() => {
              this.ui.updateMessages = true;
            })
          ))
        ))
      ))
    );
  }

  createMessage(initiatingNode: ConnectionNode, body: message) {
    return combineLatest([this.BranchId, this.connectionId]).pipe(
      take(1),
      switchMap(([branch, connectionId]) => this.messageService.createConnectionRelation(connectionId).pipe(
        take(1),
        switchMap((connectionRelation) => this.messageService.createNodeRelation(body.id, initiatingNode.id).pipe(
          take(1),
          switchMap((nodeRelation) => this.messageService.createMessage(branch, body, [connectionRelation, nodeRelation]).pipe(
            take(1),
            switchMap((transaction) => this.messageService.performMutation(transaction).pipe(
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
      take(1),
      switchMap((branchId) => this.messageService.deleteMessage(branchId, messageId).pipe(
        switchMap((transaction) => this.messageService.performMutation(transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    )
  }

  removeMessage(messageId: string) {
    return combineLatest([this.connectionId, this.BranchId]).pipe(
      take(1),
      switchMap(([connectionId, branchId]) => this.messageService.createConnectionRelation(connectionId, messageId).pipe(
        switchMap((relation) => this.messageService.deleteRelation(branchId, relation).pipe(
          switchMap((transaction) => this.messageService.performMutation(transaction).pipe(
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
      take(1),
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
      take(1),
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
      switchMap((transaction) => this.messageService.performMutation(transaction).pipe(
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

  set toggleDone(value: unknown) {
    this._done.next(true);
    this._done.complete();
  }

  get done() {
    return this._done;
  }

  parseIntoMessagesAndSubmessages(changes: changeInstance[], _oldMessageList: (message | messageWithChanges)[]) {
    let messageList = JSON.parse(JSON.stringify(_oldMessageList)) as (message | messageWithChanges)[];
    let newMessages: changeInstance[] = [];
    let newMessagesId: string[] = [];
    let newSubmessages: changeInstance[] = [];
    let newSubmessagesId: string[] = [];
    changes.forEach((change) => {
      //this loop is solely just for building a list of deleted nodes/connections
      if (change.itemTypeId === ARTIFACTTYPEID.SUBMESSAGE && !newMessagesId.includes(change.artId) && !newSubmessagesId.includes(change.artId)) {
        //deleted submessage
        newSubmessagesId.push(change.artId);
      } else if (change.itemTypeId === ARTIFACTTYPEID.MESSAGE && !newMessagesId.includes(change.artId) && !newSubmessagesId.includes(change.artId)) {
        //deleted message
        newMessagesId.push(change.artId);
      } else if (typeof change.itemTypeId === "object" && "id" in change.itemTypeId && change.itemTypeId.id === RelationTypeId.INTERFACECONNECTIONCONTENT) {
        if (!newMessagesId.includes(change.artId)) {
          newMessagesId.push(change.artId)
        } else if (!newMessagesId.includes(change.artIdB)) {
          newMessagesId.push(change.artIdB)
        }
      } else if (typeof change.itemTypeId === "object" && "id" in change.itemTypeId && change.itemTypeId.id === RelationTypeId.INTERFACEMESSAGECONTENT) {
        if (!newSubmessagesId.includes(change.artId)) {
          newSubmessagesId.push(change.artId)
        }
      }
    })
    changes.sort((a, b) => ["111", "333", "222", "444"].indexOf(a.changeType.id) - ["111", "333", "222", "444"].indexOf(b.changeType.id)).forEach((change) => {
      if (messageList.find((val) => val.id === change.artId)) {
        //logic for message update
        const messageIndex = messageList.indexOf(messageList.find((val) => val.id === change.artId) as message);
        messageList[messageIndex] = this.messageChange(change, messageList[messageIndex]);
        const messageChanges = (messageList[messageIndex] as messageWithChanges).changes;
        if (messageChanges.applicability!==undefined && messageChanges.name!==undefined && messageChanges.description!==undefined && messageChanges.interfaceMessageNumber !==undefined && messageChanges.interfaceMessagePeriodicity !== undefined && messageChanges.interfaceMessageRate !== undefined && messageChanges.interfaceMessageType!==undefined && messageChanges.interfaceMessageWriteAccess !== undefined && (messageList[messageIndex] as messageWithChanges).deleted !== true) {
          (messageList[messageIndex] as messageWithChanges).added = true;
          (messageList[messageIndex] as messageWithChanges).changes.initiatingNode = {previousValue: "", currentValue: messageList[messageIndex].initiatingNode, transactionToken: (messageList[messageIndex] as messageWithChanges).changes.description!.transactionToken}
        } else {
          (messageList[messageIndex] as messageWithChanges).added = false;
        }
        if (!(messageList[messageIndex] as messageWithChanges).hasSubMessageChanges) {
          (messageList[messageIndex] as messageWithChanges).hasSubMessageChanges = false;
        }
      }
      else if (messageList.find((val)=>val.subMessages.find((val2)=>val2.id===change.artId)!==undefined)) {
        //logic for submessage update
        let filteredMessages = messageList.filter((val) => val.subMessages.find((val2) => val2.id === change.artId));
        filteredMessages.forEach((value, index) => {
          let subMessageIndex = filteredMessages[index].subMessages.indexOf(filteredMessages[index].subMessages.find((val2) => val2.id === change.artId) as Required<subMessage>);
          filteredMessages[index].subMessages[subMessageIndex] = this.subMessageChange(change, filteredMessages[index].subMessages[subMessageIndex]);
          (filteredMessages[index] as messageWithChanges).hasSubMessageChanges = true;
          let messageChanges = (filteredMessages[index].subMessages[subMessageIndex] as subMessageWithChanges).changes;
          if (messageChanges.name !== undefined && messageChanges.description !== undefined && messageChanges.interfaceSubMessageNumber !== undefined && messageChanges.applicability !== undefined && (filteredMessages[index].subMessages[subMessageIndex] as subMessageWithChanges).deleted!==true) {
            (filteredMessages[index].subMessages[subMessageIndex] as subMessageWithChanges).added = true;  
          } else {     
            (filteredMessages[index].subMessages[subMessageIndex] as subMessageWithChanges).added = false;   
          }
          ///update main list
          let messageIndex = messageList.indexOf(messageList.find((val) => val.id === filteredMessages[index].id) as message|messageWithChanges);
          messageList[messageIndex] = filteredMessages[index];
        })
      }
      else if ((newMessagesId.includes(change.artId) || newMessagesId.includes(change.artIdB)) && change.deleted) {
        newMessages.push(change);
      }
      else if ((newSubmessagesId.includes(change.artId)||newSubmessagesId.includes(change.artIdB)) && change.deleted) {
        newSubmessages.push(change)
      }
    })
    newMessages.sort((a, b) => Number(a.artId) - Number(b.artId));
    newSubmessages.sort((a, b) => Number(a.artId) - Number(b.artId));
    let messages = this.splitByArtId(newMessages);
    messages.forEach((value) => {
      //create deleted messages
      let tempMessage = this.messageDeletionChanges(value);
      if (!isNaN(+tempMessage.id) && tempMessage.id!=='') {
        messageList.push(tempMessage); 
      }
    })
    let submessages = this.splitByArtId(newSubmessages);
    submessages.forEach((value) => {
      //create deleted submessages
    })
    messageList.forEach((m) => {
      m.subMessages=m.subMessages.sort((a,b)=>Number(a.id)-Number(b.id))
    })
    return messageList.sort((a,b)=>Number(a.id)-Number(b.id))
  }

  private splitByArtId(changes: changeInstance[]): changeInstance[][]{
    let returnValue: changeInstance[][] = [];
    let prev: Partial<changeInstance> | undefined = undefined;
    let tempArray: changeInstance[] = [];
    changes.forEach((value, index) => {
      if (prev !== undefined) {
        if (prev.artId === value.artId) {
          //condition where equal, add to array
          tempArray.push(value);
        } else {
          prev = Object.assign(prev, value);
          returnValue.push(tempArray);
          tempArray = [];
          tempArray.push(value);
          //condition where not equal, set prev to value, push old array onto returnValue, create new array
        }
      } else {
        tempArray = [];
        tempArray.push(value);
        prev = {}
        prev = Object.assign(prev, value);
        //create new array, push prev onto array, set prev 
      }
    })
    if (tempArray !== []) {
      returnValue.push(tempArray)
    }
    return returnValue;
  }

  private messageDeletionChanges(changes: changeInstance[]) {
    let tempMessage: messageWithChanges = {
      added: false,
      deleted: true,
      hasSubMessageChanges: false,
      changes: {},
      id: '',
      name: '',
      description: '',
      applicability: {
        id: '1',
        name: 'Base',
      },
      subMessages: [],
      interfaceMessageRate: '',
      interfaceMessagePeriodicity: '',
      interfaceMessageWriteAccess: false,
      interfaceMessageType: '',
      interfaceMessageNumber: '',
      initiatingNode: {
        id: '',
        name: ''
      }
    }
    changes.forEach((value) => {
      tempMessage = this.parseMessageDeletionChange(value, tempMessage);
    })
    return tempMessage;
  }
  parseMessageDeletionChange(change: changeInstance, message: messageWithChanges): messageWithChanges {
    message.id = change.artId;
    if (message.changes === undefined) {
      message.changes = {};
    }
    if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
      let changes ={
        previousValue: change.baselineVersion.value,
        currentValue: change.destinationVersion.value,
        transactionToken:change.currentVersion.transactionToken
      }
      if (change.itemTypeId === ATTRIBUTETYPEID.NAME) {
        message.changes.name = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.DESCRIPTION) {
        message.changes.description = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMESSAGENUMBER) {
        message.changes.interfaceMessageNumber = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMESSAGEPERIODICITY) {
        message.changes.interfaceMessagePeriodicity = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMESSAGERATE) {
        message.changes.interfaceMessageRate = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMESSAGETYPE) {
        message.changes.interfaceMessageType = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMESSAGEWRITEACCESS) {
        message.changes.interfaceMessageWriteAccess = changes;
      }
    } else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
      message.changes.applicability = {
        previousValue: change.baselineVersion.applicabilityToken,
        currentValue: change.currentVersion.applicabilityToken,
        transactionToken:change.currentVersion.transactionToken
      }
    } else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
      message.id = change.artIdB
      message.applicability = change.currentVersion.applicabilityToken as applic;
    }
    return message;
  }

  private messageChange(change: changeInstance, message: message|messageWithChanges) {
    return this.parseMessageChange(change, this.initializeMessage(message));
  }
  private parseMessageChange(change: changeInstance, message: messageWithChanges) {
    if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
      let changes ={
        previousValue: change.baselineVersion.value,
        currentValue: change.currentVersion.value,
        transactionToken:change.currentVersion.transactionToken
      }
      if (change.itemTypeId === ATTRIBUTETYPEID.NAME) {
        message.changes.name = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.DESCRIPTION) {
        message.changes.description = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMESSAGENUMBER) {
        message.changes.interfaceMessageNumber = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMESSAGEPERIODICITY) {
        message.changes.interfaceMessagePeriodicity = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMESSAGERATE) {
        message.changes.interfaceMessageRate = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMESSAGETYPE) {
        message.changes.interfaceMessageType = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMESSAGEWRITEACCESS) {
        message.changes.interfaceMessageWriteAccess = changes;
      }
    } else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
      if (change.currentVersion.transactionToken .id !=='-1') {
        message.changes.applicability = {
          previousValue: change.baselineVersion.applicabilityToken,
          currentValue: change.currentVersion.applicabilityToken,
          transactionToken:change.currentVersion.transactionToken
        }
      }
    } else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
      //do nothing currently
      if ((change.itemTypeId as itemTypeIdRelation).id = RelationTypeId.INTERFACEMESSAGECONTENT) {
        message.hasSubMessageChanges = true;
        let submessageIndex = message.subMessages.findIndex((val) => val.id === change.artIdB);
        if (submessageIndex !== -1) {
          message.subMessages[submessageIndex] = this.subMessageChange(change, message.subMessages[submessageIndex]); 
        } else {
          let submessage:subMessageWithChanges={
            added: false,
            deleted: true,
            changes: {},
            applicability: {
              id: '1',
              name: 'Base',
            },
            id:change.artIdB,
            name: '',
            description: '',
            interfaceSubMessageNumber: ''
          }
          message.subMessages.push(submessage)
        }
      }
    }
    return message
  }
  private isMessageWithChanges(message: message | messageWithChanges): message is messageWithChanges { return (message as messageWithChanges).changes !== undefined;}
  private initializeMessage(message: message|messageWithChanges) {
    let tempMessage: messageWithChanges;
    if (!this.isMessageWithChanges(message)) {
      tempMessage = message as messageWithChanges;
      tempMessage.changes = {}
    } else {
      tempMessage = message;
    }
    return tempMessage
  }

  private subMessageChange(change: changeInstance, submessage: subMessage | subMessageWithChanges) {
    return this.parseSubMessageChange(change, this.initializeSubMessage(submessage))
  }
  parseSubMessageChange(change: changeInstance, submessage: subMessageWithChanges) {
    if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
      let changes ={
        previousValue: change.baselineVersion.value,
        currentValue: change.currentVersion.value,
        transactionToken:change.currentVersion.transactionToken
      }
      if (change.itemTypeId === ATTRIBUTETYPEID.NAME) {
        submessage.changes.name = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.DESCRIPTION) {
        submessage.changes.description = changes;
      } else if (change.itemTypeId === ATTRIBUTETYPEID.INTERFACESUBMESSAGENUMBER) {
        submessage.changes.interfaceSubMessageNumber = changes;
      }
    } else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
      if (change.currentVersion.transactionToken.id !== '-1') {
        submessage.changes.applicability = {
          previousValue: change.baselineVersion.applicabilityToken,
          currentValue: change.currentVersion.applicabilityToken,
          transactionToken:change.currentVersion.transactionToken
        }
      }
    } else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
      //do nothing currently
      submessage.added = true;
    }
    return submessage;
  }
  initializeSubMessage(submessage: subMessage | subMessageWithChanges) {
    let tempMessage: subMessageWithChanges;
    if (!this.isSubMessageWithChanges(submessage)) {
      tempMessage = submessage as subMessageWithChanges;
      tempMessage.changes = {}
    } else {
      tempMessage = submessage;
    }
    return tempMessage
  }
  isSubMessageWithChanges(submessage: subMessage | subMessageWithChanges): submessage is subMessageWithChanges { return (submessage as subMessageWithChanges)?.changes !== undefined; }
  
  get initialRoute() {
    return combineLatest([this.ui.type, this.BranchId, this.connectionId]).pipe(
      switchMap(([type,id,connection])=>of('/ple/messaging/'+type+'/'+id+'/'+connection+'/messages/'))
    )
  }

  get endOfRoute() {
    return this.isInDiff.pipe(
      switchMap((val)=>iif(()=>val,of("/diff"),of("")))
    )
  }

  get connectionsRoute() {
    return combineLatest([this.ui.type, this.BranchId]).pipe(
      switchMap(([type, BranchId])=>of("/ple/messaging/connections/" + type + "/" + BranchId))
    )
  }
}
