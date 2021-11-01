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
import { debounceTime, distinctUntilChanged, filter, map, mergeMap, reduce, repeatWhen, share, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { PlMessagingTypesUIService } from './pl-messaging-types-ui.service';
import { TypesService } from './types.service';
import { PlatformType } from '../types/platformType'
import { MimPreferencesService } from '../../shared/services/http/mim-preferences.service';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { transaction } from 'src/app/transactions/transaction';
import { settingsDialogData } from '../../shared/types/settingsdialog';
import { applic } from '../../../../types/applicability/applic';
import { enumeration, enumerationSet } from '../../shared/types/enum';
import { ApplicabilityListUIService } from '../../shared/services/ui/applicability-list-ui.service';
import { EnumerationUIService } from '../../shared/services/ui/enumeration-ui.service';
import { PreferencesUIService } from '../../shared/services/ui/preferences-ui.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentTypesService {

  private _typeData: Observable<PlatformType[]> = this.uiService.filter.pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    switchMap(x => this.typesService.getFilteredTypes(x,this.uiService.BranchId.getValue()).pipe(
      repeatWhen(_ => this.uiService.typeUpdateRequired),
      share(),
      tap((y) => {
        //this.uiService.updateTypes = false;
        if (y.length <= this.uiService.columnCount.getValue()) {
          this.uiService.singleLineAdjustmentNumber = 30;
        } else {
          this.uiService.singleLineAdjustmentNumber = 0;
        }
      })
    )),
  )

  constructor(private typesService: TypesService, private uiService: PlMessagingTypesUIService, private preferenceService: PreferencesUIService,private userService: UserDataAccountService, private applicabilityService: ApplicabilityListUIService, private enumSetService: EnumerationUIService) { }

  /**
   * Returns a list of platform types based on current branch and filter conditions(debounced).
   * Sets the "single line adjustment" which is used to offset platform type cards in the grid when there is only one line of platform types
   * Also updates when insertions are done via API.
   * @returns @type {Observable<PlatformType[]>} list of platform types
   */
  get typeData() {
    return this._typeData;
  }

  get applic(){
    return this.applicabilityService.applic;
  }

  get enumSets() {
    return this.enumSetService.enumSets;
  }

  /**
   * Updates the attributes of a platform type using the platform types PATCH API and current branch, id is required
   * @param body @type {Partial<PlatformType>} attributes to update + id of platform type
   * @returns @type {Observable<TypesApiResponse>} observable containing results (see @type {TypesApiResponse} and @type {Observable})
   */
  partialUpdate(body: Partial<PlatformType>) {
    return this.typesService.changePlatformType(this.uiService.BranchId.getValue(), body).pipe(
      take(1),
      switchMap((transaction) => this.typesService.performMutation(transaction, this.uiService.BranchId.getValue()).pipe(
        tap(() => {
          this.uiService.updateTypes = true;
        })
      ))
    )
  }

  copyType(body: PlatformType | Partial<PlatformType>) {
    delete body.id;
    return this.typesService.createPlatformType(this.uiService.BranchId.getValue(), body, []).pipe(
      take(1),
      switchMap((transaction) => this.typesService.performMutation(transaction, this.uiService.BranchId.getValue()).pipe(
        tap(() => {
          this.uiService.updateTypes = true;
        })
      ))
    )
  }
  /**
   * Creates a new platform type using the platform types POST API, current branch,but without the id,idIntValue, and idString present and includes enum values
   * @todo fix this up later to be in enumeration-ui.service
   * @param body @type {PlatformType} platform type to create
   * @returns @type {Observable<TypesApiResponse>} observable containing results (see @type {TypesApiResponse} and @type {Observable})
   */
  createType(body: PlatformType|Partial<PlatformType>,isNewEnumSet:boolean,enumSetData:{ enumSetId:string,enumSetName: string, enumSetDescription: string, enumSetApplicability: applic, enums: enumeration[] }) {
    delete body.id;
    return iif(()=>body.interfaceLogicalType==='enumeration',iif(() => isNewEnumSet, this.typesService.createPlatformType(this.uiService.BranchId.getValue(), body, []).pipe(
      take(1),
      switchMap((platformTypeCreationTransaction) => this.enumSetService.createEnumSetToPlatformTypeRelation(body.name).pipe(
        take(1),
        switchMap((relationPlatform) => this.enumSetService.createEnumSet(this.uiService.BranchId.getValue(), { name: enumSetData.enumSetName, description: enumSetData.enumSetDescription, applicability: enumSetData.enumSetApplicability, applicabilityId: enumSetData.enumSetApplicability.id }, [relationPlatform], platformTypeCreationTransaction).pipe(
          take(1),
          switchMap((enumSetTransaction) => of(enumSetTransaction).pipe(
            mergeMap((temp) => from(enumSetData.enums).pipe(
              mergeMap((enumValue) => this.enumSetService.createEnumToEnumSetRelation(enumSetData.enumSetName).pipe(
                switchMap((relationEnum) => this.fixEnum(enumValue).pipe(
                  switchMap((enumeration)=>this.enumSetService.createEnum(this.uiService.BranchId.getValue(),enumValue,[relationEnum]))
                ))
              ))
            )),
            reduce((acc, curr) => [...acc, curr], [] as transaction[]),
            switchMap((enumTransactions) => this.mergeEnumArray(enumTransactions).pipe(
              take(1),
              switchMap((enumTransaction)=>this.mergeEnumTransactionWithPlatformType(enumSetTransaction,enumTransaction))
            ))
          ))
        ))
      ))
    ), this.enumSetService.createPlatformTypeToEnumSetRelation(enumSetData.enumSetId).pipe(
      take(1),
      switchMap((relation)=>this.typesService.createPlatformType(this.uiService.BranchId.getValue(),body,[relation]))
    )
    ),this.typesService.createPlatformType(this.uiService.BranchId.getValue(),body,[]))
    .pipe(
      switchMap((transaction) => this.typesService.performMutation(transaction, this.uiService.BranchId.getValue()).pipe(
        tap(() => {
          this.uiService.updateTypes = true;
        })
      )),
    )
  }
  private mergeEnumArray(transactions: transaction[]) {
    let currentTransaction:transaction = {
      branch: '',
      txComment: '',
      createArtifacts: [],
    };
    if (transactions?.[0]) {
      currentTransaction = transactions.shift() ||
      {
        branch: '',
        txComment: '',
        createArtifacts: [],
      };
    }
    transactions.forEach((transaction) => {
      currentTransaction.createArtifacts?.push(...transaction?.createArtifacts||[])
    })
    return of<transaction>(currentTransaction);
  }
  private mergeEnumTransactionWithPlatformType(transactionA: transaction, transactionB: transaction) {
    transactionA.createArtifacts?.push(...transactionB.createArtifacts||[])
    return of<transaction>(transactionA);
  }

  private fixEnum(enumeration:enumeration) {
    enumeration.applicabilityId = enumeration.applicability.id;
    return of<enumeration>(enumeration);
  }

  get logicalTypes() {
    return this.typesService.logicalTypes;
  }

  getLogicalTypeFormDetail(id: string) {
    return this.typesService.getLogicalTypeFormDetail(id);
  }

  public get preferences() {
    return this.preferenceService.preferences;
  }
  public get inEditMode() {
    return this.preferenceService.inEditMode;
  }
  public get BranchPrefs() {
    return this.preferenceService.BranchPrefs;
  }

  updatePreferences(preferences: settingsDialogData) {
    return this.createUserPreferenceBranchTransaction(preferences.editable).pipe(
      take(1),
      switchMap((transaction) => this.typesService.performMutation(transaction,this.uiService.BranchId.getValue()).pipe(
        take(1),
        tap(() => {
          this.uiService.updateTypes = true;
        })
      )
      )
    )
  }

  private createUserPreferenceBranchTransaction(editMode:boolean) {
    return combineLatest(this.preferences, this.uiService.BranchId, this.BranchPrefs).pipe(
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
  getEnumSet(platformTypeId: string) {
    return this.enumSetService.getEnumSet(platformTypeId);
  }
  changeEnumSet(changes:enumerationSet) {
    return this.enumSetService.changeEnumSet(changes);
  }
}
