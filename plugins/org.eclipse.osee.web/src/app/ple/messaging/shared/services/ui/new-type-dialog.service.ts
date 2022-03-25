/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { iif, of, from } from 'rxjs';
import { take, switchMap, mergeMap, reduce, tap } from 'rxjs/operators';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { transaction } from '../../../../../transactions/transaction';
import { applic } from '../../../../../types/applicability/applic';
import { enumeration } from '../../types/enum';
import { PlatformType } from '../../types/platformType';
import { EnumsService } from '../http/enums.service';
import { TypesService } from '../http/types.service';
import { ApplicabilityListUIService } from './applicability-list-ui.service';
import { EnumerationUIService } from './enumeration-ui.service';

@Injectable({
  providedIn: 'root'
})
export class NewTypeDialogService {

  constructor (private typesService: TypesService,
    private constantEnumService: EnumsService,
    private applicabilityService: ApplicabilityListUIService, private enumSetService: EnumerationUIService,private uiService:UiService) { }
  
  createType(body: PlatformType|Partial<PlatformType>,isNewEnumSet:boolean,enumSetData:{ enumSetId:string,enumSetName: string, enumSetDescription: string, enumSetApplicability: applic, enums: enumeration[] }) {
    delete body.id;
    return iif(()=>body.interfaceLogicalType==='enumeration',iif(() => isNewEnumSet, this.typesService.createPlatformType(this.uiService.id.getValue(), body, []).pipe(
      take(1),
      switchMap((platformTypeCreationTransaction) => this.enumSetService.createEnumSetToPlatformTypeRelation(body.name).pipe(
        take(1),
        switchMap((relationPlatform) => this.enumSetService.createEnumSet(this.uiService.id.getValue(), { name: enumSetData.enumSetName, description: enumSetData.enumSetDescription, applicability: enumSetData.enumSetApplicability, applicabilityId: enumSetData.enumSetApplicability.id }, [relationPlatform], platformTypeCreationTransaction).pipe(
          take(1),
          switchMap((enumSetTransaction) => of(enumSetTransaction).pipe(
            mergeMap((temp) => from(enumSetData.enums).pipe(
              mergeMap((enumValue) => this.enumSetService.createEnumToEnumSetRelation(enumSetData.enumSetName).pipe(
                switchMap((relationEnum) => this.fixEnum(enumValue).pipe(
                  switchMap((enumeration)=>this.enumSetService.createEnum(this.uiService.id.getValue(),enumValue,[relationEnum]))
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
      switchMap((relation)=>this.typesService.createPlatformType(this.uiService.id.getValue(),body,[relation]))
    )
    ),this.typesService.createPlatformType(this.uiService.id.getValue(),body,[]))
    .pipe(
      switchMap((transaction) => this.typesService.performMutation(transaction)),
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
}
