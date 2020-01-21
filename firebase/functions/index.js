//  Based on https://github.com/googleapis/nodejs-dialogflow/blob/master/samples/detect.js

const functions = require('firebase-functions');
const dialogflow = require('dialogflow');
const uuid = require('uuid');

//  https://us-central1-androidv2dialogflow-xspkei.cloudfunctions.net/detectIntent?question=How are you feeling?
//exports.detectIntent = functions.https.onRequest((request, response) => {  //  HTTP onRequest
exports.detectIntent = functions.https.onCall((data, context) => {  //  HTTP onCall
    const projectId = 'androidv2dialogflow-xspkei';
    const sessionId = uuid.v4();
    const languageCode = 'en';
//    const query = request.query.question; //  HTTP onRequest
    const query = data.question;    //  HTTP onCall

    const sessionClient = new dialogflow.SessionsClient();
    async function detectIntent(
        projectId,
        sessionId,
        query,
        contexts,
        languageCode
    ) {
        // The path to identify the agent that owns the created intent.
        const sessionPath = sessionClient.sessionPath(projectId, sessionId);

        // The text query request.
        const request = {
            session: sessionPath,
            queryInput: {
                text: {
                    text: query,
                    languageCode: languageCode,
                },
            },
        };

        if (contexts && contexts.length > 0) {
            request.queryParams = {
                contexts: contexts,
            };
        }

        const responses = await sessionClient.detectIntent(request);
        return responses[0];
    }

    async function executeQuery(projectId, sessionId, query, languageCode) {
        // Keeping the context across queries let's us simulate an ongoing conversation with the bot
        let context;
        let intentResponse;
        try {
            console.log(`Sending Query: ${query}`);
            intentResponse = await detectIntent(
                projectId,
                sessionId,
                query,
                context,
                languageCode
            );
            console.log('Detected intent');
            console.log(
                `Fulfillment Text: ${intentResponse.queryResult.fulfillmentText}`
            );
//            response.send(intentResponse.queryResult.fulfillmentText);  //  HTTP onRequest
            return intentResponse.queryResult.fulfillmentText  //  HTTP onCall
            // Use the context from this response for next queries
            context = intentResponse.queryResult.outputContexts;
        } catch (error) {
            console.log(error);
//            response.send("Error detecting Intent");    //  HTTP onRequest
            return "Error detecting Intent"   //  HTTP onCall
        }
    }

//    executeQuery(projectId, sessionId, query, languageCode);      //  HTTP onRequest
    return executeQuery(projectId, sessionId, query, languageCode); //  HTTP onCall
});

