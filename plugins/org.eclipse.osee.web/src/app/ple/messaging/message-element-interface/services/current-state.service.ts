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
import { combineLatest, from, iif, Observable, of } from 'rxjs';
import { share, debounceTime, distinctUntilChanged, switchMap, repeatWhen, mergeMap, scan, distinct, tap, shareReplay, first, filter, take, map, reduce, delay } from 'rxjs/operators';
import { transaction } from 'src/app/transactions/transaction';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { ApplicabilityListService } from '../../shared/services/http/applicability-list.service';
import { MimPreferencesService } from '../../shared/services/http/mim-preferences.service';
import { MimPreferences } from '../../shared/types/mim.preferences';
import { settingsDialogData } from '../../shared/types/settingsdialog';
import { element } from '../types/element';
import { structure } from '../types/structure';
import { ElementService } from './element.service';
import { MessagesService } from './messages.service';
import { PlatformTypeService } from './platform-type.service';
import { StructuresService } from './structures.service';
import { UiService } from './ui.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentStateService {

  private _structures = combineLatest(this.ui.filter, this.ui.BranchId, this.ui.messageId, this.ui.subMessageId,this.ui.connectionId).pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    switchMap(x => this.structure.getFilteredStructures(...x).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
      shareReplay(1),
    )),
    shareReplay(1),
  )

  private _types = this.ui.BranchId.pipe(
    share(),
    switchMap(x => this.typeService.getTypes(x).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
      shareReplay(1),
    )),
    shareReplay(1),
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

  constructor (private ui: UiService, private structure: StructuresService, private messages:MessagesService, private elements:ElementService, private typeService: PlatformTypeService, private applicabilityService: ApplicabilityListService,private preferenceService: MimPreferencesService, private userService: UserDataAccountService) { }
  
  get structures() {
    return this._structures;
  }

  set filter(value: string) {
    this.ui.filterString = value;
  }

  set branchId(value: string) {
    this.ui.BranchIdString = value;
  }

  get BranchId() {
    return this.ui.BranchId;
  }

  get branchType() {
    return this.ui.branchType;
  }

  set BranchType(value:string) {
    this.ui.BranchType = value;
  }
  get BranchType() {
    return this.ui.branchType.getValue();
  }

  set messageId(value: string) {
    this.ui.messageIdString = value;
  }

  get MessageId() {
    return this.ui.messageId;
  }

  get SubMessageId() {
    return this.ui.subMessageId
  }

  set subMessageId(value: string) {
    this.ui.subMessageIdString = value;
  }

  set connection(id: string) {
    this.ui.connectionIdString = id;
  }

  get connectionId() {
    return this.ui.connectionId;
  }

  get preferences() {
    return this._preferences;
  }

  get BranchPrefs() {
    return this._branchPrefs;
  }

  private get structureObservable(){
    return this.messages.getMessages(this.BranchId.getValue(),this.connectionId.getValue()).pipe(
      mergeMap(messages => from(messages).pipe(
        mergeMap(message => of(message?.subMessages).pipe(
          mergeMap(submessage => from(submessage).pipe(
            distinct((x) => { return x.id }),
            mergeMap((submessage) => this.structure.getFilteredStructures("", this.BranchId.getValue(), message?.id, submessage?.id,this.connectionId.getValue()).pipe(
              mergeMap(structures => from(structures).pipe(
                distinct((structure)=>{return structure.id})
              ))
            )),
          )),
        )),
      )),
    )
  }
  get availableStructures(): Observable<structure[]> {
    return this.structureObservable.pipe(
      scan((acc, curr) => [...acc, curr], [] as structure[]),
    )
  }

  get availableElements(): Observable<element[]>{
    return this.structureObservable.pipe(
      mergeMap((value) => from(value?.elements||[]).pipe(
        distinct()
      )),
      scan((acc, curr) => [...acc, curr], [] as element[]),
    )
  }

  get types() {
    return this._types;
  }

  get applic() {
    return this._applics;
  }

  createStructure(body: Partial<structure>) {
    delete body.elements;
    return this.messages.getSubMessage(this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), this.connectionId.getValue()).pipe(
      switchMap((submessage)=>this.structure.createSubMessageRelation(submessage.name).pipe(
        take(1),
        switchMap((relation) => this.structure.createStructure(body, this.BranchId.getValue(), [relation]).pipe(
          take(1),
          switchMap((transaction) => this.structure.performMutation(this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), this.connectionId.getValue(), transaction).pipe(
            tap(() => {
              this.ui.updateMessages = true;
            })
          ))
        )
        )
      ) 
      )
    )
  }

  relateStructure(structureId: string) {
    return this.structure.createSubMessageRelation(this.SubMessageId.getValue(), structureId).pipe(
      take(1),
      switchMap((relation) => this.structure.addRelation(this.BranchId.getValue(), relation).pipe(
        take(1),
        switchMap((transaction) => this.structure.performMutation(this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), this.connectionId.getValue(), transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    );
  }
  partialUpdateStructure(body: Partial<structure>) {
    return this.structure.changeStructure(body, this.BranchId.getValue()).pipe(
      take(1),
      switchMap((transaction) => this.structure.performMutation(this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), this.connectionId.getValue(), transaction).pipe(
        tap(() => {
          this.ui.updateMessages = true;
        })
      ))
    )
  }

  partialUpdateElement(body: Partial<element>, structureId: string) {
    return this.elements.changeElement(body, this.BranchId.getValue()).pipe(
      take(1),
      switchMap((transaction) => this.elements.performMutation(transaction, this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), structureId, this.connectionId.getValue()).pipe(
        tap(() => {
          this.ui.updateMessages = true;
        })
      ))
    )
  }

  createNewElement(body: Partial<element>, structureId: string, typeId: string) {
    return combineLatest([this.structure.getStructure(this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), structureId, this.connectionId.getValue()).pipe(
      take(1),
      switchMap((structure) => this.elements.createStructureRelation(structure.name))),
      this.typeService.getType(this.BranchId.getValue(), typeId).pipe(take(1), switchMap((type) => this.elements.createPlatformTypeRelation(type.name)))]).pipe(
      take(1),
      map((latest)=>[latest[0],latest[1]]),
      switchMap((relations) => this.elements.createElement(body, this.BranchId.getValue(), relations).pipe(
        take(1),
        switchMap((transaction) => this.elements.performMutation(transaction, this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), structureId, this.connectionId.getValue()).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    )
  }

  relateElement(structureId: string, elementId: string) {
    return this.elements.createStructureRelation(structureId, elementId).pipe(
      take(1),
      switchMap((relation) => this.structure.addRelation(this.BranchId.getValue(), relation).pipe(
        take(1),
        switchMap((transaction) => this.structure.performMutation(this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), this.connectionId.getValue(), transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    )
  }

  changeElementPlatformType(structureId: string, elementId: string, typeId: string) {
    return this.elements.getElement(this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), structureId, elementId, this.connectionId.getValue()).pipe(
      take(1),
      switchMap((element) => combineLatest([this.elements.createPlatformTypeRelation("" + (element.platformTypeId || -1), elementId),this.elements.createPlatformTypeRelation(typeId, elementId)]).pipe( //create relations for delete/add ops
        take(1),
        switchMap(([deleteRelation, addRelation]) => this.elements.deleteRelation(this.BranchId.getValue(), deleteRelation).pipe( //create delete transaction
          take(1),
          switchMap((deleteTransaction) => this.elements.addRelation(this.BranchId.getValue(), addRelation, deleteTransaction).pipe( //create add transaction and merge with delete transaction
            take(1),
            switchMap((transaction) => this.elements.performMutation(transaction, this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), structureId, this.connectionId.getValue()).pipe(
              tap(() => {
                this.ui.updateMessages = true;
              })
            ))
          ))
        ))
      ))
    )
  }

  updatePreferences(preferences: settingsDialogData) {
    return this.createColumnPreferences(preferences).pipe(
      take(1),
      switchMap(([columns,allColumns]) => this.createUserPreferenceBranchTransaction(columns,allColumns, preferences.editable).pipe(
        take(1),
        switchMap((transaction) => this.createUserPreferenceColumnTransaction(columns, allColumns, preferences.editable).pipe(
          take(1),
          switchMap((transaction2) => iif(() => transaction2 !== undefined, combineLatest([this.structure.performMutation(this.BranchId.getValue(), '', '', '', transaction), this.structure.performMutation(this.BranchId.getValue(), '', '', '', transaction2!)]), this.structure.performMutation(this.BranchId.getValue(), '', '', '', transaction))),
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    )
  }
  private createColumnPreferences(preferences: settingsDialogData) {
    let columnPrefs: string[] = [];
    let allColumns: string[] = [];
    let temp = preferences.allHeaders1.concat(preferences.allHeaders2);
    let allHeaders = temp.filter((item, pos) => temp.indexOf(item) === pos);
    preferences.allowedHeaders1.concat(preferences.allowedHeaders2).forEach((header) => {
      if (allHeaders.includes(header) && !(allColumns.includes(`${header}:true`) || allColumns.includes(`${header}:false`))) {
        allColumns.push(`${header}:true`)
        columnPrefs.push(`${header}:true`)
      } else if(!(allColumns.includes(`${header}:true`) || allColumns.includes(`${header}:false`))) {
        allColumns.push(`${header}:false`);
      }
    })
    return of([columnPrefs,allColumns]);
  }

  removeStructureFromSubmessage(structureId:string,submessageId:string) {
    return this.ui.BranchId.pipe(
      switchMap((branchId) => this.structure.deleteSubmessageRelation(branchId, submessageId, structureId).pipe(
        switchMap((transaction) => this.structure.performMutation(branchId, '', '', '', transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    )
  }
  removeElementFromStructure(element: element, structure: structure) {
    return this.ui.BranchId.pipe(
      switchMap((branchId) => this.elements.createStructureRelation(structure.id, element.id).pipe(
        switchMap((relation) => this.elements.deleteRelation(branchId, relation).pipe(
          switchMap((transaction) => this.elements.performMutation(transaction, branchId, '', '', '', '').pipe(
            tap(() => {
              this.ui.updateMessages = true;
            })
          ))
        ))
      ))
    )
  }

  deleteElement(element: element) {
    return this.ui.BranchId.pipe(
      switchMap((branchId) => this.elements.deleteElement(branchId, element.id).pipe(
        switchMap((transaction) => this.elements.performMutation(transaction, branchId, '', '', '', '').pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    )
  }

  deleteStructure(structureId:string) {
    return this.ui.BranchId.pipe(
      switchMap((branchId) => this.structure.deleteStructure(branchId, structureId).pipe(
        switchMap((transaction) => this.structure.performMutation(branchId, '', '', '', transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    )
  }

  private createUserPreferenceBranchTransaction(columnPrefs:string[], allColumns:string[],editMode:boolean) {
    return combineLatest(this.preferences, this.BranchId, this.BranchPrefs).pipe(
      take(1),
      switchMap(([prefs, branch, branchPrefs]) => iif(() => prefs.hasBranchPref,
        iif(() => prefs.columnPreferences.length === 0,
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
                    addAttributes:
                      [
                        { typeName: "MIM Column Preferences", value: allColumns }
                      ]
                  }
                ]
            }
          ),
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
                        { typeName: "MIM Branch Preferences", value: [...branchPrefs, `${branch}:${editMode}`] },
                      ],
                    deleteAttributes:[{typeName:"MIM Column Preferences"}]
                  }
                ]
              }
          )
        ),
        iif(() => prefs.columnPreferences.length === 0,
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
                    { typeName: "MIM Column Preferences", value: allColumns },
                    { typeName: "MIM Branch Preferences", value: `${branch}:${editMode}` }
                  ]
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
                deleteAttributes: [{ typeName: "MIM Column Preferences" }],
                addAttributes:
                  [
                    { typeName: "MIM Branch Preferences", value: `${branch}:${editMode}` },
                  ],
              }
            ]
        
            }
          )
        )
      )))
  }

  private createUserPreferenceColumnTransaction(columnPrefs:string[], allColumns:string[],editMode:boolean) {
    return combineLatest(this.preferences, this.BranchId).pipe(
      switchMap(([prefs, branch]) => iif(() => prefs.columnPreferences.length === 0, of(undefined), of<transaction>(
        {
          branch: "570",
          txComment: 'Updating MIM User Preferences',
          modifyArtifacts:
            [
              {
                id: prefs.id,
                addAttributes:
                  [
                    { typeName: "MIM Column Preferences", value: allColumns }
                  ]
              }
            ]
        }
      )))
    );
  }
}