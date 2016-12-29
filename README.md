This app is used to fetch weather forecast and display the data in a neat and optimized RecyclerView. 
This app uses SharedPreferences for persistence of User Preferences. Stores the fetched data in SQLiteDatabase.
Uses Content Providers to fetch data . And also uses FireBaseJobDispatcher for running services in the background.
This app generates a notification after the data has been upgraded in the background (which is performed everyday).