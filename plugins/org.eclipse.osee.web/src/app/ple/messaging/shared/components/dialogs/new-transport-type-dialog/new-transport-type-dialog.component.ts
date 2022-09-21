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
import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { transportType } from '../../../types/transportType';

@Component({
  selector: 'osee-new-transport-type-dialog',
  templateUrl: './new-transport-type-dialog.component.html',
  styleUrls: ['./new-transport-type-dialog.component.sass']
})
export class NewTransportTypeDialogComponent implements OnInit {

  transportType:transportType={
    name: '',
    byteAlignValidation: false,
    messageGeneration: false,
    byteAlignValidationSize: 0,
    messageGenerationType: '',
    messageGenerationPosition: ''
  }
  generationTypes = ['None', 'Dynamic', 'Relational', 'Static']
  validation = this.transportType.byteAlignValidation ? this.transportType.byteAlignValidationSize!==0 : true;

  constructor(public dialogRef: MatDialogRef<NewTransportTypeDialogComponent>) { }

  ngOnInit(): void {
  }

  onNoClick(): void {
    console.log(this.validation)
    this.dialogRef.close();
  }
}
