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
import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatGridListModule } from '@angular/material/grid-list';
import { HeaderService } from '../../../shared/services/ui/header.service';
interface Tile {
	cols: number;
	rows: number;
	descriptions?: string[];
	imgSrc?: string;
}
@Component({
	selector: 'osee-overview',
	templateUrl: './overview.component.html',
	styleUrls: ['./overview.component.sass'],
	standalone: true,
	imports: [MatGridListModule, NgFor, NgIf],
})
export class OverviewComponent {
	constructor(private headerService: HeaderService) {}

	tiles: Tile[] = [
		{ imgSrc: 'assets/connection_page.jpg', cols: 10, rows: 3 },
		{
			descriptions: [
				'node1 and node2 can represent hardware or software components',
				'conn represents the set of data broken into messages/submessages/structures/elements that represents the traffic on the connection',
				'Generally the information stored is exported to an Interface Control Document (ICD)',
				'transport1 represents the type of connection - example might be MUX, Fibre...etc',
				'Clicking on Connection1 would open up a page showing associated messages such as below',
			],
			cols: 10,
			rows: 1,
		},
		{ imgSrc: 'assets/messages.jpg', cols: 10, rows: 3 },
		{
			descriptions: [
				'The above screenshot shows the list of messages which comprise Connection1',
				'When user selects the drop down to the left of the message name, the message is expanded to show the submessages of the message.',
			],
			cols: 10,
			rows: 1,
		},
		{ imgSrc: 'assets/messages_submsgs.jpg', cols: 10, rows: 4 },
		{
			descriptions: [
				'The above screenshot shows the first message expanded to show the SubMessages contained in message "Control Message"',
				'Clicking on the "Go To Message Details" button will open up a new page which shows details of the submessages',
			],
			cols: 10,
			rows: 1,
		},
		{ imgSrc: 'assets/structures.jpg', cols: 10, rows: 3 },
		{
			descriptions: [
				'Initially after click on "Go To Message Details" will display the list of Structures that make up the SubMessages.',
				'In this case "Temp Control SubMessage" is made up of two structures.',
				'When user selects the drop down to the left of the structure name, the structure is expanded to show the elements as seen in the screenshot below.',
			],
			cols: 10,
			rows: 1,
		},
		{ imgSrc: 'assets/structure_with_elements.jpg', cols: 10, rows: 6 },
	];
}

export default OverviewComponent;
