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
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { share, take } from 'rxjs/operators';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { editConfiguration } from '../../types/pl-config-configurations';
import { response } from '../../types/pl-config-responses';
import { PLAddConfigData, PLEditConfigData } from '../../types/pl-edit-config-data';
import { AddConfigurationDialogComponent } from '../add-configuration-dialog/add-configuration-dialog.component';
import { CopyConfigurationDialogComponent } from '../copy-configuration-dialog/copy-configuration-dialog.component';
import { EditConfigurationDialogComponent } from '../edit-config-dialog/edit-config-dialog.component';

@Component({
  selector: 'plconfig-configuration-dropdown',
  templateUrl: './configuration-dropdown.component.html',
  styleUrls: ['./configuration-dropdown.component.sass']
})
export class ConfigurationDropdownComponent implements OnInit {
  selectedBranch: Observable<string> = this.uiStateService.branchId;
  branchApplicability = this.currentBranchService.branchApplicability.pipe(share());
  branchId: string | undefined;
  constructor(private uiStateService: PlConfigUIStateService, private branchService: PlConfigBranchService, private currentBranchService: PlConfigCurrentBranchService, public dialog: MatDialog) { 
    this.selectedBranch.subscribe((val) => {
      this.branchId = val;
    });
    this.branchApplicability.subscribe(() => {
    });
  }

  ngOnInit(): void {
  }

  deleteConfig(config: {id:string , name:string}) {
    this.currentBranchService.deleteConfiguration(config.id).pipe(take(1)).subscribe((response: response[]) => {
      this.uiStateService.deleteReqConfig = config.name;
    })
  }

  openEditDialog(config: { id: string, name: string, hasFeatureApplicabilities: boolean },productApplicabilities?: string[]) {
    let dialogData = new PLEditConfigData(this.branchId, config,undefined,productApplicabilities,true);
    const dialogRef = this.dialog.open(EditConfigurationDialogComponent, {
      data: dialogData,
      minWidth: '60%'
    });
    dialogRef.afterClosed().subscribe((result: PLEditConfigData) => {
      if (result) {
        let apiRequest;
        let body: editConfiguration = {
          ...result.currentConfig,
          copyFrom: result.copyFrom.id && result.copyFrom.id || '',
          configurationGroup: result.group.id && result.group.id || '',
          productApplicabilities:result.productApplicabilities||[]
        };
        apiRequest = this.currentBranchService.editConfigurationDetails(body);
        apiRequest.pipe(take(1)).subscribe((response:response) => {
        }); 
      }
    });
  }

  addConfiguration() {
    let dialogData: PLAddConfigData = {
      currentBranch: this.branchId?.toString(),
      copyFrom: { id: '0', name: '', hasFeatureApplicabilities:false, productApplicabilities:[] },
      title: '',
      group: { id: '0', name: ''},
      productApplicabilities:[],
    }
    const dialogRef = this.dialog.open(AddConfigurationDialogComponent, {
      data: dialogData,
      minWidth: '60%'
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.currentBranchService.addConfiguration({ name: result.title, copyFrom: result.copyFrom.id, configurationGroup: result.group.id, productApplicabilities:result.productApplicabilities }).pipe(take(1)).subscribe((response: response) => {
        });
      }
    })
  }
  copyConfiguration() {
    const dialogRef = this.dialog.open(CopyConfigurationDialogComponent, {
      data: {
        currentConfig: {id:'',name:''},
        currentBranch: this.branchId
      },
      minWidth: '60%'
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        let body = {
          ...result.currentConfig,
          copyFrom: result.ConfigurationToCopyFrom.id && result.ConfigurationToCopyFrom.id || '',
          configurationGroup: result.group && result.group || ''
        };
        this.currentBranchService.editConfigurationDetails(body).pipe(take(1)).subscribe((response:response) => {
        }); 
      }
    })
  }
}
