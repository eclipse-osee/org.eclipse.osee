import { httpResource } from '@angular/common/http';
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	input,
} from '@angular/core';
import {
	MatCard,
	MatCardContent,
	MatCardHeader,
	MatCardTitle,
} from '@angular/material/card';
import { apiURL } from '@osee/environments';
import { workDefinition } from '@osee/shared/types/configuration-management';
import { KanbanWorkflow } from '../../models/kanban-workflow';

@Component({
	selector: 'osee-kanban-board',
	imports: [MatCard, MatCardContent, MatCardHeader, MatCardTitle],
	templateUrl: './board.component.html',
	styleUrl: './board.component.css',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BoardComponent {
	readonly teamDefinitionId = input('');
	readonly workflows = input<readonly KanbanWorkflow[]>([]);

	protected readonly definition = httpResource<workDefinition>(() => {
		const id = this.teamDefinitionId();
		return id
			? apiURL + '/ats/workdef/teamdef/' + encodeURIComponent(id)
			: undefined;
	});

	protected readonly columns = computed(() => {
		const states = this.definition.hasValue()
			? (this.definition.value()?.states ?? [])
			: [];
		const cardsByState = new Map(
			states.map(({ name }) => [name, [] as KanbanWorkflow[]])
		);
		for (const workflow of this.workflows()) {
			const cards =
				cardsByState.get(workflow.state) ??
				cardsByState.get(
					workflow.state.split(/ \((?:Blocked|Hold)\)/, 1)[0]
				);
			cards?.push(workflow);
		}
		return [...cardsByState].map(([name, cards]) => ({
			id: name,
			title: name,
			cards,
		}));
	});
}
