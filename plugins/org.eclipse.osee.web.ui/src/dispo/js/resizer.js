angular.module('mc.resizer', []).directive('resizer', function($document) {

	return function($scope, $element, $attrs) {

		$element.on('mousedown', function(event) {
			event.preventDefault();

			$document.on('mousemove', mousemove);
			$document.on('mouseup', mouseup);
		});

		function mousemove(event) {

			if ($attrs.resizer == 'vertical') {
				// Handle vertical resizer
				var x = event.pageX;

				if ($attrs.resizerMax && x > $attrs.resizerMax) {
					x = parseInt($attrs.resizerMax);
				}

				$element.css({
					left: x + 'px'
				});

				$($attrs.resizerLeft).css({
					width: x + 'px'
				});
				$($attrs.resizerRight).css({
					left: (x + parseInt($attrs.resizerWidth)) + 'px'
				});

			} else {
				var innerHeight = window.innerHeight;
				var y = innerHeight - event.pageY;
				
				console.log(window.innerHeight);
				if(y < innerHeight - 200 && y > 50) {
					
					// Handle horizontal resizer
	
					$element.css({
						bottom: y + 'px'
					});
	
					$($attrs.resizerTop).css({
						bottom: (y + parseInt($attrs.resizerHeight)) + 'px'
					});
					$($attrs.resizerBottom).css({
						height: y + 'px'
					});
				}
			}
		}

		function mouseup() {
			$document.unbind('mousemove', mousemove);
			$document.unbind('mouseup', mouseup);
		}
	};
});