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
import { from } from 'rxjs';
import { map, reduce, switchMap, tap } from 'rxjs/operators';
import { CurrentTypesService } from '../../services/current-types.service';
import { editPlatformTypeDialogData } from '../../types/editPlatformTypeDialogData';
import { logicalType } from '../../../shared/types/logicaltype';

@Component({
  selector: 'app-edit-type-dialog',
  templateUrl: './edit-type-dialog.component.html',
  styleUrls: ['./edit-type-dialog.component.sass']
})
export class EditTypeDialogComponent implements OnInit {

  platform_type: string = "";
  logicalTypes = this.typesService.logicalTypes.pipe(
    switchMap((logicalTypes) => from(logicalTypes).pipe(
      map((type) => { let name = type.name.replace("nsigned ", "");if(name!==type.name){name =name.charAt(0)+name.charAt(1).toUpperCase()+name.slice(2)} type.name = name; return type; })
    )),
    reduce((acc, curr) => [...acc, curr], [] as logicalType[]),
  );
  units = this.typesService.units;
  constructor(public dialogRef: MatDialogRef<EditTypeDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: editPlatformTypeDialogData, private typesService: CurrentTypesService) {
    this.platform_type = this.data.type.name;
   }

  ngOnInit(): void {
  }

  /**
   * Calculates MSB value based on Bit Resolution, Byte Size and whether or not the type is signed/unsigned
   * @returns @type {number} MSB Value
   */
  getMSBValue(): number{
    return Number(this.data.type.interfacePlatformTypeBitsResolution)*(2^(((Number(this.data.type.interfacePlatformTypeBitSize)*8)-Number(this.data.type.interfacePlatform2sComplement))/2));
  }

  /**
   * Calculates Resolution based on MSB value, Byte Size and whether or not the type is signed/unsigned
   * @returns @type {number} Resolution
   */
  getResolution(): number{
    return (Number(this.data.type.interfacePlatformTypeMsbValue) * 2) / (2 ^ ((Number(this.data.type.interfacePlatformTypeBitSize)*8) - Number(this.data.type.interfacePlatform2sComplement)));
  }

  /**
   * Returns the bit size which is 8 * byte size
   */
  get byte_size() {
    return Number(this.data.type.interfacePlatformTypeBitSize) /8;
  }

  /**
   * Sets the byte size based on bit size /8
   */
  set byte_size(value: number) {
    this.data.type.interfacePlatformTypeBitSize = String(value * 8);
  }

  /**
   * Forcefully closes dialog without returning data
   */
  onNoClick(): void {
    this.dialogRef.close();
  }

}
