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
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { view } from '../../types/pl-config-applicui-branch-mapping';
import { CfgGroupDialog } from '../../types/pl-config-cfggroups';

@Component({
  selector: 'app-config-group-dialog',
  templateUrl: './config-group-dialog.component.html',
  styleUrls: ['./config-group-dialog.component.sass']
})
export class ConfigGroupDialogComponent implements OnInit {
  totalConfigurations: view[] = [];
  constructor(public dialogRef: MatDialogRef<ConfigGroupDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: CfgGroupDialog) {
    this.totalConfigurations = data.configGroup.views;
     }

  ngOnInit(): void {
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
}
