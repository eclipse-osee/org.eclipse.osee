import { Pipe, PipeTransform } from '@angular/core';
const transformMatrix: any = {
  sub_name: "SubMessage Name",
  sub_description: "SubMessage Description",
  sub_number: "SubMessage Number",
  sub_txRate: "SubMessage Tx Rate",
  name: "Message Name",
  description: "Message Description",
  interfaceMessageNumber: "Message Number",
  interfaceMessagePeriodicity: "Periodicity",
  interfaceMessageRate: "Tx Rate",
  interfaceMessageWriteAccess: "Read/Write",
  interfaceMessageType:"Type"
}
@Pipe({
  name: 'convertMessageTableTitlesToString'
})
export class ConvertMessageTableTitlesToStringPipe implements PipeTransform {

  transform(value: string, ...args: unknown[]): unknown {
    if (transformMatrix[value] != null && transformMatrix[value] != undefined) {
      return transformMatrix[value]
    }
    return value;
  }

}
