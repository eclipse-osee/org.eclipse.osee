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
import { Router, ActivatedRoute } from '@angular/router';
import { transportType } from 'src/app/ple/messaging/shared/types/connection';
import { applic } from 'src/app/types/applicability/applic';
import { difference } from 'src/app/types/change-report/change-report';
import { PlConfigCurrentBranchService } from '../../../services/pl-config-current-branch.service';
import { ExtendedNameValuePair, ExtendedNameValuePairWithChanges } from '../../../types/base-types/ExtendedNameValuePair';

@Component({
  selector: 'plconfig-value-menu',
  templateUrl: './value-menu.component.html',
  styleUrls: ['./value-menu.component.sass']
})
export class ValueMenuComponent implements OnInit {

  @Input() value: (ExtendedNameValuePair|ExtendedNameValuePairWithChanges)={id:'',name:'',value:'',values:[]}

  constructor(private currentBranchService:PlConfigCurrentBranchService, private router:Router, private route:ActivatedRoute) { }

  ngOnInit(): void {
  }
  hasChanges(value: ExtendedNameValuePair | ExtendedNameValuePairWithChanges): value is ExtendedNameValuePairWithChanges {
    return (value as ExtendedNameValuePairWithChanges).changes!==undefined
  }

  viewDiff(open: boolean, value: difference, header: string) {
    let current = value.currentValue as string | number | applic | transportType;
    let prev = value.previousValue as string | number | applic | transportType;
    if (prev === null) {
      prev = ''
    }
    if (current === null) {
      current = ''
    }
    this.currentBranchService.sideNav = { opened: open, field: header, currentValue: current, previousValue: prev, transaction: value.transactionToken };
    this.router.navigate([{ outlets: { rightSideNav: ['diffOpen'] } }], {
      relativeTo: this.route.parent,
      queryParamsHandling: 'merge',
      skipLocationChange:true
    });
  }
}
