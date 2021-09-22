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
