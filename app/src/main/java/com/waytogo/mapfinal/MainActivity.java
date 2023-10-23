package com.waytogo.mapfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//new thing
import com.mapbox.mapboxsdk.annotations.MarkerOptions;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,Callback<DirectionsResponse>, PermissionsListener {
    //line 379 for route simulation options

    private MapView mapview;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private LocationEngine locationEngine;
    private Location originLocation;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    //from search video
    private static final int REQUEST_CODE_AUTOCOMPLETE =1;
    private CarmenFeature home;
    private CarmenFeature work;
    private String geojsonSourceLayerId="geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private static final int REQUEST_CODE = 5678;
    String address;
    Point origin = Point.fromLngLat(90.399452,23.777176);
    Point destination = Point.fromLngLat(90.399452,23.777176);
    private static final String ROUTE_LAYER_ID="route-layer-id";
    private static final String ROUTE_SOURCE_ID="route-source-id";
    private static final String ICON_LAYER_ID="icon-layer-id";
    private static final String ICON_SOURCE_ID="icon-source-id";
    private static final String RED_PIN_ICON_ID="red-pin-icon-id";
    private MapboxDirections client;
    int c = 0;
    MapboxNavigation navigation;
    double distance;
    String st;
    String startLocation="";
    String endLocation="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Mapbox.getInstance(this,"sk.eyJ1Ijoic2hhaGlsMDEiLCJhIjoiY2w2b290aGZwMGNxejNicW9lMDVjdWoycSJ9.rtvQeWsrJXpGNpNA6W3kDQ");

        setContentView(R.layout.activity_main);

        navigation = new MapboxNavigation(this,"sk.eyJ1Ijoic2hhaGlsMDEiLCJhIjoiY2w2b290aGZwMGNxejNicW9lMDVjdWoycSJ9.rtvQeWsrJXpGNpNA6W3kDQ"); //from video

        mapview = findViewById(R.id.mapView);
        mapview.onCreate(savedInstanceState);
        mapview.getMapAsync(this);

        BottomNavigationView bnv = findViewById(R.id.bottomNavigationView);

        bnv.setSelectedItemId(R.id.home);

        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favourites:
                        startActivity(new Intent(getApplicationContext(), Favourites.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        return true;
                    case R.id.info:
                        startActivity(new Intent(getApplicationContext(), aboutpage.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });


    }

    @Override
    public void onMapReady(final MapboxMap mapboxMap) {




        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                enableLocationComponent(style);
                initSearchFab();

                addUserLocations();
                Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_location_on_24,null);
                Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);
                style.addImage(symbolIconId,mBitmap);

                setUpSource(style);

                setUpLayer(style);

                initSource(style);

                initLayers(style);


                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    LatLng source;
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {

                        if (c==0){
                            origin = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                            source= point;
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(point);
                            markerOptions.title("Source");
                            mapboxMap.addMarker(markerOptions);
                            reverseGeocodefunc(point,c);
                        }

                        if(c==1){
                            destination = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                            getRoute(mapboxMap,origin,destination);
                            MarkerOptions markerOptions2 = new MarkerOptions();
                            markerOptions2.position(point);
                            markerOptions2.title("destination");
                            mapboxMap.addMarker(markerOptions2);
                            getRoute(mapboxMap,origin,destination);
                        }
                        if (c>1){
                            c=0;
                            recreate();
                        }
                        c++;
                        return true;
                    }
                });

                TrafficPlugin trafficPlugin = new TrafficPlugin(mapview, mapboxMap, style);

                // Enable the traffic view by default
                trafficPlugin.setVisibility(true);

                findViewById(R.id.traffic_toggle_fab).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mapboxMap != null) {
                            trafficPlugin.setVisibility(!trafficPlugin.isVisible());
                        }
                    }
                });
            }
        });

    }





    private void reverseGeocodefunc(LatLng point, int c)
    {
        MapboxGeocoding reverseGeocode= MapboxGeocoding.builder()
                .accessToken(getString(R.string.access_token))
                .query(Point.fromLngLat(point.getLongitude(),point.getLatitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_POI) // or TYPE_ADDRESS
                .build();
        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                List<CarmenFeature> results = response.body().features();

                if (results.size()>0){
                    CarmenFeature feature;
                    Point firstResultPoint = results.get(0).center();
                    feature=results.get(0);
                    if (c==0)
                    {
                        startLocation=feature.placeName();
                        TextView tv2 = findViewById(R.id.s);
                        tv2.setText(startLocation);
                        Toast.makeText(MainActivity.this, ""+feature.placeName(), Toast.LENGTH_SHORT).show();
                    }
                    else if (c==1){
                        endLocation=feature.placeName();
                        TextView tv3 = findViewById(R.id.d);
                        tv3.setText(endLocation);
                        Toast.makeText(MainActivity.this, ""+feature.placeName(), Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(MainActivity.this, "Not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initLayers(@NonNull Style loadedMapStyle){
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        routeLayer.setProperties(
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);

        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.ic_baseline_location_on_24)));

        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID,ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                PropertyFactory.iconIgnorePlacement(true),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconOffset(new Float[]{0f,-9f})));
    }

    private void initSource(@NonNull Style loadedMapStyle){
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(),origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(),destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    private void getRoute(final MapboxMap mapboxMap, Point origin, final Point destination){
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.access_token))
                .build();
        client.enqueueCall(this);
    }

    private void navigationRoute(){
        NavigationRoute.builder(this)
                .accessToken(getString(R.string.access_token))
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body()==null){
                            Toast.makeText(MainActivity.this, "No routes found", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (response.body().routes().size()<1){
                            Toast.makeText(MainActivity.this, "No routes", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DirectionsRoute route= response.body().routes().get(0);
                        boolean simulateRoute = true;

                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(route)
                                .shouldSimulateRoute(false) //is false for POE, otherwise use 'true or simulateRoute' for simulation
                                .build();

                        NavigationLauncher.startNavigation(MainActivity.this,options);


                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
    }

    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
        if (response.body() == null) {
            Toast.makeText(MainActivity.this, "No routes found", Toast.LENGTH_SHORT).show();
            return;
        } else if (response.body().routes().size() < 1) {
            Toast.makeText(MainActivity.this, "No routes", Toast.LENGTH_SHORT).show();
        }

        final DirectionsRoute currentRoute = response.body().routes().get(0);
        distance= currentRoute.distance()/1000;
        st = String.format("%.2f K.M",distance);
        TextView dv = findViewById(R.id.distanceView);
        dv.setText(st);

        if (mapboxMap != null){
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                    if (source!=null){
                        source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), Constants.PRECISION_6));
                    }
                }
            });
        }

    }

    @Override
    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

    }

    public void confirmed(View view){
        navigationRoute();
    }

    private void initSearchFab(){
        findViewById(R.id.searchbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() !=null? Mapbox.getAccessToken() : getString(R.string.access_token))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .addInjectedFeature(home)
                                .addInjectedFeature(work)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(MainActivity.this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });
    }

    private void addUserLocations(){
        home= CarmenFeature.builder().text("Mapbox SF office")
                .geometry(Point.fromLngLat(-122.3964485,37.7912561))
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work= CarmenFeature.builder().text("Mapbox DC office")
                .geometry(Point.fromLngLat(-77.0338348,38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    private void setUpSource(@NonNull Style loadedMapStyle){
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setUpLayer(@NonNull Style loadedMapStyle){
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID",geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[]{0f,-8f})
        ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == Activity.RESULT_OK && requestCode==REQUEST_CODE_AUTOCOMPLETE){
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            if (mapboxMap !=null){
                Style style = mapboxMap.getStyle();
                if (style!=null){
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if(source != null){
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                    ((Point) selectedCarmenFeature.geometry()).longitude()))
                                .zoom(14)
                                .build()),4000);
                }
            }
        }
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle){
        if (PermissionsManager.areLocationPermissionsGranted(MainActivity.this)){
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(MainActivity.this, loadedMapStyle).build());

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);

            locationComponent.setRenderMode(RenderMode.COMPASS);
        }else{
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    protected void onStart() {
        super.onStart();
        mapview.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapview.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapview.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
        navigation.onDestroy();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle outState) {
        super.onPostCreate(outState);
        mapview.onSaveInstanceState(outState);
    }

    //from video
    @Override
    public void onExplanationNeeded(List<String> permissionToExplain) {
        Toast.makeText(this, "explain permission", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted){
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        }else{
            finish();
        }
    }

   /* @Override
    public boolean onMapClick(@NonNull LatLng point) {
    return false;
        }
    }*/



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapview.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_language,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                SymbolLayer countryLabelTextSymbolLayer =
                        style.getLayerAs("country-label");
                if (countryLabelTextSymbolLayer !=null){
                    switch (item.getItemId()){
                        case R.id.french:
                            countryLabelTextSymbolLayer.setProperties(textField("{name_fr}"));
                            return;
                        case R.id.logout:
                            Intent intent = new Intent(MainActivity.this, Login.class);
                            startActivity(intent);
//                        case R.id.about:
//                            Intent i = new Intent(MainActivity.this, aboutpage.class);
//                            startActivity(i);
                        default:
                            countryLabelTextSymbolLayer.setProperties(textField("{name_en}"));
                    }
                }
            }
        });
        return super.onOptionsItemSelected(item);
    }


    public void nearMall (View view) {
        //new thing
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("The Pavilion Shopping Centre");
        markerOptions.position(new LatLng(-29.850081311183207, 30.935641254676884));
        markerOptions.snippet("Jack Martens Dr, Dawncliffe, Westville, 3611");
        mapboxMap.addMarker((markerOptions));
    }
    public void nearInstitution (View view) {
        //new thing
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("The Bergtheil Museum Westville");
        markerOptions.position(new LatLng(-29.833586255610527, 30.93055478486177));
        markerOptions.snippet("16 Queens Ave, Berea West, Westville, 3629");
        mapboxMap.addMarker((markerOptions));
    }
    public void nearhospital (View view) {
        //new thing
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Westville Country Club");
        markerOptions.position(new LatLng(-29.837032979090168, 30.924750756743315));
        markerOptions.snippet("1 Link Rd, Dawncliffe, Westville, 3629");
        mapboxMap.addMarker((markerOptions));
    }




}