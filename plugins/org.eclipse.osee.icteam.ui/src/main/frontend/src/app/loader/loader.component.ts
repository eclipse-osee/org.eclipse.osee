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
import { Component, OnInit } from '@angular/core';
import { LoaderState } from '../Model/loaderModel';
import { Subscription } from 'rxjs';
import { LoaderService } from '../service/loader.service';

@Component({
  selector: 'app-loader',
  templateUrl: './loader.component.html',
  styleUrls: ['./loader.component.scss']
})
export class LoaderComponent implements OnInit {

  show = false;
  private subscription: Subscription;
  noOfRequests: Array<Boolean>;
  constructor(
    private loaderService: LoaderService
  ) {
    console.log("Loader Constructor");

  }
  ngOnInit() {
    this.noOfRequests = new Array<Boolean>();
    this.subscription = this.loaderService.loaderState
      .subscribe((state: LoaderState) => {
        if (state.show) {
          this.noOfRequests.push(state.show);
        } else {
          this.noOfRequests.pop();
        }
        console.log("state.show", state.show);
        // this.show = state.show;
      });
  }
  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

}
