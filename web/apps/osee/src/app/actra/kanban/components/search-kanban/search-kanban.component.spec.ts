import { provideHttpClient } from '@angular/common/http';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSelect } from '@angular/material/select';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { apiURL } from '@osee/environments';

import { SearchKanbanComponent } from './search-kanban.component';

describe('SearchKanbanComponent', () => {
	let fixture: ComponentFixture<SearchKanbanComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [SearchKanbanComponent, NoopAnimationsModule],
			providers: [provideHttpClient(), provideHttpClientTesting()],
		}).compileComponents();

		fixture = TestBed.createComponent(SearchKanbanComponent);
		fixture.detectChanges();
		TestBed.tick();
	});

	it('loads team definitions and shows the other filters only after selection', async () => {
		const http = TestBed.inject(HttpTestingController);
		http.expectOne(apiURL + '/ats/config').flush({
			topTeamDefinition: '56004',
			idToTeamDef: {
				'56004': { name: 'Teams', parentId: null },
				'100': { name: 'SAW SW', parentId: '56004' },
				'200': { name: 'CIS SW', parentId: '56004' },
				'300': { name: 'SAW Development', parentId: '100' },
			},
		});
		await fixture.whenStable();

		const select = fixture.debugElement.query(By.directive(MatSelect));
		const options = (
			select.componentInstance as MatSelect
		).options.toArray();
		expect(
			options.map(({ value, viewValue }) => [value, viewValue])
		).toEqual([
			['200', 'CIS SW'],
			['100', 'SAW SW'],
		]);
		expect(
			fixture.debugElement.queryAll(By.directive(MatSelect))
		).toHaveLength(1);
		http.expectNone(apiURL + '/ats/world/search');

		select.triggerEventHandler('selectionChange', { value: '100' });
		TestBed.tick();
		const search = http.expectOne(apiURL + '/ats/world/search');
		expect(search.request.method).toBe('PUT');
		expect(search.request.body.teamDefIds).toEqual(['100', '300']);
		search.flush({
			results: [
				{
					id: '500',
					values: [
						'Requirements update',
						'Open',
						'SAW Development',
						'Taylor; Casey',
					],
				},
			],
		});
		await fixture.whenStable();

		const filters = [
			...fixture.nativeElement.querySelectorAll('mat-select'),
		];
		expect(filters).toHaveLength(4);
		expect(filters[0].id).toBe('team-definition-filter');
		expect(filters[1].id).toBe('team-filter');

		const [, team, workflow, assignee] = fixture.debugElement
			.queryAll(By.directive(MatSelect))
			.map((element) => element.componentInstance as MatSelect);
		expect(team.options.map(({ viewValue }) => viewValue)).toEqual([
			'SAW Development',
		]);
		expect(workflow.options.map(({ viewValue }) => viewValue)).toEqual([
			'Requirements update',
		]);
		expect(assignee.options.map(({ viewValue }) => viewValue)).toEqual([
			'Casey',
			'Taylor',
		]);
		expect(fixture.componentInstance.filteredWorkflows()).toHaveLength(1);

		select.triggerEventHandler('selectionChange', { value: '200' });
		TestBed.tick();
		const nextSearch = http.expectOne(apiURL + '/ats/world/search');
		expect(nextSearch.request.body.teamDefIds).toEqual(['200']);
		nextSearch.flush({ results: [] });
		await fixture.whenStable();
		expect(fixture.componentInstance.filteredWorkflows()).toEqual([]);

		http.verify();

		select.triggerEventHandler('selectionChange', { value: '' });
		TestBed.tick();
		expect(
			fixture.debugElement.queryAll(By.directive(MatSelect))
		).toHaveLength(1);
	});
});
