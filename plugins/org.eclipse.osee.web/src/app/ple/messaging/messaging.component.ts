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
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-messaging',
  templateUrl: './messaging.component.html',
  styleUrls: ['./messaging.component.sass']
})
export class MessagingComponent implements OnInit {

  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
  }

  navigateTo(location: string) {
    this.router.navigate([location], {
      relativeTo: this.route.parent,
      queryParamsHandling: 'merge',
    });
  }
}
