import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import {
	MatCard,
	MatCardContent,
	MatCardHeader,
	MatCardTitle,
} from '@angular/material/card';

export type KanbanCard = {
	readonly id: string;
	readonly title: string;
	readonly description?: string;
	readonly assignee?: string;
};

export type KanbanColumn = {
	readonly id: string;
	readonly title: string;
	readonly cards: readonly KanbanCard[];
};

@Component({
	selector: 'osee-kanban-board',
	imports: [MatCard, MatCardContent, MatCardHeader, MatCardTitle],
	templateUrl: './board.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BoardComponent {
	readonly columns = input<readonly KanbanColumn[]>([
		{ id: 'to-do', title: 'To Do', cards: [] },
		{ id: 'in-progress', title: 'In Progress', cards: [] },
		{ id: 'done', title: 'Done', cards: [] },
	]);
}
