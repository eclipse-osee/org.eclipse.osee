import { UrlSegment, NavigationEnd } from '@angular/router';
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
import { Component } from '@angular/core';
import { Router } from '@angular/router';

import navigationStructure from './top-level-navigation-structure';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';

@Component({
  selector: 'osee-top-level-navigation',
  templateUrl: './top-level-navigation.component.html',
  styleUrls: ['./top-level-navigation.component.sass']
})
export class TopLevelNavigationComponent {

  navElements = navigationStructure; // structure that stores the navigation elements
  userIsAdmin = this.userService.userIsAdmin;

  constructor(public router: Router, private userService: UserDataAccountService) {}
}
