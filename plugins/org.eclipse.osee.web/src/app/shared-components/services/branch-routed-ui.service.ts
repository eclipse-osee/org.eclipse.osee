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
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BranchUIService } from '../../ple-services/ui/branch/branch-ui.service';

@Injectable({
  providedIn: 'root'
})
export class BranchRoutedUIService {

  constructor (private branchService: BranchUIService, private router: Router) { }
  
  set branchType(value: string) {
    let baseUrl;
    if (this.branchService.type.getValue() != "") {
      baseUrl=this.router.url.split(this.branchService.type.getValue().replace(/ /g,"%20"))[0]
    } else {
      baseUrl = this.router.url;
    }
    this.branchService.typeValue = value;
    this.router.navigate([baseUrl,value])
  }
  get type() {
    return this.branchService.type;
  }

  get id() {
    return this.branchService.id;
  }

  set branchId(value: string) {
    let baseUrl;
    if (this.branchService.type.getValue() != "") {
      baseUrl=this.router.url.split(this.branchService.type.getValue().replace(/ /g,"%20"))[0]
    } else {
      baseUrl = this.router.url;
    }
    this.branchService.idValue = value;
    this.router.navigate([baseUrl,this.branchService.type.getValue(),value])
  }

}
