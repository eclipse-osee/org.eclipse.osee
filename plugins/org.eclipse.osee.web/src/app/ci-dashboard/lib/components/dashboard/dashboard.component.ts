/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { PieChartComponent } from 'src/app/ci-dashboard/lib/components/pie-chart/pie-chart.component';

@Component({
	selector: 'osee-dashboard',
	standalone: true,
	imports: [PieChartComponent],
	templateUrl: './dashboard.component.html',
})
export default class DashboardComponent {}
