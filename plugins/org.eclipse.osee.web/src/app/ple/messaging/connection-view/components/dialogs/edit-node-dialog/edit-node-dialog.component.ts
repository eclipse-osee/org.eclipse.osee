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
import { applic } from 'src/app/types/applicability/applic';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { node, nodeData } from '../../../../shared/types/node';

@Component({
  selector: 'app-edit-node-dialog',
  templateUrl: './edit-node-dialog.component.html',
  styleUrls: ['./edit-node-dialog.component.sass']
})
export class EditNodeDialogComponent implements OnInit {

  title: string = "";
  applics = this.graphService.applic;
  constructor (public dialogRef: MatDialogRef<EditNodeDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: nodeData, private graphService: CurrentGraphService) {
    this.title = data.name;
   }

  ngOnInit(): void {
  }

  onNoClick() {
    this.dialogRef.close();
  }

  compareApplics(o1:applic,o2:applic) {
    return o1?.id === o2?.id && o1?.name === o2?.name;
  }
}
