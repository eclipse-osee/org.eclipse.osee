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
 import { Component, Input, OnInit } from '@angular/core';
 import { Observable, of } from 'rxjs';
 import { filter, map, switchMap, tap } from 'rxjs/operators';
 import { TransactionService } from 'src/app/transactions/transaction.service';
 import { transactionInfo } from 'src/app/types/change-report/transaction';
 import { transactionToken } from 'src/app/types/change-report/transaction-token';
 import { applic } from '../../../types/applicability/applic';
 import { transportType } from '../../messaging/shared/types/connection';
 
 @Component({
   selector: 'app-mim-single-diff',
   templateUrl: './mim-single-diff.component.html',
   styleUrls: ['./mim-single-diff.component.sass']
 })
 export class MimSingleDiffComponent implements OnInit {
 
   @Input() contents: Partial<{opened:boolean,field:string,currentValue:string|number|applic|transportType,previousValue?:string|number|applic|transportType,transaction?:transactionToken,user?:string,date?:string}> = {};
 
   @Input() transaction: Observable<transactionToken|undefined> = of({id:"-1",branchId:"2"});
   transactionInfo!: Observable<transactionInfo>;
   constructor (private transactionService: TransactionService) {}
 
   ngOnInit(): void {
     this.transactionInfo = this.transactionService.getTransaction(this.contents.transaction?.id || '-1').pipe();
   }
 
   isApplic(value:string|number|applic|null|undefined): value is applic{
     return typeof value==='object'
   }
 
 }