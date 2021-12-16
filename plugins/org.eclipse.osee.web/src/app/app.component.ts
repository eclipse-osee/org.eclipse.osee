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
import { Component } from '@angular/core';
import { HttpLoadingService } from './services/http-loading.service';
import { SideNavService } from './shared-services/ui/side-nav.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  opened = this.sideNavService.opened;
  isLoading = this.loadingService.isLoading;
  constructor (private sideNavService: SideNavService, private loadingService: HttpLoadingService) {
    
  }
  title = 'OSEE';
}
