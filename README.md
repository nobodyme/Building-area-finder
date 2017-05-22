# Building Area Finder
This is an Android app that helps an user to select buildings or other land areas using markers and then gives the user the coordinates and the area of the selected building along with the distance between each of the markers, it can also be used to eliminate tanks or other structures that makes it impossible for placing of *solar panels* and redirects to a site which helps in providing information about advantages of such solar installation. The application uses the google-maps-api and google-places-api for acheiving the task.

# Installing

You might wanna edit this geo-key line in the [manifest file](https://github.com/nobodyme/Buildingareafinder/blob/master/app/src/main/AndroidManifest.xml) with your own key which can be found [here](https://developers.google.com/maps/documentation/android-api/signup) if the map is not visible or search isn't working

`android:value="@string/google_maps_key" />`

