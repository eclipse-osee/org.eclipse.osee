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
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { EnumsService } from '../../../shared/services/http/enums.service';
import { andBitSizeQuery, andDefaultValQuery, andLogicalTypeQuery, andMaxValQuery, andMinValQuery, andMsbValQuery, andNameQuery, andQuery, andUnitQuery, PlatformTypeQuery } from '../../../shared/types/MimQuery';
import { PlatformType } from '../../../shared/types/platformType';

@Component({
  selector: 'app-platform-type-query',
  templateUrl: './platform-type-query.component.html',
  styleUrls: ['./platform-type-query.component.sass']
})
export class PlatformTypeQueryComponent implements OnInit {

  @Input() platformTypes: PlatformType[] = [];
  @Input() bitSizeSliderStepSize: number = 0.05;
  units = this.constantEnumService.units;
  unit = "";
  logicalType = "";
  defaultVal = ""
  maximumValue = ""
  minimumValue = ""
  msbValue = ""
  bitSize = 0;
  name = "";
  @Output('query') returnQuery = new EventEmitter<PlatformTypeQuery>();
  constructor (private constantEnumService: EnumsService) { 
  }

  ngOnInit(): void {
  }

  get logicalTypes() {
    return this.platformTypes.map(type => type.interfaceLogicalType).filter((v, i, a) => a.indexOf(v) === i);
  }
  get bitSizes() {
    return this.platformTypes.map(type => Number(type.interfacePlatformTypeBitSize)).filter((v, i, a) => a.indexOf(v) === i);
  }
  get maxBitSize() {
    return Math.max(...this.bitSizes)/8;
  }

  get minBitSize() {
    return Math.min(...this.bitSizes)/8;
  }
  get defaultValues() {
    return this.platformTypes.map(type => type.interfacePlatformTypeDefaultValue).filter((v, i, a) => a.indexOf(v) === i);
  }

  get maxValues() {
    return this.platformTypes.map(type => type.interfacePlatformTypeMaxval).filter((v, i, a) => a.indexOf(v) === i);
  }

  get minValues() {
    return this.platformTypes.map(type => type.interfacePlatformTypeMinval).filter((v, i, a) => a.indexOf(v) === i);
  }

  get msbValues() {
    return this.platformTypes.map(type => type.interfacePlatformTypeMsbValue).filter((v, i, a) => a.indexOf(v) === i);
  }

  bitSizeDisplay(value: number) {
    return value.toPrecision(4)
  }

  get enumerations() {
    return this.platformTypes.filter(type=>type.interfaceLogicalType.toLowerCase()==='enumeration').map(type=>type.name).filter((v, i, a) => a.indexOf(v) === i);
  }

  get query() {
    const queries: andQuery[] = [];
    if (this.unit !== "") queries.push(new andUnitQuery(this.unit));
    if (this.bitSize !== 0) queries.push(new andBitSizeQuery(this.bitSize.toString()));
    if (this.logicalType!== "") queries.push(new andLogicalTypeQuery(this.logicalType));
    if (this.minimumValue !== "") queries.push(new andMinValQuery(this.minimumValue));
    if (this.maximumValue !== "") queries.push(new andMaxValQuery(this.maximumValue));
    if (this.defaultVal !== "") queries.push(new andDefaultValQuery(this.defaultVal));
    if (this.msbValue !== "") queries.push(new andMsbValQuery(this.msbValue));
    if (this.name !== "") queries.push(new andNameQuery(this.name));
    return new PlatformTypeQuery(undefined,queries)
  }
  queryTypes() {
    this.returnQuery.emit(this.query);
  }
}
