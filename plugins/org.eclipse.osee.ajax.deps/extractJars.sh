
cd target/repository/plugins;
find . -name "org.webjars*.jar" -exec jar -xf  {} \;
echo unjarred it;
cd META-INF/resources/webjars/;
mv github-com-AlexSuleap-angular-tree-widget angular-tree-widget;
mv angular-tree-widget/v1.1.1/* angular-tree-widget;
mv angularjs-dropdown-multiselect angularjs-multiselect;
mv angularjs-multiselect/2.0.0-beta.10/dist/src/* angularjs-multiselect;
mv angular-local-storage/0.2.2/dist/* angular-local-storage/0.2.2;
mv html5shiv/3.7.2/dist/* html5shiv/3.7.2;
mv jquery/2.1.3/dist/* jquery/2.1.3;
mv moment/2.9.0/min/* moment/2.9.0;
mv ng-grid/2.0.11/build/* ng-grid/2.0.11;
mv respond/1.4.2/dest/* respond/1.4.2;
mv angular-ui-grid ui-grid;
mv ui-grid/3.0.0-rc.20 ui-grid/3.0.0;
mv angular-ui-bootstrap ui-bootstrap;
mv ui-bootstrap/0.12.0/ui-bootstrap-tpls.min.js ui-bootstrap/0.12.0/ui-bootstrap-tpls-0.12.0.min.js;
mv ui-bootstrap/0.12.1/ui-bootstrap-tpls.min.js ui-bootstrap/0.12.1/ui-bootstrap-tpls-0.12.1.min.js;
mv ui-bootstrap/1.3.1/ui-bootstrap-tpls.min.js ui-bootstrap/1.3.1/ui-bootstrap-tpls-1.3.1.min.js
rm -rf /angularjs-multiselect/2*
pwd;
cp -r * ../../../../../../../org.eclipse.oss.ajax/src
