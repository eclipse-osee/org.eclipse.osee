import { HttpClient } from '@angular/common/http';
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	linkedSignal,
	viewChild,
} from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { apiURL } from '@osee/environments';
import { map } from 'rxjs';
import { AssigneeSearchComponent } from './components/assignee-search/assignee-search.component';
import { SearchWorkflowComponent } from './components/search-workflow/search-workflow.component';
import { TeamDefinitionFilterComponent } from './components/team-definition-filter/team-definition-filter.component';
import { TeamFilterComponent } from './components/team-filter/team-filter.component';
import { KanbanWorkflow } from '../../models/kanban-workflow';

const workflowColumns = [
	'framework.artifact.name.Title',
	'ats.column.state',
	'ats.column.team',
	'ats.column.assignees',
	'ats.Description',
];

type WorkflowResults = {
	results: { id: string; values: string[] }[];
	rd?: { errors?: boolean };
};

@Component({
	selector: 'osee-search-kanban',
	imports: [
		TeamDefinitionFilterComponent,
		AssigneeSearchComponent,
		TeamFilterComponent,
		SearchWorkflowComponent,
	],
	templateUrl: './search-kanban.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SearchKanbanComponent {
	private readonly http = inject(HttpClient);
	private readonly definitionFilter = viewChild(
		TeamDefinitionFilterComponent
	);
	readonly selectedTeamDefinitionId = computed(
		() => this.definitionFilter()?.selectedTeamDefinitionId() ?? ''
	);

	protected readonly selectedTeams = linkedSignal<string[]>(() => {
		this.selectedTeamDefinitionId();
		return [];
	});
	protected readonly selectedWorkflowIds = linkedSignal<string[]>(() => {
		this.selectedTeamDefinitionId();
		return [];
	});
	protected readonly selectedAssignees = linkedSignal<string[]>(() => {
		this.selectedTeamDefinitionId();
		return [];
	});

	protected readonly search = rxResource({
		params: () => this.definitionFilter()?.selectedTeamScope(),
		stream: ({ params }) =>
			this.http
				.put<WorkflowResults>(apiURL + '/ats/world/search', {
					teamDefIds: params,
					workItemTypes: ['TeamWorkflow'],
					customizeData: {
						columnData: {
							columns: workflowColumns.map((id) => ({
								id,
								show: true,
							})),
						},
					},
				})
				.pipe(
					map(({ results, rd }): KanbanWorkflow[] => {
						if (rd?.errors) {
							throw new Error('ATS workflow search failed');
						}
						return results.map(
							({
								id,
								values: [
									title,
									state,
									team,
									assignees,
									description,
								],
							}) => ({
								id,
								title: title || id,
								state: state ?? '',
								team: team?.trim() ?? '',
								assignees: (assignees ?? '')
									.replace(/^\((.*)\)$/, '$1')
									.split(';')
									.map((name) => name.trim())
									.filter(Boolean),
								description,
							})
						);
					})
				),
	});
	private readonly workflows = computed(() =>
		this.search.hasValue() ? this.search.value() : []
	);

	protected readonly teams = computed(() =>
		[
			...new Set(
				this.workflows()
					.map(({ team }) => team)
					.filter(Boolean)
			),
		].sort((a, b) => a.localeCompare(b))
	);
	protected readonly workflowOptions = computed(() =>
		this.workflows()
			.map(({ id, title }) => ({ id, name: title }))
			.sort((a, b) => a.name.localeCompare(b.name))
	);
	protected readonly assignees = computed(() =>
		[
			...new Set(this.workflows().flatMap(({ assignees }) => assignees)),
		].sort((a, b) => a.localeCompare(b))
	);

	readonly filteredWorkflows = computed(() => {
		const teams = new Set(this.selectedTeams());
		const workflows = new Set(this.selectedWorkflowIds());
		const assignees = new Set(this.selectedAssignees());
		return this.workflows().filter(
			(workflow) =>
				(!teams.size || teams.has(workflow.team)) &&
				(!workflows.size || workflows.has(workflow.id)) &&
				(!assignees.size ||
					workflow.assignees.some((name) => assignees.has(name)))
		);
	});
}
