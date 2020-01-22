*This application is provided without guarantee or warranty*
=========================================================

# AndroidV2DialogFlow

With the deprecation of the [Android DialogFlow SDK](https://github.com/dialogflow/dialogflow-android-client) and the move from DialogFlow V1 to DialogFlow V2 [being forced in March 2020](https://dialogflow.com/docs/reference/v1-v2-migration-guide) there is no native option to incorporate DialogFlow into your Android application.  There are [several API clients](https://cloud.google.com/dialogflow/docs/reference/libraries/overview) available for DialogFlow V2 but note that the Java client does not support Android.

To call DialogFlow V2 Agents from Android it is therefore necessary to either use the REST API or an intermediary system that **does** have a DF client and can manage the DF communications.  Authenticating the REST API on Android does not seem to be supported so this project demonstrates how to use Firebase Functions to communicate with DialogFlow via the [DF NodeJS API](https://cloud.google.com/dialogflow/docs/reference/libraries/nodejs).

![Architecture](https://raw.githubusercontent.com/darryncampbell/AndroidV2DialogFlow/master/media/diagram.png)

You could use any number of intermediary server or serverless component but since Firebase lives within GCP, as does DialogFlow, all the authentication seems to happen automatically.

There is a reference project implementation for this code which has usage limits as defined by the free tier for Firebase, GCP and DialogFlow:
+ **Reference project ID:** androidv2dialogflow-xspkei
+ **Reference project DialogFlow Agent:** _Only Supports a single question, 'How are you?'._

You can compile the sample Android project and run it against this reference backend.

##  Testing the Firebase function

The Firebase function exposes an HTTP onCall endpoint which can be invoked via a POST to `https://us-central1-androidv2dialogflow-xspkei.cloudfunctions.net/detectIntent`.  The question is provided as a JSON object in the body but bear in mind that this reference implementation only supports a single question

````json
{
  "data": {
    "question": "How are you today?"
  }
}
````

Postman output is shown below:

![Testing](https://raw.githubusercontent.com/darryncampbell/AndroidV2DialogFlow/master/media/testing.png)

# Running the code in your own project

## Prerequisites

These prerequisites are only required to modify the existing project
- Node.js and NPM
- Install the [Firebase CLI](https://developers.google.com/assistant/actions/dialogflow/deploy-fulfillment)


## DialogFlow

+ Follow the [DialogFlow setup instructions](https://cloud.google.com/dialogflow/docs/quick/setup) to create a project.  
+ Create an Intent to handle the conversation your GDF instance supports.  The reference implementation has only a single Intent
+ If you want to test GDF from a Windows or Linux box you will need to set up authentication but once I deployed to Firebase, and everything was part of the same GCP project essentially, I did not need to configure separate authentication.

## Firebase

+ Modify the `index.js` file in the `functions` directory to refer to your GDF project ID
  + To find your **Project ID**: In [Dialogflow console](https://console.dialogflow.com/) under **Settings** ⚙ > **General** tab > **Project ID**.
+ On your local machine, in the `functions` directory, run `npm install`
+ Run `firebase deploy --project {PROJECT_ID} to deploy the function
+ The [GDF Quickstart docs](https://cloud.google.com/dialogflow/docs/quick/api#detect-intent-text-nodejs) were good to get started with and this example cribs most of the code from there.
+ _Note: The `index.js` file exposes an HTTP onCall endpoint but an earlier version exposed an onRequest endpoint.  I left the latter commented out in case it is useful_

## Android Client

The Android client in this project will, by default, communicate with the reference project.  To communicate with your own Firebase instance, add the Android application to your Firebase project and configure the Android project with your own `google-services.json` file.  [More info](https://firebase.google.com/docs/android/setup).  

The client uses the [Firebase Functions SDK](https://firebase.google.com/docs/functions/callable) and Android Speech Recognition / text to speech APIs which require a GMS device to run.

### Using the client

As shown in the below image, just tap the mic (floating action) button and ask the only supported question by GDF, 'how are you feeling?'.  The response will be given on the UI and via text to speech, so ensure your volume is turned up.

![Android Client](https://raw.githubusercontent.com/darryncampbell/AndroidV2DialogFlow/master/media/client_1.jpg)

Below: Speech recognition has succeeded.

![Android Client](https://raw.githubusercontent.com/darryncampbell/AndroidV2DialogFlow/master/media/client_2.jpg)

Below: Response has been received from Google DialogFlow

![Android Client](https://raw.githubusercontent.com/darryncampbell/AndroidV2DialogFlow/master/media/client_3.jpg)

