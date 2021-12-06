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
import { DialogService } from '../../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../../services/pl-config-uistate.service';
import { view, viewWithChanges } from '../../../types/pl-config-applicui-branch-mapping';

@Component({
  selector: 'plconfig-config-menu',
  templateUrl: './config-menu.component.html',
  styleUrls: ['./config-menu.component.sass']
})
export class ConfigMenuComponent implements OnInit {
  _editable = this.uiStateService.editable;
  @Input() config:view|viewWithChanges={name:'',hasFeatureApplicabilities:false,id:''}
  constructor(private dialogService: DialogService,private router: Router, private route: ActivatedRoute,private uiStateService: PlConfigUIStateService, private currentBranchService: PlConfigCurrentBranchService) { }

  ngOnInit(): void {
  }

  openConfigMenu(header: string, editable: string) {
    this.dialogService.openConfigMenu(header, editable).subscribe();
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
  hasViewChanges(value: view | viewWithChanges): value is viewWithChanges{
    return (value as viewWithChanges).changes !== undefined;
  }
}
