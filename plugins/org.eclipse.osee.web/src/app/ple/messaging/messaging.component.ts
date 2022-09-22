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
import { Component, OnInit } from '@angular/core';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import navigationStructure from 'src/app/navigation/top-level-navigation/top-level-navigation-structure';

@Component({
  selector: 'app-messaging',
  templateUrl: './messaging.component.html',
  styleUrls: ['./messaging.component.sass']
})
export class MessagingComponent implements OnInit {

  constructor(private userService: UserDataAccountService) { }

  ngOnInit(): void {
  }

  navItems = navigationStructure[0].children.filter(c => c.label === 'Messaging Configuration')[0].children;
  userIsAdmin = this.userService.userIsAdmin;

}
