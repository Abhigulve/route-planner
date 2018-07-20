 (function(tomtom) {
        // Define your product name and version
        //tomtom.setProductInfo('<your-product-name>', '<your-product-version>');
        // Set TomTom keys
        tomtom.key('b5w0ip0dC0PPuyZ75hbmUPBcQK7IhO0V');
        tomtom.searchKey('b5w0ip0dC0PPuyZ75hbmUPBcQK7IhO0V');

        var map = tomtom.map('map', {
            source: 'vector',
            basePath: '/sdk',
           	glyphsUrl : 'https://api.tomtom.com/maps-sdk-js/glyphs/v1/{fontstack}/{range}.pbf',
			spriteUrl : 'https://api.tomtom.com/maps-sdk-js/sprites/v1/sprite',
			preferCanvas : true,
			inertia : false,
			traffic: { style: 's3', key: 'b5w0ip0dC0PPuyZ75hbmUPBcQK7IhO0V' }
        });
        map.zoomControl.setPosition('bottomright');
        map.attributionControl.setPosition('bottomright');

        /*tomtom.foldable({
            position: 'topleft',
            closeOnMapClick: false
        }).addTo(map).addContent(document.getElementById('form'));*/

        var markersLayer = L.tomTomMarkersLayer().addTo(map);
        markersLayer.setMarkerOptions({
            chunkedLoading: true,
            disableClusteringAtZoom: 12,
            maxClusterRadius: 80,
            noMarkerClustering: false,
            showCoverageOnHover: false,
            spiderfyOnMaxZoom: false
        });
        markersLayer.on('clusterclick', function(event) {
            event.layer.zoomToBounds({padding: [10, 10]});
        });
        var loader = document.getElementById('loader');

        var options = document.querySelector('.select .options');
        var cities = document.querySelector('.cities');
        var moveListItem = function(destination) {
            return function(event) {
                var item = event.target;
                if (item.nodeName !== 'LI') {
                    return;
                }
                destination.appendChild(item);
                // make sure that IE11 is satisfied
                event.preventDefault();
                event.stopPropagation();
                var input = options.parentElement.querySelector('input');
                if (input) {
                    input.checked = false;
                }
            };
        };
        var checkOptionItems = function() {
            if (options.childElementCount) {
                options.parentNode.classList.remove('empty');
            } else {
                options.parentNode.classList.add('empty');
            }
        };
        options.addEventListener('click', moveListItem(cities));
        cities.addEventListener('click', moveListItem(options));
        options.addEventListener('click', checkOptionItems);
        cities.addEventListener('click', checkOptionItems);

        var getParameters = function(form) {
            var places = Array.apply(null, cities.querySelectorAll('li')).map(function(item) {
                return coordinates[item.innerHTML.toLowerCase()];
            });
            var inputs = form.querySelectorAll('input[name=category]:checked');
            return Array.apply(null, inputs)
                .map(function(input) {
                    return places.map(function(place) {
                        return {
                            lat: place.lat,
                            lon: place.lon,
                            query: input.value,
                            limit: 50
                        };
                    });
                }).reduce(function(acc, curr) {
                    return acc.concat(curr);
                }, []);
        };

        var requestCount = 0;
        document.getElementById('form').addEventListener('submit', function(event) {
            event.preventDefault();            
            var parameters = getParameters(this);
            var currentRequest = ++requestCount;
            if (!parameters.length) {
                throw new Error('Insufficient parameters - please, select at least one location and one category.');
            }
            loader.classList.remove('hidden');
            
            tomtom.categorySearch(parameters).go()
                .then(function(results) {
                    if (currentRequest !== requestCount) {
                        return;
                    }
                    loader.classList.add('hidden');
                    var points = results;
                    if (results.length && results[0].length) {
                        points = results.reduce(function(acc, curr) {
                            return acc.concat(curr);
                        }, []);
                    }
                    markersLayer
                        .clearLayers()
                        .setMarkersData(points)
                        .addMarkers();
                    map.fitBounds(markersLayer.getBounds());
                });
            
            
        });

        window.addEventListener('error', function(event) {
            var error = event.error;
            var message = error && (error.message || (error.error && error.error.description)) || 'Error occured';
            tomtom.messageBox({closeAfter: 3000}).setContent(message).openOn(map);
        });
        
        //search box
        
        var searchBoxInstance = tomtom.searchBox({
        	position : "topright",
        	collapsed : true
        }).addTo(map);

        var reportIcon = new tomtom.L.icon({
        	iconUrl : "img/report.png",
        	iconSize : [ 34, 34 ]
        });

        var markerOptions = {
        	icon : reportIcon
        };

        var markersLayer = L.tomTomMarkersLayer();
        // Marker layer to indicate the center of the search
        var searchCenterLayer = tomtom.markersView().addTo(map);
        searchBoxInstance.on(searchBoxInstance.Event.ResultClicked, function(result) {
        	markersLayer.setMarkersData([ result.data ]).addTo(map);
        	var viewport = result.data.viewport;
        	if (viewport) {
        		map.fitBounds([ viewport.topLeftPoint, viewport.btmRightPoint ]);
        	} else {
        		map.fitBounds(markersLayer.getBounds());
        	}
        	drawSearchCenter(result.data.position);
        });

        // Draw a marker at the center of the map
        function drawSearchCenter(position) {
        	var currentLocation = map.getCenter();
        	var markerOptions = {
        		title : 'Search Center\nLatitude: ' + position.lat
        				+ '\nLongitude: ' + position.lon,
        		icon : tomtom.L.icon({
        			iconUrl : 'img/search_center.png',
        			iconSize : [ 24, 24 ],
        			iconAnchor : [ 12, 12 ]
        		})
        	};
        	searchCenterLayer.clearLayers();
        	searchCenterLayer.addLayer(
        			tomtom.L.marker([ position.lat, position.lon ],
        					markerOptions)).addTo(map);
        }

    })(tomtom);