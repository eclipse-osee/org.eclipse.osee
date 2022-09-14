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
import { iif, of, from, combineLatest } from 'rxjs';
import { share, switchMap, repeatWhen, shareReplay, take, tap, mergeMap, reduce, concatMap, map, filter } from 'rxjs/operators';
import { transaction } from 'src/app/transactions/transaction';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { applic } from '../../../../../types/applicability/applic';
import { enumeration, enumerationSet } from '../../types/enum';
import { enumeratedPlatformType } from '../../types/enumeratedPlatformType';
import { PlatformType } from '../../types/platformType';
import { TypesService } from '../http/types.service';
import { EnumerationUIService } from './enumeration-ui.service';

@Injectable({
  providedIn: 'root'
})
export class TypesUIService {

  private _types = this._ui.id.pipe(
    filter(id=>id!==''),
    share(),
    switchMap(x => this._typesService.getTypes(x).pipe(
      repeatWhen(_ => this._ui.update),
      share(),
    )),
    shareReplay({ bufferSize: 1, refCount: true }),
  )
  constructor (private _ui: UiService, private _typesService: TypesService, private _enumSetService: EnumerationUIService,) { }
  get types() {
    return this._types;
  }
  getType( typeId: string) {
    return this._ui.id.pipe(
      take(1),
      filter(id=>id!==''),
      share(),
      switchMap(branch => this._typesService.getType(branch, typeId).pipe(
        share()
      )),
      shareReplay({ bufferSize: 1, refCount: true })
    )
  }

  getTypeFromBranch(branchId: string, typeId: string) {
    return this._typesService.getType(branchId,typeId)
  }
  changeType(type:Partial<PlatformType>) {
    return this._ui.id.pipe(
      take(1),
      filter(id=>id!==''),
      switchMap((branchId)=>this._typesService.changePlatformType(branchId,type))
    )
  }
  performMutation(body: transaction) {
    return this._ui.id.pipe(
      take(1),
      filter(id=>id!==''),
    switchMap((branchId)=>this._typesService.performMutation(body))
    )
  }

  createType(body: PlatformType|Partial<PlatformType>,isNewEnumSet:boolean,enumSetData:{ enumSetId:string,enumSetName: string, enumSetDescription: string, enumSetApplicability: applic, enums: enumeration[] }) {
    delete body.id;
    return iif(()=>body.interfaceLogicalType==='enumeration',iif(() => isNewEnumSet, this._typesService.createPlatformType(this._ui.id.getValue(), body, []).pipe(
      take(1),
      switchMap((platformTypeCreationTransaction) => this._enumSetService.createEnumSetToPlatformTypeRelation(body.name).pipe(
        take(1),
        switchMap((relationPlatform) => this._enumSetService.createEnumSet(this._ui.id.getValue(), { name: enumSetData.enumSetName, description: enumSetData.enumSetDescription, applicability: enumSetData.enumSetApplicability, applicabilityId: enumSetData.enumSetApplicability.id }, [relationPlatform], platformTypeCreationTransaction).pipe(
          take(1),
          switchMap((enumSetTransaction) => of(enumSetTransaction).pipe(
            mergeMap((temp) => from(enumSetData.enums).pipe(
              mergeMap((enumValue) => this._enumSetService.createEnumToEnumSetRelation(enumSetData.enumSetName).pipe(
                switchMap((relationEnum) => this.fixEnum(enumValue).pipe(
                  switchMap((enumeration)=>this._enumSetService.createEnum(this._ui.id.getValue(),enumValue,[relationEnum]))
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
    ), this._enumSetService.createPlatformTypeToEnumSetRelation(enumSetData.enumSetId).pipe(
      take(1),
      switchMap((relation)=>this._typesService.createPlatformType(this._ui.id.getValue(),body,[relation]))
    )
    ),this._typesService.createPlatformType(this._ui.id.getValue(),body,[]))
    .pipe(
      switchMap((transaction) => this._typesService.performMutation(transaction).pipe(
        tap(_=>this._ui.updated=true)
      )),
    )
  }
  private fixEnum(enumeration:enumeration) {
    enumeration.applicabilityId = enumeration.applicability.id;
    return of<enumeration>(enumeration);
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
  partialUpdate(body: Partial<PlatformType>) {
    return this._typesService.changePlatformType(this._ui.id.getValue(), body).pipe(
      take(1),
      switchMap((transaction) => this._typesService.performMutation(transaction).pipe(
        tap(() => {
          this._ui.updated = true;
        })
      ))
    )
  }

  copyType<T extends PlatformType | Partial<PlatformType>>(body: T) {
    this.removeId(body);
    delete body.enumSet;
    return iif(()=>body.interfaceLogicalType==='enumeration' && 'enumerationSet' in body ,this.copyEnumeratedType(body as enumeratedPlatformType),this._typesService.createPlatformType(this._ui.id.getValue(), body, []).pipe(
        take(1),
        switchMap((transaction) => this._typesService.performMutation(transaction).pipe(
          tap(() => {
            this._ui.updated = true;
          })
        ))
    ))
  }

  copyEnumeratedType(body: enumeratedPlatformType) {
    const { enumerationSet, ...type } = body;
    return this._ui.id.pipe(
      take(1),
      filter(id=>id!==''),
      switchMap(branchId => this._enumSetService.createPlatformTypeToEnumSetRelation(body.enumerationSet.name).pipe(
        take(1),
        switchMap(relation => this._typesService.createPlatformType(branchId, type, [relation]).pipe(
          take(1),
          switchMap(platformTransaction => this.createEnumSet(enumerationSet).pipe(
            map(_=>platformTransaction)
          ))
        ))
      )),
      switchMap(transaction => this._typesService.performMutation(transaction).pipe(
        tap(() => {
          this._ui.updated = true;
        })
      ))
    )
  }
  createEnums(set: enumerationSet) {
    return this._ui.id.pipe(
      filter(id=>id!==''),
      switchMap(id => of(set).pipe(
        take(1),
        concatMap(enumSet => from(enumSet.enumerations || []).pipe(
          switchMap(enumeration => this.fixEnum(enumeration)),
          switchMap(enumeration => this._enumSetService.createEnumToEnumSetRelation(set.name).pipe(
            switchMap(relation=>this._enumSetService.createEnum(id,enumeration,[relation]))
          )),
        )),
        reduce((acc, curr) => [...acc, curr], [] as transaction[]),
        switchMap((enumTransactions) => this.mergeEnumArray(enumTransactions)),
      ))
    )
  }

  createEnumSet(set: enumerationSet) {
    const { enumerations, ...body } = set;
    return this._ui.id.pipe(
      filter(id=>id!==''),
      switchMap(id => this._enumSetService.createEnumSet(id, body, []).pipe(
        switchMap(enumSetTransaction => this.createEnums(set).pipe(
          switchMap(enumTransaction=>this.mergeEnumArray([enumSetTransaction,enumTransaction]))
        ))
      )),
      switchMap(enumTransaction=>this._typesService.performMutation(enumTransaction))
    )
  }

  /**
   * recursively remove the id property from an object using JSON.parse(JSON.stringify())
   * typically used before doing a creation rest call
   * @param object 
   */
  private removeId(object: Object) {
    JSON.parse(JSON.stringify(object,(k,v)=>(k === 'id')?undefined:v))
  }
}
