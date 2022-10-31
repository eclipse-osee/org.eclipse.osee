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
import { BehaviorSubject, combineLatest, concat, from, iif, Observable, of, OperatorFunction } from 'rxjs';
import { concatMap, count, debounceTime, distinct, distinctUntilChanged, filter, map, mergeMap, reduce, repeatWhen, scan, share, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { DiffReportBranchService } from 'src/app/ple-services/ui/diff/diff-report-branch.service';
import { SideNavService } from 'src/app/shared-services/ui/side-nav.service';
import { transaction, transactionToken } from 'src/app/transactions/transaction';
import { changeInstance, changeTypeNumber, ignoreType, ModificationType } from '../../../../types/change-report/change-report.d';
import { applic } from '../../../../types/applicability/applic';
import { ATTRIBUTETYPEID } from '../../../../types/constants/AttributeTypeId.enum';
import { RelationTypeId } from '../../../../types/constants/RelationTypeId.enum';
import { EnumsService } from '../../shared/services/http/enums.service';
import { ApplicabilityListUIService } from '../../shared/services/ui/applicability-list-ui.service';
import { PreferencesUIService } from '../../shared/services/ui/preferences-ui.service';
import { TypesUIService } from '../../shared/services/ui/types-ui.service';
import { PlatformType } from '../../shared/types/platformType';
import { settingsDialogData } from '../../shared/types/settingsdialog';
import { element, elementWithChanges } from '../../shared/types/element';
import { structure, structureWithChanges } from '../../shared/types/structure';
import { ElementService } from '../../shared/services/http/element.service';
import { MessagesStructureService } from '../../shared/services/http/messages.structure.service';
import { StructuresService } from '../../shared/services/http/structures.service';
import { ElementUiService } from './ui.service';
import { MimQuery, PlatformTypeQuery } from '../../shared/types/MimQuery';
import { QueryService } from '../../shared/services/http/query.service';
import { transactionResult } from '../../../../types/change-report/transaction';

@Injectable({
  providedIn: 'root'
})
export class CurrentStructureService {

  private _structuresNoDiff = combineLatest([this.ui.filter, this.ui.BranchId, this.ui.messageId, this.ui.subMessageId,this.ui.connectionId]).pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    filter(([filter,branchId,messageId,subMessageId,connectionId])=>branchId!==''&&messageId!==''&&subMessageId!==''&&connectionId!==''),
    switchMap(x => this.structure.getFilteredStructures(...x).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
    )),
    shareReplay({ bufferSize: 1, refCount: true }),
  )

  private _structures = combineLatest([this.ui.isInDiff, this.differences, this._structuresNoDiff, combineLatest([this.BranchId, this.diffReportService.parentBranch, this.MessageId, this.SubMessageId, this.connectionId])]).pipe(
    switchMap(([isInDiff, differences, structures, [branchId, parentBranch, messageId, subMessageId, connectionId]]) => isInDiff && differences !== undefined && differences.length > 0?
      this._parseDifferencesMulti(differences,structures,parentBranch,branchId,messageId,subMessageId,connectionId):
      of(structures)
    ),
  )

  private _types = this.typeService.types;

  private _expandedRows = new BehaviorSubject<structure[]>([]);
  private _expandedRowsDecreasing = new BehaviorSubject<boolean>(false);

  constructor (private ui: ElementUiService, private structure: StructuresService, private messages: MessagesStructureService, private elements: ElementService, private typeService: TypesUIService, private applicabilityService: ApplicabilityListUIService, private preferenceService: PreferencesUIService, private diffReportService: DiffReportBranchService, private sideNavService: SideNavService, private enumListService:EnumsService, private queryService: QueryService) { }
  
  get expandedRows() {
    return this._expandedRows.asObservable();
  }

  get expandedRowsDecreasing() {
    return this._expandedRowsDecreasing.asObservable();
  }

  get message() {
    return combineLatest([this.BranchId, this.connectionId, this.MessageId, this.SubMessageId]).pipe(
      switchMap(([branch, connection, id, submessageId]) => this.messages.getMessage(branch, connection, id).pipe(
        repeatWhen(_ => this.ui.UpdateRequired),
        tap((value) => {
          this.BreadCrumb = value.name + " > " + value.subMessages.find(submessage=>submessage.id===submessageId)!.name;
        }),
        share(),
      )),
      shareReplay({ bufferSize: 1, refCount: true }),
    )
  }
  set addExpandedRow(value: structure) {
    if (this._expandedRows.getValue().map(s=>s.id).indexOf(value.id) === -1) {
      const temp = this._expandedRows.getValue();
      temp.push(value);
      this._expandedRows.next(temp)
    }
    this._expandedRowsDecreasing.next(false)
  }

  set removeExpandedRow(value: structure) {
    if (this._expandedRows.getValue().map(s=>s.id).indexOf(value.id) > -1) {
      const temp = this._expandedRows.getValue();
      temp.splice(this._expandedRows.getValue().indexOf(value), 1);
      this._expandedRows.next(temp);
    }
    this._expandedRowsDecreasing.next(true)
  }
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
  
  get breadCrumbs() {
    return this.ui.subMessageBreadCrumbs;
  }

  set BreadCrumb(value: string) {
    this.ui.subMessageBreadCrumbsString = value;
  }

  set singleStructureIdValue(value: string) {
    this.ui.singleStructureIdValue = value;
  }

  get singleStructureId() {
    return this.ui.singleStructureId;
  }

  get preferences() {
    return this.preferenceService.preferences;
  }

  get BranchPrefs() {
    return this.preferenceService.BranchPrefs;
  }

  set toggleDone(value: boolean) {
    this.ui.toggleDone = value;
  }

  get done() {
    return this.ui.done;
  }
  
  get updated() {
    return this.ui.UpdateRequired;
  }

  get sideNavContent() {
    return this.sideNavService.sideNavContent;
  }

  set sideNav(value: { opened: boolean, field: string, currentValue: string | number | applic|boolean, previousValue?: string | number | applic| boolean,transaction?:transactionToken, user?: string, date?: string }) {
    this.sideNavService.sideNav = value;
  }

  set update(value: boolean) {
    this.ui.updateMessages = value;
  }

  get units() {
    return this.enumListService.units;
  }

  get availableStructures() {
    return this.BranchId.pipe(
      switchMap(id => this.structure.getStructures(id).pipe(
        shareReplay({ bufferSize: 1, refCount: true })
      )),
      shareReplay({ bufferSize: 1, refCount: true })
    )
  }

  get availableElements() {
    return this.BranchId.pipe(
      take(1),
      switchMap((id)=>this.elements.getFilteredElements(id,''))
    )
  }

  getType(typeId: string) {
    return this.BranchId.pipe(
      take(1),
      switchMap((id)=>this.typeService.getType(typeId))
    )
  }

  get types() {
    return this._types;
  }

  get applic() {
    return this.applicabilityService.applic;
  }

  get differences() {
    return this.ui.differences
  }
  set difference(value: changeInstance[]) {
    this.ui.difference = value;
  }

  get isInDiff() {
    return this.ui.isInDiff;
  }

  set DiffMode(value:boolean) {
    this.ui.DiffMode = value;
  }

  get connectionsRoute() {
    return combineLatest([this.branchType, this.BranchId]).pipe(
      switchMap(([branchType, BranchId])=>of("/ple/messaging/connections/" + branchType + "/" + BranchId))
    )
  }

  updatePlatformTypeValue(type: Partial<PlatformType>) {
    return this.typeService.changeType(type).pipe(
      switchMap((transaction)=>this.typeService.performMutation(transaction))
    )
  }

  createStructure(body: Partial<structure>, afterStructure?: string) {
    delete body.elements;
    return combineLatest([this.BranchId, this.SubMessageId]).pipe(
      take(1),
      switchMap(([branch, submessageId]) => this.structure.createSubMessageRelation(submessageId, undefined, afterStructure).pipe(
        take(1),
        switchMap((relation) => this.structure.createStructure(body, branch, [relation]).pipe(
          take(1),
          switchMap((transaction) => this.structure.performMutation(transaction).pipe(
            tap(() => {
              this.ui.updateMessages = true;
            })
          ))
        ))
      ))
    );

  }

  relateStructure(structureId: string, afterStructure?: string) {
    return combineLatest([this.BranchId, this.SubMessageId]).pipe(
      take(1),
      switchMap(([branch,submessageId]) => this.structure.createSubMessageRelation(submessageId, structureId, afterStructure).pipe(
        take(1),
        switchMap((relation) => this.structure.addRelation(branch, relation).pipe(
          take(1),
          switchMap((transaction) => this.structure.performMutation(transaction).pipe(
            tap(() => {
              this.ui.updateMessages = true;
            })
          ))
        ))
      ))
    );
  }
  partialUpdateStructure(body: Partial<structure>) {
    return this.BranchId.pipe(
      take(1),
      switchMap(branchId => this.structure.changeStructure(body, branchId).pipe(
        take(1),
        switchMap((transaction) => this.structure.performMutation(transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        )))
      )
    );
  }

  partialUpdateElement(body: Partial<element>, structureId: string) {
    return this.BranchId.pipe(
      take(1),
      switchMap(branchId => this.elements.changeElement(body, branchId).pipe(
        take(1),
        switchMap((transaction) => this.elements.performMutation(transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    );
  }

  createNewElement(body: Partial<element>, structureId: string, typeId: string, afterElement?: string) {
    const { units,autogenerated, ...element } = body;
    return combineLatest([this.BranchId, this.elements.createStructureRelation(structureId, undefined, afterElement), this.elements.createPlatformTypeRelation(typeId)]).pipe(
      take(1),
      switchMap(([branchId, structureRelation, platformRelation]) => of([structureRelation, platformRelation]).pipe(
        switchMap((relations) => this.elements.createElement(element, branchId, relations).pipe(
          take(1),
          switchMap((transaction) => this.elements.performMutation(transaction).pipe(
            tap(() => {
              this.ui.updateMessages = true;
            })
          ))
        ))
      ))
    );
  }

  relateElement(structureId: string, elementId: string, afterElement?: string) {
    return this.BranchId.pipe(
      take(1),
      switchMap(branchId => this.elements.createStructureRelation(structureId, elementId, afterElement).pipe(
        take(1),
        switchMap((relation) => this.structure.addRelation(branchId, relation).pipe(
          take(1),
          switchMap((transaction) => this.structure.performMutation(transaction).pipe(
            tap(() => {
              this.ui.updateMessages = true;
            })
          ))
        ))
      ))
    );
  }

  changeElementPlatformType(structureId: string, elementId: string, type:PlatformType) {
    //need to modify to change element's enumLiteral attribute
    return combineLatest([this.BranchId,this.connectionId,this.MessageId, this.SubMessageId]).pipe(
      take(1),
      switchMap(([branchId,connection,message, submessage]) => this.elements.getElement(branchId, message, submessage, structureId, elementId, connection).pipe(
        take(1),
        switchMap((element) => combineLatest([this.elements.createPlatformTypeRelation("" + (element.platformTypeId || -1), elementId), this.elements.createPlatformTypeRelation(type.id||'', elementId)]).pipe( //create relations for delete/add ops
          take(1),
          switchMap(([deleteRelation, addRelation]) => this.elements.deleteRelation(branchId, deleteRelation).pipe( //create delete transaction
            take(1),
            switchMap((deleteTransaction) => this.elements.addRelation(branchId, addRelation, deleteTransaction).pipe( //create add transaction and merge with delete transaction
              take(1),
              switchMap(addTransaction => this.elements.changeElement({ id:elementId, enumLiteral: type.enumSet?.description }, branchId,addTransaction).pipe(
                take(1),
                switchMap((transaction) => this.elements.performMutation(transaction).pipe(
                  tap(() => {
                    this.ui.updateMessages = true;
                  })
                ))
              ))
            ))
          ))
        ))
      ))
    );
  }

  updatePreferences(preferences: settingsDialogData) {
    return concat(this._deleteColumnPrefs(), this._deleteBranchPrefs(), this._setBranchPrefs(preferences.editable), this._setColumnPrefs(preferences)).pipe(
      take(4),
      reduce((acc, curr) => [...acc, curr], [] as transactionResult[]),
      tap(() => {
        this.ui.updateMessages = true;
      })
    );
  }

  private _deleteBranchPrefs() {
    return this.preferences.pipe(
      take(1),
      switchMap(prefs => iif(() => prefs.hasBranchPref,
      of<transaction>({
        branch: "570",
        txComment: 'Updating MIM User Preferences',
        modifyArtifacts:
          [
            {
              id: prefs.id,
              deleteAttributes:[{typeName:"MIM Branch Preferences"}]
            }
          ]
        }),
        of(undefined)
      )),
      switchMap(transaction => iif(() => transaction !== undefined,
      this.structure.performMutation(transaction!),
        of()))
    )
  }

  private _deleteColumnPrefs() {
    return this.preferences.pipe(
      take(1),
      switchMap(prefs => iif(() => prefs.columnPreferences.length !== 0,
        of<transaction>({
          branch: "570",
          txComment: 'Updating MIM User Preferences',
          modifyArtifacts:
            [
              {
                id: prefs.id,
                deleteAttributes:[{typeName:"MIM Column Preferences"}]
              }
            ]
          }),
        of(undefined)
      )),
      switchMap(transaction => iif(() => transaction !== undefined,
      this.structure.performMutation(transaction!),
        of()))
    )
  }

  private _setBranchPrefs(editMode:boolean) {
    return combineLatest([this.preferences, this.BranchId, this.BranchPrefs]).pipe(
      take(1),
      switchMap(([prefs, branch, branchPrefs]) => iif(() => prefs.hasBranchPref,
      of<transaction>(
        {
          branch: "570",
          txComment: 'Updating MIM User Preferences',
          modifyArtifacts:
            [
              {
                id: prefs.id,
                addAttributes:
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
                { typeName: "MIM Branch Preferences", value: `${branch}:${editMode}` },
              ],
          }
        ]
        }
      )
      )),
      switchMap(transaction=>this.structure.performMutation(transaction))
    )
  }

  private _setColumnPrefs(newPreferences: settingsDialogData) {
    return combineLatest([this.preferences,this.createColumnPreferences(newPreferences)]).pipe(
      take(1),
      switchMap(([prefs, [columns, allColumns]]) => of<transaction>(
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
              ]
          }
        ]
        }
      )),
      switchMap(transaction=>this.structure.performMutation(transaction))
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
        switchMap((transaction) => this.structure.performMutation(transaction).pipe(
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
          switchMap((transaction) => this.elements.performMutation(transaction).pipe(
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
        switchMap((transaction) => this.elements.performMutation(transaction).pipe(
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
        switchMap((transaction) => this.structure.performMutation(transaction).pipe(
          tap(() => {
            this.ui.updateMessages = true;
          })
        ))
      ))
    )
  }

  getStructureRepeating(structureId: string) {
    return combineLatest([this.BranchId,this.diffReportService.parentBranch, this.MessageId, this.SubMessageId, this.connectionId,this.ui.filter]).pipe(
      switchMap(([branch,parentBranch, message, submessage, connection,filter]) => combineLatest([this.structure.getStructure(branch, message, submessage, structureId, connection,filter),this.ui.isInDiff,this.differences]).pipe(
        repeatWhen(_ => this.ui.UpdateRequired),
        switchMap(([structure, isInDiff, differences]) =>  isInDiff && differences!==undefined && differences.length>0?
          this._parseDifferences(differences,structure,parentBranch,branch,message,submessage,connection):
          //no differences
          of(structure)
        ))
      ),
    )
  }
  private _parseDifferences(differences: changeInstance[] | undefined, _oldStructure: Required<structure>,parentBranch:string,branch:string,message:string,submessage:string,connection:string) {
    let structure = JSON.parse(JSON.stringify(_oldStructure)) as Required<structure>;
    return of(differences).pipe(
      filter((val) => val !== undefined) as OperatorFunction<changeInstance[] | undefined, changeInstance[]>,
      switchMap((differenceArray) => of(differenceArray).pipe(
        map((differenceArray) => differenceArray.sort((a, b) => ["111", "222", "333", "444"].indexOf(a.changeType.id) - ["111", "222", "333", "444"].indexOf(b.changeType.id))),
        mergeMap((differences) => from(differences).pipe(
          filter((val) => val.ignoreType !== ignoreType.DELETED_AND_DNE_ON_DESTINATION),
          filter((val)=>val.artId===structure.id || val.artIdB===structure.id||val.itemId===structure.id||(structure.elements?.map((a)=>a.id).some((el)=>el===val.artId||el===val.artIdB||el===val.itemId)||false) ||(typeof val.itemTypeId === "object" && "id" in val.itemTypeId && val.itemTypeId.id === RelationTypeId.INTERFACESTRUCTURECONTENT)),
          mergeMap((change) => iif(() => change.changeType.id === changeTypeNumber.ARTIFACT_CHANGE,
            iif(() => change.currentVersion.modType === ModificationType.NEW && change.baselineVersion.modType === ModificationType.NONE && change.destinationVersion.modType === ModificationType.NONE && !change.deleted,
              iif(() => change.artId === structure.id,
                of(structure).pipe(
                  map((val) => {
                    structure = this._structureChangeSetup(structure);
                    structure.applicability = change.currentVersion.applicabilityToken as applic;
                    (structure as structureWithChanges).changes.applicability = {
                      previousValue: change.baselineVersion.applicabilityToken,
                      currentValue: change.currentVersion.applicabilityToken,
                      transactionToken: change.currentVersion.transactionToken
                    };
                    (structure as structureWithChanges).added = true;
                    return structure as structureWithChanges
                  })
                ),
                iif(() => structure.elements?.map(a => a.id).includes(change.artId) || false,
                  of(structure).pipe(
                    map((val) => {
                      let index = structure.elements?.findIndex((el) => el.id === change.artId);
                      structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                      ((structure.elements)[index] as elementWithChanges).changes.applicability = {
                        previousValue: change.baselineVersion.applicabilityToken as applic,
                        currentValue: change.currentVersion.applicabilityToken as applic,
                        transactionToken: change.currentVersion.transactionToken
                      };
                      ((structure.elements)[index] as elementWithChanges).added = true;
                      (structure as structureWithChanges).hasElementChanges = true;
                      return structure as structureWithChanges
                    })
                  ),
                  of()
                ) //check if in element array, and mark as added, else check type of object (structure/element) and mark as deleted
              ),
              iif(() => change.currentVersion.modType === ModificationType.NEW && change.baselineVersion.modType === ModificationType.NEW,
                iif(() => !change.deleted && structure.elements?.map(a => a.id).includes(change.artId),
                  of(structure).pipe(
                    map((val) => {
                      let index = structure.elements?.findIndex((el) => el.id === change.artId);
                      structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                      ((structure.elements)[index] as elementWithChanges).changes.applicability = {
                        previousValue: change.baselineVersion.applicabilityToken as applic,
                        currentValue: change.currentVersion.applicabilityToken as applic,
                        transactionToken: change.currentVersion.transactionToken
                      };
                      (structure as structureWithChanges).hasElementChanges = true;
                      return structure as structureWithChanges;
                    })
                  ),
                  of()
                ),
                of()
              ) //deleted/changed
            ),
            iif(() => change.changeType.id === changeTypeNumber.ATTRIBUTE_CHANGE,
              iif(() => change.artId === structure.id,
                iif(() => change.itemTypeId === ATTRIBUTETYPEID.DESCRIPTION,
                  of(structure).pipe(
                    map((structure) => {
                      structure = this._structureChangeSetup(structure);
                      (structure as structureWithChanges).changes.description = {
                        previousValue: change.baselineVersion.value,
                        currentValue: change.currentVersion.value,
                        transactionToken: change.currentVersion.transactionToken
                      }
                      return structure as structureWithChanges;
                    })
                  ),
                  iif(() => change.itemTypeId === ATTRIBUTETYPEID.NAME,
                  of(structure).pipe(
                    map((structure) => {
                      structure = this._structureChangeSetup(structure);
                      (structure as structureWithChanges).changes.name = {
                        previousValue: change.baselineVersion.value,
                        currentValue: change.currentVersion.value,
                        transactionToken: change.currentVersion.transactionToken
                      }
                      return structure as structureWithChanges;
                    })
                  ),
                    iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMAXSIMULTANEITY,
                    of(structure).pipe(
                      map((structure) => {
                        structure = this._structureChangeSetup(structure);
                        (structure as structureWithChanges).changes.interfaceMaxSimultaneity = {
                          previousValue: change.baselineVersion.value,
                          currentValue: change.currentVersion.value,
                          transactionToken: change.currentVersion.transactionToken
                        }
                        return structure as structureWithChanges;
                      })
                    ),
                      iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMINSIMULTANEITY,
                      of(structure).pipe(
                        map((structure) => {
                          structure = this._structureChangeSetup(structure);
                          (structure as structureWithChanges).changes.interfaceMinSimultaneity = {
                            previousValue: change.baselineVersion.value,
                            currentValue: change.currentVersion.value,
                            transactionToken: change.currentVersion.transactionToken
                          }
                          return structure as structureWithChanges;
                        })
                      ),
                        iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACESTRUCTURECATEGORY,
                        of(structure).pipe(
                          map((structure) => {
                            structure = this._structureChangeSetup(structure);
                            (structure as structureWithChanges).changes.interfaceStructureCategory = {
                              previousValue: change.baselineVersion.value,
                              currentValue: change.currentVersion.value,
                              transactionToken: change.currentVersion.transactionToken
                            }
                            return structure as structureWithChanges;
                          })
                        ),
                          iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACETASKFILETYPE,
                          of(structure).pipe(
                            map((structure) => {
                              structure = this._structureChangeSetup(structure);
                              (structure as structureWithChanges).changes.interfaceTaskFileType = {
                                previousValue: change.baselineVersion.value,
                                currentValue: change.currentVersion.value,
                                transactionToken: change.currentVersion.transactionToken
                              }
                              return structure as structureWithChanges;
                            })
                          ),
                            of()
                          )
                        )
                      )
                    )
                  )
                ),
                iif(() => structure.elements?.map(a => a.id).includes(change.artId),
                  iif(() => change.itemTypeId === ATTRIBUTETYPEID.DESCRIPTION,
                    of(structure).pipe(
                      map((structure) => {
                        let index = structure.elements?.findIndex((el) => el.id === change.artId);
                        structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                        ((structure.elements)[index] as elementWithChanges).changes.description = {
                          previousValue: change.baselineVersion.value as string,
                          currentValue: change.currentVersion.value as string,
                          transactionToken: change.currentVersion.transactionToken
                        };
                        (structure as structureWithChanges).hasElementChanges = true;
                        return structure as structureWithChanges
                      })
                    ),
                    iif(() => change.itemTypeId === ATTRIBUTETYPEID.NAME,
                      of(structure).pipe(
                        map((structure) => {
                          let index = structure.elements?.findIndex((el) => el.id === change.artId);
                          structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                          ((structure.elements)[index] as elementWithChanges).changes.name = {
                            previousValue: change.baselineVersion.value as string,
                            currentValue: change.currentVersion.value as string,
                            transactionToken: change.currentVersion.transactionToken
                          };
                          (structure as structureWithChanges).hasElementChanges = true;
                          return structure as structureWithChanges
                        })
                      ),
                      iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEELEMENTALTERABLE,
                        of(structure).pipe(
                          map((structure) => {
                            let index = structure.elements?.findIndex((el) => el.id === change.artId);
                            structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                            ((structure.elements)[index] as elementWithChanges).changes.interfaceElementAlterable = {
                              previousValue: change.baselineVersion.value as boolean,
                              currentValue: change.currentVersion.value as boolean,
                              transactionToken: change.currentVersion.transactionToken
                            };
                            (structure as structureWithChanges).hasElementChanges = true;
                            return structure as structureWithChanges
                          })
                        ),
                        iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEELEMENTEND,
                          of(structure).pipe(
                            map((structure) => {
                              let index = structure.elements?.findIndex((el) => el.id === change.artId);
                              structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                              ((structure.elements)[index] as elementWithChanges).changes.interfaceElementIndexEnd = {
                                previousValue: change.baselineVersion.value as number,
                                currentValue: change.currentVersion.value as number,
                                transactionToken: change.currentVersion.transactionToken
                              };
                              (structure as structureWithChanges).hasElementChanges = true;
                              return structure as structureWithChanges
                            })
                          ),
                          iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEELEMENTSTART,
                            of(structure).pipe(
                              map((structure) => {
                                let index = structure.elements?.findIndex((el) => el.id === change.artId);
                                structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                                ((structure.elements)[index] as elementWithChanges).changes.interfaceElementIndexStart = {
                                  previousValue: change.baselineVersion.value as number,
                                  currentValue: change.currentVersion.value as number,
                                  transactionToken: change.currentVersion.transactionToken
                                };
                                (structure as structureWithChanges).hasElementChanges = true;
                                return structure as structureWithChanges
                              })
                            ),
                            iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEENUMLITERAL,
                            of(structure).pipe(
                              map((structure) => {
                                let index = structure.elements?.findIndex((el) => el.id === change.artId);
                                structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                                ((structure.elements)[index] as elementWithChanges).changes.enumLiteral = {
                                  previousValue: change.baselineVersion.value as string,
                                  currentValue: change.currentVersion.value as string,
                                  transactionToken: change.currentVersion.transactionToken
                                };
                                (structure as structureWithChanges).hasElementChanges = true;
                                return structure as structureWithChanges
                              })
                            ),
                              of()
                            )
                          )
                        )
                      )
                    )
                  ),
                  iif(() => structure.elements?.map(a => a.platformTypeId?.toString()).includes(change.artId),
                    iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEPLATFORMTYPEUNITS,
                      of(structure).pipe(
                        map((structure) => {
                          let index = structure.elements?.findIndex((el) => el.platformTypeId?.toString() === change.artId);
                          structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                          ((structure.elements)[index] as elementWithChanges).changes.units = {
                            previousValue: change.baselineVersion.value as string,
                            currentValue: change.currentVersion.value as string,
                            transactionToken: change.currentVersion.transactionToken
                          };
                          (structure as structureWithChanges).hasElementChanges = true;
                          return structure as structureWithChanges
                        })
                      ),
                      iif(() => change.itemTypeId === ATTRIBUTETYPEID.NAME,
                        of(structure).pipe(
                          map((structure) => {
                            let index = structure.elements?.findIndex((el) => el.platformTypeId?.toString() === change.artId);
                            structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                            ((structure.elements)[index] as elementWithChanges).changes.platformTypeName2 = {
                              previousValue: change.baselineVersion.value as string,
                              currentValue: change.currentVersion.value as string,
                              transactionToken: change.currentVersion.transactionToken
                            };
                            (structure as structureWithChanges).hasElementChanges = true;
                            return structure as structureWithChanges
                          })
                        ),
                        of(structure)
                      )
                    ),
                    of(structure))
                ) //element has changed attributes
              ),
              iif(() => change.changeType.id === changeTypeNumber.RELATION_CHANGE,
                iif(() => typeof change.itemTypeId === "object" && "id" in change.itemTypeId && change.itemTypeId.id === RelationTypeId.INTERFACESUBMESSAGECONTENT && change.artIdB === structure.id,
                  of(change), //mark structure as added/deleted
                  iif(() => typeof change.itemTypeId === "object" && "id" in change.itemTypeId && change.itemTypeId.id === RelationTypeId.INTERFACESTRUCTURECONTENT && change.artId === structure.id,
                    iif(() => change.currentVersion.modType === ModificationType.NEW && change.baselineVersion.modType === ModificationType.NONE && structure.elements?.map(a => a.id).includes(change.artIdB),
                      of(change).pipe(
                        map(() => {
                          let index = structure.elements?.findIndex((el) => el.id === change.artIdB);
                          structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                          ((structure.elements)[index] as elementWithChanges).changes.applicability = {
                            previousValue: change.baselineVersion.applicabilityToken as applic,
                            currentValue: change.currentVersion.applicabilityToken as applic,
                            transactionToken: change.currentVersion.transactionToken
                          };
                          (structure as structureWithChanges).hasElementChanges = true;
                          ((structure.elements)[index] as elementWithChanges).added=true;
                          return structure as structureWithChanges
                        })
                      ),
                      iif(() => change.currentVersion.modType === ModificationType.DELETED && change.baselineVersion.modType !== ModificationType.NONE && change.baselineVersion.modType !== ModificationType.DELETED && change.baselineVersion.modType !== ModificationType.DELETED_ON_DESTINATION,
                        this.elements.getElement(parentBranch, message, submessage, structure.id, change.artIdB, connection).pipe(
                          map((initialEl) => {
                            return {
                              ...initialEl,
                              changes: {
                                name: { previousValue: initialEl.name, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                description: { previousValue: initialEl.description, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                notes: { previousValue: initialEl.notes, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                platformTypeName2: { previousValue: initialEl.platformTypeName2, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                interfaceElementIndexEnd: { previousValue: initialEl.interfaceElementIndexEnd, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                interfaceElementIndexStart: { previousValue: initialEl.interfaceElementIndexStart, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                interfaceElementAlterable: { previousValue: initialEl.interfaceElementAlterable, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                enumLiteral:{ previousValue: initialEl.enumLiteral, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                applicability:{previousValue:initialEl.applicability,currentValue:'',transactionToken:change.currentVersion.transactionToken},
                              }
                          }}),
                          map((element) => {
                            structure.elements.push({
                              ...element,
                              deleted: true
                            });
                            structure.numElements++;
                            structure = this._structureChangeSetup(structure);
                            (structure as structureWithChanges).changes.numElements = true;
                            structure.elements.sort((a, b) => Number(a.id) - Number(b.id));
                            return structure as structureWithChanges;
                          })
                        ),
                        of(change)    
                      )
                    ),//check if an element relation changed on specific structure id
                    iif(() => typeof change.itemTypeId === "object" && "id" in change.itemTypeId && change.itemTypeId.id === RelationTypeId.INTERFACEELEMENTPLATFORMTYPE && structure.elements?.map((a) => a.id).includes(change.artId),
                      iif(() => change.currentVersion.modType === ModificationType.NEW && change.baselineVersion.modType === ModificationType.NONE,
                        this.typeService.getTypeFromBranch(branch, change.artIdB).pipe(
                          map((type) => {
                            let index = structure.elements?.findIndex((el) => el.id === change.artId);
                            structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                            if (((structure.elements)[index] as elementWithChanges).changes.platformTypeName2===undefined) {
                              ((structure.elements)[index] as elementWithChanges).changes.platformTypeName2 = {
                                previousValue: '',
                                currentValue: type.name,
                                transactionToken: change.currentVersion.transactionToken
                              };
                            } else if(((structure.elements)[index] as elementWithChanges).changes.platformTypeName2!==undefined && ((structure.elements)[index] as elementWithChanges).changes.platformTypeName2?.currentValue!==((structure.elements)[index] as elementWithChanges).platformTypeName2) {
                              ((structure.elements)[index] as elementWithChanges).changes.platformTypeName2!.currentValue = type.name;
                              ((structure.elements)[index] as elementWithChanges).changes.platformTypeName2!.transactionToken = change.currentVersion.transactionToken
                            }
                            (structure as structureWithChanges).hasElementChanges = true;
                            return structure as structureWithChanges
                          })
                        ),
                        iif(() => change.currentVersion.modType === ModificationType.DELETED && change.baselineVersion.modType !== ModificationType.NONE,
                          this.typeService.getTypeFromBranch(branch, change.artIdB).pipe(
                            map((type) => {
                              let index = structure.elements?.findIndex((el) => el.id === change.artId);
                              structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                              if (((structure.elements)[index] as elementWithChanges).changes.platformTypeName2===undefined) {
                                ((structure.elements)[index] as elementWithChanges).changes.platformTypeName2 = {
                                  previousValue: type.name,
                                  currentValue: '',
                                  transactionToken: change.currentVersion.transactionToken
                                };
                              } else if(((structure.elements)[index] as elementWithChanges).changes.platformTypeName2!==undefined && ((structure.elements)[index] as elementWithChanges).changes.platformTypeName2?.currentValue!==((structure.elements)[index] as elementWithChanges).platformTypeName2) {
                                ((structure.elements)[index] as elementWithChanges).changes.platformTypeName2!.previousValue = type.name;
                                ((structure.elements)[index] as elementWithChanges).changes.platformTypeName2!.transactionToken = change.currentVersion.transactionToken
                              } else {
                                ((structure.elements)[index] as elementWithChanges).changes.platformTypeName2!.previousValue = type.name;
                              }
                              (structure as structureWithChanges).hasElementChanges = true;
                              return structure as structureWithChanges
                            })
                          ),
                          of()
                        )
                      ),
                      of()
                    )
                  ) 
                ),
                iif(() => change.changeType.id === changeTypeNumber.TUPLE_CHANGE,
                  //these should be ignored
                  of(),
                  of()
                )
              )
            )
          )),
          // tap((val) => {
          //   console.log(val)
          //   console.log(structure)
          // }),
          count()
        )),
      )),


      switchMap((val) => of(structure as structure | structureWithChanges)),
    )
  }
  private _parseDifferencesMulti(differences:changeInstance[]|undefined,_oldStructures:(Required<structure>)[],parentBranch:string,branchId:string,messageId:string,subMessageId:string,connectionId:string) {
    let structures = JSON.parse(JSON.stringify(_oldStructures)) as (Required<structure>)[];
    if (differences !==undefined && structures!==undefined) {
      return of(differences).pipe(
        filter((val) => val !== undefined) as OperatorFunction<changeInstance[] | undefined, changeInstance[]>,
        switchMap((differenceArray) => of(differenceArray).pipe(
          map((differenceArray) => differenceArray.sort((a, b) => {
            if (a.changeType.id === changeTypeNumber.RELATION_CHANGE && typeof a.itemTypeId === "object" && "id" in a.itemTypeId && b.changeType.id === changeTypeNumber.RELATION_CHANGE && typeof b.itemTypeId === "object" && "id" in b.itemTypeId) {
              const relFactor = [RelationTypeId.INTERFACESTRUCTURECONTENT, RelationTypeId.INTERFACESUBMESSAGECONTENT, RelationTypeId.INTERFACEELEMENTPLATFORMTYPE].indexOf(a.itemTypeId.id) - [RelationTypeId.INTERFACESTRUCTURECONTENT, RelationTypeId.INTERFACESUBMESSAGECONTENT, RelationTypeId.INTERFACEELEMENTPLATFORMTYPE].indexOf(b.itemTypeId.id)
                -((a.itemTypeId.id===RelationTypeId.INTERFACESTRUCTURECONTENT && (b.itemTypeId.id===RelationTypeId.INTERFACESTRUCTURECONTENT)|| (a.itemTypeId.id===RelationTypeId.INTERFACESUBMESSAGECONTENT && b.itemTypeId.id===RelationTypeId.INTERFACESUBMESSAGECONTENT)?[ModificationType.NEW,ModificationType.DELETED].indexOf(a.currentVersion.modType)-[ModificationType.NEW,ModificationType.DELETED].indexOf(b.currentVersion.modType):0))-(Number(a.artIdB)-Number(b.artIdB));
              return ["111", "222", "333", "444"].indexOf(a.changeType.id) - ["111", "222", "333", "444"].indexOf(b.changeType.id) - relFactor;
            }
            return ["111", "222", "333", "444"].indexOf(a.changeType.id) - ["111", "222", "333", "444"].indexOf(b.changeType.id)
          })),
          concatMap((differences) => from(differences).pipe(
            filter((val) => val.ignoreType !== ignoreType.DELETED_AND_DNE_ON_DESTINATION),
            filter((val)=>val.changeType.id!==changeTypeNumber.TUPLE_CHANGE),
            filter((val) => structures.map((a) => a.id).includes(val.artId) || structures.map((a) => a.id).includes(val.artIdB) ||!((structures.map((a)=>a.elements).flat() as (element|elementWithChanges|undefined)[]).includes(undefined) && (structures.map((a) => a.elements?.map((b) => b.id)).flat().includes(val.artId) || structures.map((a) => a.elements?.map((b) => b.id)).flat().includes(val.artIdB)))  || val.artId === subMessageId),
            concatMap((change) => iif(() => change.changeType.id === changeTypeNumber.ARTIFACT_CHANGE,
              iif(() => change.currentVersion.modType === ModificationType.NEW && change.baselineVersion.modType === ModificationType.NONE && change.destinationVersion.modType === ModificationType.NONE && !change.deleted,
                iif(() => structures.map((a) => a.id).includes(change.artId),
                  of(structures).pipe(
                    take(1),
                    concatMap((structures) => from(structures).pipe(
                      switchMap((structure) => iif(() => change.artId === structure.id,
                        of(structure).pipe(
                          map((val) => {
                            structure = this._structureChangeSetup(structure);
                            structure.applicability = change.currentVersion.applicabilityToken as applic;
                            (structure as structureWithChanges).changes.applicability = {
                              previousValue: change.baselineVersion.applicabilityToken,
                              currentValue: change.currentVersion.applicabilityToken,
                              transactionToken: change.currentVersion.transactionToken
                            };
                            (structure as structureWithChanges).added = true;
                            return structure as structureWithChanges
                          })
                        ),
                        of(structure)
                      ))
                    )),
                    reduce((acc, curr) => [...acc, curr], [] as (structure | structureWithChanges)[])
                  ),
                  iif(() => structures.map((a) => a.elements?.map((b) => b.id)).flat().includes(change.artId),
                    of(structures).pipe(
                      take(1),
                      concatMap((structures) => from(structures).pipe(
                        switchMap((structure) => iif(() => structure.elements?.map((a) => a.id).includes(change.artId),
                          of(structure).pipe(
                            map((val) => {
                              const index = structure.elements?.findIndex((el) => el.id === change.artId);
                              structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                              ((structure.elements)[index] as elementWithChanges).changes.applicability = {
                                previousValue: change.baselineVersion.applicabilityToken as applic,
                                currentValue: change.currentVersion.applicabilityToken as applic,
                                transactionToken: change.currentVersion.transactionToken
                              };
                              ((structure.elements)[index] as elementWithChanges).added = true;
                              (structure as structureWithChanges).hasElementChanges = true;
                              return structure as structureWithChanges
                            })
                          ),
                          of(structure)
                        ))
                      )),
                      reduce((acc, curr) => [...acc, curr], [] as (structure | structureWithChanges)[])
                    ),
                    of(change)
                  )
                ),
                iif(() => change.currentVersion.modType === ModificationType.NEW && change.baselineVersion.modType === ModificationType.NEW,
                  iif(() => !change.deleted,
                    iif(() => structures.map((a) => a.elements?.map((b) => b.id)).flat().includes(change.artId),
                      of(structures).pipe(
                        take(1),
                        concatMap((structures) => from(structures).pipe(
                          switchMap((structure) => iif(() => structure.elements?.map((a) => a.id).includes(change.artId),
                            of(structure).pipe(
                              map((val) => {
                                const index = structure.elements?.findIndex((el) => el.id === change.artId);
                                structure.elements[index] = this._elementChangeSetup(structure.elements[index]);
                                ((structure.elements)[index] as elementWithChanges).changes.applicability = {
                                  previousValue: change.baselineVersion.applicabilityToken as applic,
                                  currentValue: change.currentVersion.applicabilityToken as applic,
                                  transactionToken: change.currentVersion.transactionToken
                                };
                                (structure as structureWithChanges).hasElementChanges = true;
                                return structure as structureWithChanges;
                              })
                            ),
                            of(structure)
                          ))
                        )),
                        reduce((acc, curr) => [...acc, curr], [] as (structure | structureWithChanges)[])
                      ),
                      iif(() => structures.map((a) => a.id).includes(change.artId),
                      of(structures).pipe(
                        take(1),
                        concatMap((structures) => from(structures).pipe(
                          switchMap((structure) => iif(() => change.artId === structure.id,
                            of(structure).pipe(
                              map((val) => {
                                structure = this._structureChangeSetup(structure);
                                (structure as structureWithChanges).changes.applicability = {
                                  previousValue: change.baselineVersion.applicabilityToken,
                                  currentValue: change.currentVersion.applicabilityToken,
                                  transactionToken: change.currentVersion.transactionToken
                                };
                                return structure as structureWithChanges;
                              })
                            ),
                            of(structure)
                          ))
                        )),
                        reduce((acc, curr) => [...acc, curr], [] as (structure | structureWithChanges)[])
                      ),
                      of(change)
                    )
                    ),
                    iif(() => structures.map((a) => a.id).includes(change.artId),
                      of(structures).pipe(
                        take(1),
                        concatMap((structures) => from(structures).pipe(
                          switchMap((structure) => iif(() => change.artId === structure.id,
                            of(structure).pipe(
                              map((val) => {
                                structure = this._structureChangeSetup(structure);
                                (structure as structureWithChanges).changes.applicability = {
                                  previousValue: change.baselineVersion.applicabilityToken,
                                  currentValue: change.currentVersion.applicabilityToken,
                                  transactionToken: change.currentVersion.transactionToken
                                };
                                return structure as structureWithChanges;
                              })
                            ),
                            of(structure)
                          ))
                        )),
                        reduce((acc, curr) => [...acc, curr], [] as (structure | structureWithChanges)[])
                      ),
                      of(change)
                    )
                  ),
                  of(change)
                )
              ),
              iif(() => change.changeType.id === changeTypeNumber.ATTRIBUTE_CHANGE,
                iif(() => structures.map((a) => a.id).includes(change.artId),
                  of(structures).pipe(
                    take(1),
                    concatMap((structures) => from(structures).pipe(
                      switchMap((structure) => iif(() => change.artId === structure.id,
                        iif(() => change.itemTypeId === ATTRIBUTETYPEID.DESCRIPTION,
                          of(structure).pipe(
                            map((structure) => {
                              structure = this._structureChangeSetup(structure);
                              (structure as structureWithChanges).changes.description = {
                                previousValue: change.baselineVersion.value,
                                currentValue: change.currentVersion.value,
                                transactionToken: change.currentVersion.transactionToken
                              }
                              return structure as structureWithChanges;
                            })
                          ),
                          iif(() => change.itemTypeId === ATTRIBUTETYPEID.NAME,
                            of(structure).pipe(
                              map((structure) => {
                                structure = this._structureChangeSetup(structure);
                                (structure as structureWithChanges).changes.name = {
                                  previousValue: change.baselineVersion.value,
                                  currentValue: change.currentVersion.value,
                                  transactionToken: change.currentVersion.transactionToken
                                }
                                return structure as structureWithChanges;
                              })
                            ),
                            iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMAXSIMULTANEITY,
                              of(structure).pipe(
                                map((structure) => {
                                  structure = this._structureChangeSetup(structure);
                                  (structure as structureWithChanges).changes.interfaceMaxSimultaneity = {
                                    previousValue: change.baselineVersion.value,
                                    currentValue: change.currentVersion.value,
                                    transactionToken: change.currentVersion.transactionToken
                                  }
                                  return structure as structureWithChanges;
                                })
                              ),
                              iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEMINSIMULTANEITY,
                                of(structure).pipe(
                                  map((structure) => {
                                    structure = this._structureChangeSetup(structure);
                                    (structure as structureWithChanges).changes.interfaceMinSimultaneity = {
                                      previousValue: change.baselineVersion.value,
                                      currentValue: change.currentVersion.value,
                                      transactionToken: change.currentVersion.transactionToken
                                    }
                                    return structure as structureWithChanges;
                                  })
                                ),
                                iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACESTRUCTURECATEGORY,
                                  of(structure).pipe(
                                    map((structure) => {
                                      structure = this._structureChangeSetup(structure);
                                      (structure as structureWithChanges).changes.interfaceStructureCategory = {
                                        previousValue: change.baselineVersion.value,
                                        currentValue: change.currentVersion.value,
                                        transactionToken: change.currentVersion.transactionToken
                                      }
                                      return structure as structureWithChanges;
                                    })
                                  ),
                                  iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACETASKFILETYPE,
                                    of(structure).pipe(
                                      map((structure) => {
                                        structure = this._structureChangeSetup(structure);
                                        (structure as structureWithChanges).changes.interfaceTaskFileType = {
                                          previousValue: change.baselineVersion.value,
                                          currentValue: change.currentVersion.value,
                                          transactionToken: change.currentVersion.transactionToken
                                        }
                                        return structure as structureWithChanges;
                                      })
                                    ),
                                    of()
                                  )
                                )
                              )
                            )
                          )
                        ),
                        of(structure) //default case
                      ))
                    )),
                    reduce((acc, curr) => [...acc, curr], [] as (structure | structureWithChanges)[])
                  ), //structures
                  iif(() => structures.map((a) => a.elements?.map((b) => b.id)).flat().includes(change.artId),
                    of(structures).pipe(
                      take(1),
                      concatMap((structures) => from(structures).pipe(
                        switchMap((structure) => iif(() => structure.elements?.map((a) => a.id).includes(change.artId),
                          of(structure).pipe(
                            concatMap((structure) => from(structure.elements).pipe(
                              switchMap((element) => iif(() => change.artId === element.id,
                                iif(() => change.itemTypeId === ATTRIBUTETYPEID.DESCRIPTION,
                                  of(element).pipe(
                                    map((el) => {
                                      el = this._elementChangeSetup(el);
                                      (el as elementWithChanges).changes.description = {
                                        previousValue: change.baselineVersion.value as string,
                                        currentValue: change.currentVersion.value as string,
                                        transactionToken: change.currentVersion.transactionToken
                                      }
                                      return el as elementWithChanges;
                                    })
                                  ),
                                  iif(() => change.itemTypeId === ATTRIBUTETYPEID.NAME,
                                    of(element).pipe(
                                      map((el) => {
                                        el = this._elementChangeSetup(el);
                                        (el as elementWithChanges).changes.name = {
                                          previousValue: change.baselineVersion.value as string,
                                          currentValue: change.currentVersion.value as string,
                                          transactionToken: change.currentVersion.transactionToken
                                        }
                                        return el as elementWithChanges;
                                      })
                                    ),
                                    iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEELEMENTALTERABLE,
                                      of(element).pipe(
                                        map((el) => {
                                          el = this._elementChangeSetup(el);
                                          (el as elementWithChanges).changes.interfaceElementAlterable = {
                                            previousValue: change.baselineVersion.value as boolean,
                                            currentValue: change.currentVersion.value as boolean,
                                            transactionToken: change.currentVersion.transactionToken
                                          }
                                          return el as elementWithChanges;
                                        })
                                      ),
                                      iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEELEMENTSTART,
                                        of(element).pipe(
                                          map((el) => {
                                            el = this._elementChangeSetup(el);
                                            (el as elementWithChanges).changes.interfaceElementIndexStart = {
                                              previousValue: change.baselineVersion.value as number,
                                              currentValue: change.currentVersion.value as number,
                                              transactionToken: change.currentVersion.transactionToken
                                            }
                                            return el as elementWithChanges;
                                          })
                                        ),
                                        iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEELEMENTEND,
                                          of(element).pipe(
                                            map((el) => {
                                              el = this._elementChangeSetup(el);
                                              (el as elementWithChanges).changes.interfaceElementIndexEnd = {
                                                previousValue: change.baselineVersion.value as number,
                                                currentValue: change.currentVersion.value as number,
                                                transactionToken: change.currentVersion.transactionToken
                                              }
                                              return el as elementWithChanges;
                                            })
                                          ),
                                          iif(() => change.itemTypeId === ATTRIBUTETYPEID.NOTES,
                                            of(element).pipe(
                                              map((el) => {
                                                el = this._elementChangeSetup(el);
                                                (el as elementWithChanges).changes.notes = {
                                                  previousValue: change.baselineVersion.value as string,
                                                  currentValue: change.currentVersion.value as string,
                                                  transactionToken: change.currentVersion.transactionToken
                                                }
                                                return el as elementWithChanges;
                                              })
                                            ),
                                            iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEENUMLITERAL,
                                            of(element).pipe(
                                              map((el) => {
                                                el = this._elementChangeSetup(el);
                                                (el as elementWithChanges).changes.enumLiteral = {
                                                  previousValue: change.baselineVersion.value as string,
                                                  currentValue: change.currentVersion.value as string,
                                                  transactionToken: change.currentVersion.transactionToken
                                                }
                                                return el as elementWithChanges;
                                              })
                                            ),
                                              of(element)
                                            )
                                          )
                                        )
                                      )
                                    )
                                  )
                                ),
                                of(element) //default case
                              ))
                            )),
                            reduce((acc, curr) => [...acc, curr], [] as (element | elementWithChanges)[]),
                            map((val) => { structure.elements = val;(structure as structureWithChanges).hasElementChanges = true; return structure;})
                          ),
                          of(structure)
                        ))
                      )),
                      reduce((acc,curr)=>[...acc,curr],[] as (structure|structureWithChanges)[])
                    ), //elements
                    iif(() => structures.map(a => a.elements.map(b => b.platformTypeId?.toString())).flat().includes(change.artId),
                      of(structures).pipe(
                        take(1),
                        concatMap((structures) => from(structures).pipe(
                          switchMap((structure) => iif(() => structure.elements.map(a => a.platformTypeId?.toString()).flat().includes(change.artId),
                            of(structure).pipe(
                              concatMap((structure) => from(structure.elements).pipe(
                                switchMap((element) => iif(() => change.artId === element.platformTypeId?.toString(),
                                  iif(() => change.itemTypeId === ATTRIBUTETYPEID.INTERFACEPLATFORMTYPEUNITS,
                                    of(element).pipe(
                                      map(el => {
                                        el = this._elementChangeSetup(el);
                                        (el as elementWithChanges).changes.units = {
                                          previousValue: change.baselineVersion.value as string,
                                          currentValue: change.currentVersion.value as string,
                                          transactionToken: change.currentVersion.transactionToken
                                        }
                                        return el as elementWithChanges;
                                      })
                                    ),
                                    iif(() => change.itemTypeId === ATTRIBUTETYPEID.NAME,
                                      of(element).pipe(
                                        map(el => {
                                          el = this._elementChangeSetup(el);
                                          (el as elementWithChanges).changes.platformTypeName2 = {
                                            previousValue: change.baselineVersion.value as string,
                                            currentValue: change.currentVersion.value as string,
                                            transactionToken: change.currentVersion.transactionToken
                                          }
                                          return el as elementWithChanges;
                                        })
                                      ),
                                      of(element))),
                                  of(element))),
                              )),
                              reduce((acc, curr) => [...acc, curr], [] as (element | elementWithChanges)[]),
                              map((val) => { structure.elements = val;(structure as structureWithChanges).hasElementChanges = true; return structure;})
                            ),
                            of(structure)
                          ))
                        ))
                      ),
                      of()
                    )
                  )
                ),
                iif(() => change.changeType.id === changeTypeNumber.RELATION_CHANGE,
                  iif(() => typeof change.itemTypeId === "object" && "id" in change.itemTypeId && change.itemTypeId.id === RelationTypeId.INTERFACESUBMESSAGECONTENT && change.artId === subMessageId,
                    iif(() => change.currentVersion.modType === ModificationType.NEW && change.baselineVersion.modType === ModificationType.NONE && structures.map((a) => a.id).includes(change.artIdB),
                      of(structures).pipe(
                        take(1),
                        concatMap((structures) => from(structures).pipe(
                          switchMap((structure) => iif(() => change.artIdB === structure.id,
                            of(structure).pipe(
                              map((struct) => {
                                struct = this._structureChangeSetup(struct);
                                (struct as structureWithChanges).added = true;
                                return struct as structureWithChanges
                              })
                            ),
                            of(structure)
                          ))
                        )),
                        reduce((acc,curr)=>[...acc,curr],[] as (structure|structureWithChanges)[])
                      ),
                      iif(() => change.currentVersion.modType === ModificationType.DELETED && change.baselineVersion.modType !== ModificationType.NONE && change.baselineVersion.modType !== ModificationType.DELETED && change.baselineVersion.modType !== ModificationType.DELETED_ON_DESTINATION,
                        this.structure.getStructure(parentBranch, messageId, subMessageId, change.artIdB, connectionId).pipe(
                          map((initialStruct) => {
                            return {
                              ...initialStruct,
                              deleted: true,
                              added:false,
                              changes: {
                                name: { previousValue: initialStruct.name, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                description: { previousValue: initialStruct.description, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                interfaceMaxSimultaneity: { previousValue: initialStruct.interfaceMaxSimultaneity, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                interfaceMinSimultaneity: { previousValue: initialStruct.interfaceMinSimultaneity, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                interfaceTaskFileType: { previousValue: initialStruct.interfaceTaskFileType, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                interfaceStructureCategory: { previousValue: initialStruct.interfaceStructureCategory, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                applicability: { previousValue: initialStruct.applicability, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                numElements:true,
                              }
                            }
                          }),
                          map((struct) => {
                            structures=[...structures,struct as structureWithChanges]
                            return structures as (Required<structure>|structureWithChanges)[];
                          })
                        ),
                        of(structures)
                      )
                    ),
                    iif(() => typeof change.itemTypeId === "object" && "id" in change.itemTypeId && change.itemTypeId.id === RelationTypeId.INTERFACESTRUCTURECONTENT && structures.map((a) => a.id).includes(change.artId),
                      iif(() => change.currentVersion.modType === ModificationType.NEW && change.baselineVersion.modType === ModificationType.NONE && structures.map((a) => a.elements?.map((b) => b.id)).flat().includes(change.artIdB),
                        of(structures).pipe(
                          take(1),
                          concatMap((structures) => from(structures).pipe(
                            switchMap((structure) => iif(() => structure.elements?.map((a) => a.id).includes(change.artIdB),
                              of(structure).pipe(
                                concatMap((structure) => from(structure.elements).pipe(
                                  switchMap((element) => iif(() => change.artIdB === element.id,
                                    of(element).pipe(
                                      map((el) => {
                                        el = this._elementChangeSetup(el);
                                        (el as elementWithChanges).changes.name = {
                                          previousValue: '',
                                          currentValue:el.name,
                                          transactionToken: change.currentVersion.transactionToken
                                        };
                                        (el as elementWithChanges).changes.description = {
                                          previousValue: '',
                                          currentValue:el.description,
                                          transactionToken: change.currentVersion.transactionToken
                                        };
                                        (el as elementWithChanges).changes.interfaceElementAlterable = {
                                          previousValue: false,
                                          currentValue:el.interfaceElementAlterable,
                                          transactionToken: change.currentVersion.transactionToken
                                        };
                                        (el as elementWithChanges).changes.interfaceElementIndexEnd = {
                                          previousValue: 0,
                                          currentValue:el.interfaceElementIndexEnd,
                                          transactionToken: change.currentVersion.transactionToken
                                        };
                                        (el as elementWithChanges).changes.interfaceElementIndexStart = {
                                          previousValue: 0,
                                          currentValue:el.interfaceElementIndexStart,
                                          transactionToken: change.currentVersion.transactionToken
                                        };
                                        (el as elementWithChanges).changes.notes = {
                                          previousValue: '',
                                          currentValue:el.notes,
                                          transactionToken: change.currentVersion.transactionToken
                                        };
                                        (el as elementWithChanges).changes.enumLiteral = {
                                          previousValue: '',
                                          currentValue:el.notes,
                                          transactionToken: change.currentVersion.transactionToken
                                        };
                                        (el as elementWithChanges).changes.platformTypeName2 = {
                                          previousValue: '',
                                          currentValue:el.platformTypeName2,
                                          transactionToken: change.currentVersion.transactionToken
                                        };
                                        (el as elementWithChanges).changes.applicability = {
                                          previousValue: change.baselineVersion.applicabilityToken as applic,
                                          currentValue: change.currentVersion.applicabilityToken as applic,
                                          transactionToken: change.currentVersion.transactionToken
                                        };
                                        (el as elementWithChanges).added = true;
                                        return el as elementWithChanges
                                      })
                                    ),
                                    of(element)
                                  ))
                                )),
                                reduce((acc, curr) => [...acc, curr], [] as (element | elementWithChanges)[]),
                                map((val) => { structure.elements = val;(structure as structureWithChanges).hasElementChanges = true; return structure;})
                              ),
                              of(structure)
                            ))
                          )),
                          reduce((acc,curr)=>[...acc,curr],[] as (structure|structureWithChanges)[])
                        ),
                        iif(() => change.currentVersion.modType === ModificationType.DELETED && change.baselineVersion.modType !== ModificationType.NONE && change.baselineVersion.modType !== ModificationType.DELETED && change.baselineVersion.modType !== ModificationType.DELETED_ON_DESTINATION,
                          of(structures).pipe(
                            take(1),
                            concatMap((structures) => from(structures).pipe(
                              concatMap((structure) => iif(() => change.artId === structure.id,
                                of(structure).pipe(
                                  concatMap((structure) => this.elements.getElement(parentBranch, messageId, subMessageId, structure.id, change.artIdB, connectionId).pipe(
                                    map((initialEl) => {
                                      return {
                                        ...initialEl,
                                        changes: {
                                          name: { previousValue: initialEl.name, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                          description: { previousValue: initialEl.description, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                          notes: { previousValue: initialEl.notes, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                          platformTypeName2: { previousValue: initialEl.platformTypeName2, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                          interfaceElementIndexEnd: { previousValue: initialEl.interfaceElementIndexEnd, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                          interfaceElementIndexStart: { previousValue: initialEl.interfaceElementIndexStart, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                          interfaceElementAlterable: { previousValue: initialEl.interfaceElementAlterable, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                          enumLiteral:{ previousValue: initialEl.enumLiteral, currentValue: '', transactionToken: change.currentVersion.transactionToken },
                                          applicability:{previousValue:initialEl.applicability,currentValue:'',transactionToken:change.currentVersion.transactionToken},
                                        },
                                        deleted: true
                                    }}),
                                  )),
                                  map((val) => {
                                    structure.elements=[...structure.elements,val]
                                    structure.numElements = structure.elements.length;
                                    structure = this._structureChangeSetup(structure);
                                    (structure as structureWithChanges).changes.numElements = true;
                                    structure.elements.sort((a, b) => Number(a.id) - Number(b.id));
                                    return structure;
                                  })
                                ),
                                of(structure)
                              ))
                            )),
                            reduce((acc, curr) => [...acc, curr], [] as (structure | structureWithChanges)[])
                          ),
                          of(change)
                        )
                      ),
                      iif(() => typeof change.itemTypeId === "object" && "id" in change.itemTypeId && change.itemTypeId.id === RelationTypeId.INTERFACEELEMENTPLATFORMTYPE && structures.map((a) => a.elements?.map((b) => b.id)).flat().includes(change.artId),
                        iif(() => change.currentVersion.modType === ModificationType.NEW && change.baselineVersion.modType === ModificationType.NONE,
                          of(structures).pipe(
                            take(1),
                            concatMap((structures) => from(structures).pipe(
                              concatMap((structure) => iif(() => structure.elements?.map((a) => a.id).includes(change.artId),
                                of(structure).pipe(
                                  concatMap((structure) => from(structure.elements).pipe(
                                    concatMap((element) => iif(() => change.artId === element.id,
                                      of(element).pipe(
                                        concatMap((val) => this.typeService.getTypeFromBranch(branchId, change.artIdB).pipe(
                                          map((type) => {
                                            element=this._elementChangeSetup(element)
                                            if ((element as elementWithChanges).changes.platformTypeName2 === undefined) {
                                              (element as elementWithChanges).changes.platformTypeName2 = {
                                                previousValue: '',
                                                currentValue: type.name,
                                                transactionToken: change.currentVersion.transactionToken
                                              }
                                            } else if ((element as elementWithChanges).changes.platformTypeName2 !== undefined && (element as elementWithChanges).changes.platformTypeName2?.currentValue !== element.platformTypeName2) {
                                              (element as elementWithChanges).changes.platformTypeName2!.currentValue = type.name;
                                              (element as elementWithChanges).changes.platformTypeName2!.transactionToken = change.currentVersion.transactionToken;
                                            }
                                            return element as elementWithChanges;
                                          })
                                        ))
                                      ),
                                      of(element)
                                    ))
                                  )),
                                  reduce((acc, curr) => [...acc, curr], [] as (element | elementWithChanges)[]),
                                  map((val) => { structure.elements = val;(structure as structureWithChanges).hasElementChanges = true; return structure;})
                                ),
                                of(structure)
                              )),
                            )),
                            reduce((acc, curr) => [...acc, curr], [] as (structure | structureWithChanges)[])
                          ),
                          iif(() => change.currentVersion.modType === ModificationType.DELETED && change.baselineVersion.modType !== ModificationType.NONE,
                          of(structures).pipe(
                            take(1),
                            concatMap((structures) => from(structures).pipe(
                              concatMap((structure) => iif(() => structure.elements?.map((a) => a.id).includes(change.artId),
                                of(structure).pipe(
                                  concatMap((structure) => from(structure.elements).pipe(
                                    concatMap((element) => iif(() => change.artId === element.id,
                                      of(element).pipe(
                                        concatMap((val) => this.typeService.getTypeFromBranch(branchId, change.artIdB).pipe(
                                          map((type) => {
                                            element = this._elementChangeSetup(element);
                                            if ((element as elementWithChanges).changes.platformTypeName2 === undefined) {
                                              (element as elementWithChanges).changes.platformTypeName2 = {
                                                previousValue: type.name,
                                                currentValue: '',
                                                transactionToken: change.currentVersion.transactionToken
                                              }
                                            } else if ((element as elementWithChanges).changes.platformTypeName2 !== undefined && (element as elementWithChanges).changes.platformTypeName2?.currentValue !== element.platformTypeName2) {
                                              (element as elementWithChanges).changes.platformTypeName2!.previousValue = type.name;
                                              (element as elementWithChanges).changes.platformTypeName2!.transactionToken = change.currentVersion.transactionToken;
                                            } else {
                                              (element as elementWithChanges).changes.platformTypeName2!.previousValue = type.name;
                                            }
                                            return element as elementWithChanges;
                                          })
                                        ))
                                      ),
                                      of(element)
                                    ))
                                  )),
                                  reduce((acc, curr) => [...acc, curr], [] as (element | elementWithChanges)[]),
                                  map((val) => { structure.elements = val;(structure as structureWithChanges).hasElementChanges = true; return structure;})
                                ),
                                of(structure)
                              )),
                            )),
                            reduce((acc, curr) => [...acc, curr], [] as (structure | structureWithChanges)[])
                            ),
                            of(structures)
                          )
                        ),
                        of(change)
                      ))
                  ),
                  of()
                )
              )
            )),
            // tap((valueToDebug) => {
            //   console.log(valueToDebug)
            // })
          )),
          switchMap((val) => of(structures as (structure | structureWithChanges)[]))
        ))
      )
    } else {
      return of(_oldStructures);
    }
    
  }
  private _elementChangeSetup(element: element | elementWithChanges): elementWithChanges{
    if ((element as elementWithChanges).changes === undefined) {
      (element as elementWithChanges).changes = {};
    }
    return element as elementWithChanges;
  }
  private _structureChangeSetup(structure: structure | structureWithChanges): structureWithChanges{
    if ((structure as structureWithChanges).changes === undefined) {
      (structure as structureWithChanges).changes = {};
    }
    return structure as structureWithChanges;
  }
  query<T=unknown>(query:MimQuery<T>) {
    return this.BranchId.pipe(
      switchMap(id => this.queryService.query(id, query).pipe(
        shareReplay({bufferSize:1,refCount:true})
      ))
    )
  }
}