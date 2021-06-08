import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'displayTruncatedStringWithFieldOverflow'
})
export class DisplayTruncatedStringWithFieldOverflowPipe implements PipeTransform {
  transform(value: string, ...args: number[]): string {
    let stringLength = 0;
    if (args[0]!=null && args[0]!=undefined && args[0]>0) {
      stringLength = args[0];
    } else {
      stringLength = 10;
    }
    if (value.length < stringLength) {
      return value;
    } else {
      let sub = value.substring(0, stringLength);
      sub = sub + '...';
      return sub;
    }
  }

}
