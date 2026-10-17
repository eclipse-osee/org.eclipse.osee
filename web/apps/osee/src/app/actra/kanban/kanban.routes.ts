import { Routes } from '@angular/router';

const routes: Routes = [
	{
		path: '',
		loadComponent: () =>
			import('./kanban.component').then((m) => m.KanbanComponent),
	},
];

export default routes;