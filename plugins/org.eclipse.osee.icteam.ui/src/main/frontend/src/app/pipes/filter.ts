/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
import { Pipe, PipeTransform, Injectable } from '@angular/core';

@Pipe({
    name: 'filter'
})

@Injectable()
export class FilterPipe implements PipeTransform {

    transform(items: any[], field: any, value: string): any[] {
        if (!items) {
            return [];
        }
        if (!field || !value) {
            return items;
        }
      

        let itemReturn = items.filter(singleItem => {

            if (field.length>1) {
                return this.getProerty(singleItem,field)[0].toLowerCase().includes(value.toLowerCase());
            } else {
                if (typeof singleItem[field] === "string") {
                    return singleItem[field].toLowerCase().includes(value.toLowerCase());
                } else if (typeof singleItem[field] === "number") {
                    var number = singleItem[field];
                    var nuberString = number.toString();
                    return nuberString.search(new RegExp(value, 'gi')) > -1 ? true : false;
                }
                else if (singleItem[field].constructor === Array) {
                    let dataFound: Boolean = false;
                    singleItem[field].forEach(element => {
                        if (element.toLowerCase().includes(value.toLowerCase())) {
                            dataFound = true;
                            return true;
                        }
                    });
                    return dataFound ? true : false;
                }
            }

        });
        return itemReturn;
    }

    
    getProerty(eachItem: any, properity: Array<string>) {

        let data = eachItem;
        if (properity.length === 1) {
          return properity[0];
        }
        switch (properity.length) {
          case 2: return eachItem[properity[0]][properity[1]];
          case 3: return eachItem[properity[0]][properity[1]][properity[2]];
          case 4: return eachItem[properity[0]][properity[1]][properity[2]][properity[3]];
          case 5: return eachItem[properity[0]][properity[1]][properity[2]][properity[3]][properity[4]];
          case 6: return eachItem[properity[0]][properity[1]][properity[2]][properity[3]][properity[4]][properity[5]];
        }
        return eachItem[properity[0]];
      }
      
}