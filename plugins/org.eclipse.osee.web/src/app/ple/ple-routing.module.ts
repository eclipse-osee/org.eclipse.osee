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
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import navigationStructure from '../layout/lib/navigation/top-level-navigation/top-level-navigation-structure';
import { PleComponent } from './ple.component';

const ple = navigationStructure[0].children.find(
	(page) => page.label === 'Product Line Engineering - Home'
);
const plconfig = navigationStructure[0].children.find(
	(page) => page.label === 'Product Line Configuration'
);
const messaging = navigationStructure[0].children.find(
	(page) => page.label === 'Messaging Configuration'
);
const asciidoc = navigationStructure[0].children.find(
	(page) => page.label === 'AsciiDoc Editor'
);

const routes: Routes = [
	{
		path: '',
		title: ple?.pageTitle,
		component: PleComponent,
	},
	{
		path: 'plconfig',
		title: plconfig?.pageTitle,
		loadChildren: () =>
			import('./plconfig/plconfig.module').then((m) => m.PlconfigModule),
	},
	{
		path: 'messaging',
		title: messaging?.pageTitle,
		loadChildren: () => import('./messaging/messaging.routes'),
	},
	{
		path: 'asciidoc',
		title: asciidoc?.pageTitle,
		loadChildren: () =>
			import('./asciidoc/asciidoc.module').then((m) => m.AsciidocModule),
	},
	{
		path: 'changes',
		loadChildren: () =>
			import('./change-report/change-report.module').then(
				(m) => m.ChangeReportModule
			),
	},
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
})
export class PleRoutingModule {}
