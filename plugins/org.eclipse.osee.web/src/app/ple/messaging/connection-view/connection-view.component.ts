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
import { RouteStateService } from './services/route-state-service.service';

@Component({
  selector: 'app-connection-view',
  templateUrl: './connection-view.component.html',
  styleUrls: ['./connection-view.component.sass']
})
export class ConnectionViewComponent implements OnInit {

  constructor(private route: ActivatedRoute, private router: Router, private routerState: RouteStateService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((values) => {
      this.routerState.branchId = values.get('branchId') || '';
      this.routerState.branchType = values.get('branchType') || '';
    });
  }

}
