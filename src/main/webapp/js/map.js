var map = L
		.map(
				"map",
				{
					key : "b5w0ip0dC0PPuyZ75hbmUPBcQK7IhO0V",
					 source: ["raster", "vector"],
					glyphsUrl : "https://api.tomtom.com/maps-sdk-js/glyphs/v1/{fontstack}/{range}.pbf",
					spriteUrl : "https://api.tomtom.com/maps-sdk-js/sprites/v1/sprite",
					preferCanvas : true,
					inertia : false
//					traffic: { style: "s3", key: "b5w0ip0dC0PPuyZ75hbmUPBcQK7IhO0V" }
				}, 10);

map.locate({setView: true, maxZoom: 15});

/*
 * Added Reports layer 
 */
/*var imported = document.createElement('script');
imported.src = 'js/ReportsLayer.js';
document.head.appendChild(imported);*/

/*
 * Location search layer
 */
var imported1 = document.createElement('script');
imported1.src = 'js/SearchLayer.js';
document.head.appendChild(imported1);


/*
 * Location Places layer
 */
var imported2 = document.createElement('script');
imported2.src = 'js/PlacesLayer.js';
document.head.appendChild(imported2);




