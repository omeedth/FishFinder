# FishFinder
CS501 Project - Helps users find fish and where they can catch them. Also displays fish facts

Github URL: https://github.com/omeedth/FishFinder

# Project Requirements
- Your App must be worthwhile.
- Your App must utilize two significant APIs.
- Your App must have a database backend, eg, FireBase.
- Your App must utilize authentication, again, eg, FireBase.
- Your App must have a preferences page.
- Due 4/20/2021

# Description
Is mobile application, developed using Android Studio to help users learn about fish and also to locate fish: The user can learn basic information about fish such: length, weight, body shapeÉ so far the user can search for these information by entering ÒSpecies NameÓ but, in our final version f the app we will change the setup so he user can search by entering the ÒCommon NameÓ of the fish.

The app will display all the available information about the fish in a costumed List View, the user can click on any item in the list view, by clicking on an item, the user then will be directed to another page in which he will be able to search for that fish by enter the ÒStateÓ (eg. MA), the app will populate the coordinates associated with that fish that corresponds to the entered state and display those coordinates to the user in a latitude - longitude format.

# Project Settings
```Emulated On:``` Google Pixel 1 <br/>
```Min SDK:``` 22 - Android 5.1 Lollipop

# Technologies
```* means this project requires the API key to be added in keys.xml``` <br/>
FishBase API <br/>
USGS NAS API <br/>
Firebase API (API Key Required) <br/>
Google Maps API (API Key Required)*

# Usage
1. Clone Project
2. Import the project folder into Android Studio
3. Add the values.xml file in your "res/values" folder (This will contain all API keys and/or other private values! NOTE: You must add your own API keys here)
4. Create a Firebase project, download the google-services.json file, and navigate to the android projects main project directory. Place the google-services.json in the app folder.
5. Sync the project with Gradle
6. Run Project