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
import navigationStructure from '../../../navigation/top-level-navigation/top-level-navigation-structure';
import { MessagingHelpComponent } from './messaging-help.component';

const columnDescriptions = navigationStructure[0].children
	.filter((c) => c.label === 'Messaging Configuration')[0]
	.children.find((page) => page.label === 'Help')
	?.children.find((page) => page.label === 'Column Descriptions');
const routes: Routes = [
	{ path: '', component: MessagingHelpComponent },
	{
		path: 'columnDescriptions',
		title: columnDescriptions?.pageTitle || 'OSEE - MIM - Help',
		loadChildren: () =>
			import(
				'./column-descriptions-message-help/column-descriptions-message-help.module'
			).then((m) => m.ColumnDescriptionsMessageHelpModule),
	},
	{
		path: 'overview',
		loadChildren: () =>
			import('./overview/overview.module').then((m) => m.OverviewModule),
	},
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
})
export class MessagingHelpRoutingModule {}
