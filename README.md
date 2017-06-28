# Building Area Finder
This is an Android app that helps an user to select buildings or other land areas using markers and then gives the user the coordinates and the area of the selected building along with the distance between each of the markers, it can also be used to eliminate tanks or other structures that makes it impossible for placing of *solar panels* and redirects to a site which helps in providing information about advantages of such solar installation. The application uses the `google-maps-api` for incorporating the map and it's features, `google-places-api` for incorporating the place search and the `sphericalUtil` library for irregular area computation performed on the map.

# Installing

You might wanna edit the following geo-key line in the [manifest file](https://github.com/nobodyme/Buildingareafinder/blob/master/app/src/main/AndroidManifest.xml) with your own key which can be found [here](https://developers.google.com/maps/documentation/android-api/signup) if the map is not visible or search isn't working and then run it on the device.

`android:value="@string/google_maps_key" />`

