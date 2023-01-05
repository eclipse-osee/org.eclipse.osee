/*********************************************************************
* Copyright (c) 2023 Boeing
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
(function(window,angular)
{
    'use strict';

    var module;

    module = angular.module('aLinkRewrite',[]);
    module.config
    (
        [
            '$provide','$locationProvider',
            function (p,lp)
            {                                       // (decorator is undocumented... *sigh*)
                p.decorator(                        // decorate the
                    '$sniffer',                     // sniffer service
                    [
                        '$delegate',                // obtain a delegate object to modify the sniffer
                        function(d)                 // which we will call 'd'
                        {
                            d.history = false;      // tell angular that we don't have html5 history capabilities
                            return d;
                        }
                    ]
                );

                lp.html5Mode(true).hashPrefix('!');      // HTML5 mode
            }
        ]
    ).run(['$location',function($location){}]);     // inject $location into the browser
})(window,window.angular);