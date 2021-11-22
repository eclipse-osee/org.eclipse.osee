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
 import { iif, Observable, of, Subject } from 'rxjs';
 import { filter, map, switchMap, tap } from 'rxjs/operators';
import { SideNavService } from 'src/app/shared-services/ui/side-nav.service';
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
 
   contents = this.sideNavService.sideNavContent.pipe(
     switchMap((content) => of(content).pipe(
       switchMap((val) => iif(()=>val.transaction?.id!==undefined && val.transaction.id!=='-1',this.transactionService.getTransaction(val?.transaction?.id || '-1'),of(undefined)).pipe(
        switchMap((tx)=>of({navInfo:val,transaction:tx}))
      ))
     )),
   );
 
   transactionInfo!: Observable<transactionInfo>;
   opened = this.sideNavService.opened;
   constructor (private transactionService: TransactionService, private sideNavService: SideNavService) {}
 
   ngOnInit(): void {
   }
 
   isApplic(value:string | number | boolean | applic|undefined): value is applic{
     return typeof value==='object'
   }

   viewDiff(open:boolean,value:string|number|applic|transportType, header:string) {
    this.sideNavService.sideNav = { opened: open,field:header, currentValue: value };
  }
 }