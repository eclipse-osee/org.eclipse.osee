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
  applicability: "Applicability"
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
