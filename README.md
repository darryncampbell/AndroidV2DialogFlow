*This application is provided without guarantee or warranty*
=========================================================

# AndroidV2DialogFlow

# WORK IN PROGRESS
Sample application to show how to call DialogFlow V2 APIs from an Android application using Firebase functions

Overview of approach
Single DialogFlow Agent

This code is deployed to a reference implementation with usage limits (free tier, all part of the same GCP project):
- Firebase
- GCP
- DialogFlow

## Prerequisites

These prerequisites are only required to modify the existing project
- Node.js and NPM
- Install the [Firebase CLI](https://developers.google.com/assistant/actions/dialogflow/deploy-fulfillment)

##  Testing the Firebase function

THIS IS NOW WRONG - NEEDS TO BE AN HTTP POST

To test the firebase cloud function call a HTTP GET with `{Firebase function URL}/detectIntent?question={The question}`

For the reference implementation:

**HTTP GET** `https://us-central1-androidv2dialogflow-xspkei.cloudfunctions.net/detectIntent?question=How are you feeling?`

The response will be as defined in the DialogFlow Intent identified by the question.

## DialogFlow



## Firebase

- On your local machine, in the `functions` directory, run `npm install`
Run `firebase deploy --project {PROJECT_ID} to deploy the function
  + To find your **Project ID**: In [Dialogflow console](https://console.dialogflow.com/) under **Settings** ⚙ > **General** tab > **Project ID**.

## Android Client

- Modify the [DialogFlow Comms Service class](https://github.com/darryncampbell/AndroidV2DialogFlow/blob/master/client/app/src/main/java/com/darryncampbell/androidv2dialogflowclient/DialogFlowCommsService.java) `PROJECT_ID` to point to your project's endpoint
    + To find your **Project ID**: In [Dialogflow console](https://console.dialogflow.com/) under **Settings** ⚙ > **General** tab > **Project ID**.
- Android Speech Recognition requires a GMS device to run