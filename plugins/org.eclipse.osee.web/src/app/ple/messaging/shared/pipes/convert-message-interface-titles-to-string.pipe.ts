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
import { Pipe, PipeTransform } from '@angular/core';

const transformMatrix:any = {
  name: "Name",
  interfaceStructureCategory: "Category",
  TxRate: "Tx Rate",
  interfaceMinSimultaneity: "Min Simult.",
  interfaceMaxSimultaneity: "Max Simult.",
  numElements: "Num. Elements",
  sizeInBytes: "Size(B)",
  bytesPerSecondMinimum: "Min BPS",
  bytesPerSecondMaximum: "Max BPS",
  interfaceTaskFileType: "Task File Type",
  description: "Description",
  GenerationIndicator: "Indicator",
  ElementName: "Element Name",
  beginWord: "Begin Word",
  endWord: "End Word",
  beginByte: "Begin Byte",
  endByte: "End Byte",
  Sequence: "Seq",
  Units: "Units",
  MinValue: "Min",
  MaxValue: "Max",
  interfaceElementAlterable: "Alterable",
  EnumsLiteralsDesc: "Enum Lit. Desc.",
  notes: "Notes",
  DefaultValue: "Default Value",
  isArray: "Is An Array",
  platformTypeName2: "Type",
  applicability: "Applicability",
  logicalType: "Logical Type",
  interfacePlatformTypeDefaultValue: "Default",
  interfacePlatformTypeMaxval: "Max",
  interfacePlatformTypeMinval: "Min",
  interfaceElementIndexEnd: "End Index",
  interfaceElementIndexStart:"Start Index"
}

@Pipe({
  name: 'convertMessageInterfaceTitlesToString'
})
export class ConvertMessageInterfaceTitlesToStringPipe implements PipeTransform {

  transform(value: string, ...args: unknown[]): string {
    if (transformMatrix[value] != null && transformMatrix[value] != undefined) {
      return transformMatrix[value]
    }
    return value;
  }

}
