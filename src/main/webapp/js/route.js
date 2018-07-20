map.zoomControl.setPosition('topright');
var checkbox, select, route, polyline;
var loader = document.getElementById('loader');
var computeBestOrder = true;
var routeRepresentationValue = 'polyline';
var markers = [];
var locations = [];
/*var foldable = new L.Foldable({
    closeOnMapClick: false,
    position: 'topleft',
    title: 'Routing options:'
}).addTo(map);*/
/*foldable.addContent(createFoldableContent());*/

//getRoute(computeBestOrder, routeRepresentationValue);

function createFoldableContent() {
    var container = document.createElement('div');
    container.appendChild(createSelect());
    container.appendChild(createCheckbox());
    return container;
}


function distance(lat1, lon1, lat2, lon2, unit) {
    var radlat1 = Math.PI * lat1 / 180;
    var radlat2 = Math.PI * lat2 / 180;
    var theta = lon1 - lon2;
    var radtheta = Math.PI * theta / 180;
    var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
    if (dist > 1) {
        dist = 1;
    }
    dist = Math.acos(dist);
    dist = dist * 180 / Math.PI;
    dist = dist * 60 * 1.1515;
    if (unit == "K") {
        dist = dist * 1.609344;
    }
    if (unit == "N") {
        dist = dist * 0.8684;
    }
    return dist;
}


var sortbyLDist = function (locations) {

    for (i = 0; i < locations.length; i++) {
        locations[i]["distance"] = distance(locations[0].lat, locations[0].lon, locations[i].lat,
            locations[i].lon, "K");
    }

    locations.sort(function (a, b) {
        return a.distance - b.distance;
    });
}


function planRoute() {
    sortbyLDist(locations);
    if (locations.length >= 2) {
        getRoute(true, 'polyline');
        drawMarkers(locations);
    }
}

function createCheckbox() {
    var checkboxId = 'checkbox-route-representation';
    var container = document.createElement('div');
    container.className = 'checkbox-route-representation-label';
    checkbox = document.createElement('input');
    checkbox.className = 'checkbox-route-representation';
    checkbox.type = 'checkbox';
    checkbox.id = checkboxId;
    checkbox.onchange = function () {
        computeBestOrder = checkbox.checked;
        getRoute(computeBestOrder, routeRepresentationValue);
    };
    var label = document.createElement('label');
    label.htmlFor = checkboxId;
    label.appendChild(document.createTextNode('Compute best order'));
    container.appendChild(checkbox);
    container.appendChild(label);
    return container;
}

function createSelect() {
    var container = document.createElement('div');
    select = document.createElement('select');
    select.className = 'route-representation-select';
    select.appendChild(new Option('Polyline', 'polyline'));
    select.appendChild(new Option('None', 'none'));
    select.onchange = function () {
        routeRepresentationValue = select.value;
        getRoute(computeBestOrder, routeRepresentationValue);
    };
    var routeRepresentationLabel = document.createElement('div');
    routeRepresentationLabel.className = 'select-label';
    routeRepresentationLabel.innerHTML = 'Route representation';
    container.appendChild(routeRepresentationLabel);
    container.appendChild(select);
    return container;
}

function drawMarkers(locations) {
    locations.forEach(function (coordinates, index) {
        var svgText = '<svg width="30" height="36" xmlns="http://www.w3.org/2000/svg" xmlns:svg="http://www.w3.org/2000/svg">' +
            '<ellipse id="svg_1" fill="#000" opacity="0.2" ry="2" rx="7.661" cy="34" cx="15"></ellipse>' +
            '<path id="svg_2" fill="#000" d="m25.6,4.4c-2.7,-2.7 -6.5,-4.4 -10.6,-4.4s-7.9,1.7 -10.6,4.4c-2.7,2.7 -4.4,6.5 -4.4,10.6s1.7,7.9 4.4,10.6c2.7,2.7 10.6,8.9 10.6,8.9s7.9,-6.2 10.6,-8.9c2.7,-2.7 4.4,-6.5 4.4,-10.6s-1.7,-7.9 -4.4,-10.6z"></path>' +
            '<circle id="svg_3" fill="none" r="12" cy="15" cx="15" class="innerCircle"></circle>' +
            '<text fill="#ffffff" stroke="#000000" stroke-width="0" x="15" y="23" id="svg_4" font-size="24" font-family="serif" font-weight="900" text-anchor="middle" xml:space="preserve">' + (index + 1) + '</text>' +
            '</svg>';
        var url = 'data:image/svg+xml,' + encodeURIComponent(svgText);
        var myIcon = L.icon({
            iconUrl: url,
            iconSize: [30, 36],
            iconAnchor: [15, 36]
        });
        markers.push(L.marker(coordinates, {
            icon: myIcon
        }).addTo(map));
    });
}

function drawRoute(routeJson) {
    route = tomtom.L.geoJson(routeJson, {style: {color: '#06f', opacity: 1}}).addTo(map);
    if (computeBestOrder) {
        drawMarkers(getOptimizedLocations(routeJson.optimizedWaypoints));
    } else {
        drawMarkers(locations);
    }
    map.fitBounds(route.getBounds(), {padding: [5, 5]});
}

function drawLines(optimizedWaypoints) {
    var polylineOptions = {color: '#06f', dashArray: '20', weight: 4};
    if (optimizedWaypoints) {
        var optimizedLocations = getOptimizedLocations(optimizedWaypoints);
        polyline = L.polyline(optimizedLocations, polylineOptions).addTo(map);
        drawMarkers(optimizedLocations);
    } else {
        polyline = L.polyline(locations, polylineOptions).addTo(map);
        drawMarkers(locations);
    }
    map.fitBounds(polyline.getBounds());
}

function getOptimizedLocations(optimizedWaypoints) {
    var optimizedLocations = [locations[0]];
    optimizedWaypoints.forEach(function (waypoint) {
        optimizedLocations.push(locations[waypoint.optimizedIndex + 1]);
    });
    optimizedLocations.push(locations[locations.length - 1]);
    return optimizedLocations;
}

function clearMap() {
    if (route) {
        map.removeLayer(route);
    }
    if (polyline) {
        map.removeLayer(polyline);
    }
    markers.forEach(function (marker) {
        map.removeLayer(marker);
    });
    markers = [];
}

function getRoute(computeBestOrder, routeRepresentation) {
    showLoader();
    clearMap();
    if (!computeBestOrder && routeRepresentation === 'none') {
        drawLines();
        hideLoader();
        return;
    }
    //alert(JSON.stringify(locations));
    var routing = tomtom.routing({traffic: false})
        .locations(locations)
        .routeRepresentation(routeRepresentation)
        .computeBestOrder(computeBestOrder);
    routing.go()
        .then(function (routeJson) {
            if (routeRepresentation === 'polyline') {
                drawRoute(routeJson);
            } else {
                drawLines(routeJson.optimizedWaypoints);
            }
            hideLoader();
        }).catch(function () {
        tomtom.messageBox({closeAfter: 3000}).setContent('An error occured, please try again').openOn(map);
        hideLoader();
    });
}

function showLoader() {
    loader.style.display = 'block';
    //checkbox.disabled = true;
    //select.disabled = true;
}

function hideLoader() {
    loader.style.display = 'none';
    //checkbox.disabled = false;
    //select.disabled = false;
}
