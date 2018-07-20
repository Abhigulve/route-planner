var imported2 = document.createElement('script');
imported2.src = 'js/PlacesLayer.js';
document.head.appendChild(imported2);

var route2 = document.createElement('script');
route2.src = 'js/route.js';
document.head.appendChild(route2);

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
	
	var searchedPlaces = getPlacesForSelectedCity(result.data.position.lat,result.data.position.lon);
//	var resp = searchedPlaces.responseJSON.results;
	//alert(JSON.stringify(searchedPlaces.responseText));
	locations = [];
	var geojson = convertToGeoJson(searchedPlaces.responseText);
	plotPlaces(geojson);
});

var convertToGeoJson = function(searchedPlaces){
	searchedPlaces = JSON.parse(searchedPlaces);
	var poiArray = [];
	for(var i=0; i < searchedPlaces.pois.length; i++){
		//alert(JSON.stringify(searchedPlaces.pois[i]));
		var poi=  new Object();
		poi.type="Feature";
		var geometry=  new Object();
		poi.geometry = geometry;
		poi.geometry.type="Point";
		var coordinates = [];
		poi.geometry.coordinates = coordinates;
		poi.geometry.coordinates[0] = parseFloat(searchedPlaces.pois[i].position.lon);
		poi.geometry.coordinates[1] = parseFloat(searchedPlaces.pois[i].position.lat);
		
		poi.properties = new Object();
		poi.properties.name = searchedPlaces.pois[i].poi.name;
		poi.properties.phone = searchedPlaces.pois[i].poi.phone;
		poi.properties.url = searchedPlaces.pois[i].poi.url;
		poi.properties.categories = searchedPlaces.pois[i].poi.classifications[0].code;
		if(searchedPlaces.pois[i].ratingAndReview.candidates[0].photos){
			poi.properties.photos = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+searchedPlaces.pois[i].ratingAndReview.candidates[0].photos[0].photo_reference+"&key=AIzaSyAjaD9K1m3H3ozNirySM7caoby32qoiiKA";
		}
		poi.properties.rating = searchedPlaces.pois[i].ratingAndReview.candidates[0].rating;
		poi.properties.open_now = false;
		if(searchedPlaces.pois[i].ratingAndReview.candidates[0].opening_hours){
			poi.properties.open_now = searchedPlaces.pois[i].ratingAndReview.candidates[0].opening_hours.open_now;
		}
		//poi.properties.dist = searchedPlaces.pois[i].ratingAndReview.candidates[0].rating;
		
		poi.properties.entryPoints = [];
		poi.properties.entryPoints[0] = parseFloat(searchedPlaces.pois[i].entryPoints.lat);
		poi.properties.entryPoints[1] = parseFloat(searchedPlaces.pois[i].entryPoints.lon);

		poi.properties.wikitext = searchedPlaces.pois[i].wikitext;
		
		poiArray[i] = poi;
		console.log(poi);
	}
	var geojson = new Object();
	geojson.type="FeatureCollection";
	geojson.features = poiArray;
	return geojson;
}


