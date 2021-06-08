import { Pipe, PipeTransform } from '@angular/core';
const transformMatrix: any = {
  name: "SubMessage Name",
  description: "SubMessage Description",
  interfaceSubMessageNumber: "SubMessage Number",
  interfaceMessageRate: "SubMessage Tx Rate",
}
@Pipe({
  name: 'convertSubMessageTitlesToString'
})
export class ConvertSubMessageTitlesToStringPipe implements PipeTransform {

  transform(value: string, ...args: unknown[]): unknown {
    if (transformMatrix[value] != null && transformMatrix[value] != undefined) {
      return transformMatrix[value]
    }
    return value;
  }
  
}

