# OSEE

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 11.1.4. Currently we are running version 14.2.4.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Deploying to Jetty
Run `ng build -c production` and copy the produced contents into /plugins/org.eclipse.osee.web.deploy/OSEE-INF/web/dist. Run your local server.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io). Tests should be run with the --browsers=ChromeHeadless flag due to some third party dependencies not playing nice in test.

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via Cypress. It is recommended to leverage the --watch=false and --headless=true flags when running e2e tests. You may also specify a spec range such as 'cypress/integration/ple/plconfig/**/*'.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.
