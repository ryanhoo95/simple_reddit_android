# Simple Reddit Android

## Introduction
This app simply demo posting and voting of topic. 
User can view their posted topic by clicking toolbar icon from the Home Page.
Since user authentication is not required, device ID is used to identify the current user's post.

**Note**
The server might be put to sleep after some period of time (no request), so you might see "request failed" dialog when opennning the app. But no worry, you can swipe down the page to resend the request.

## OkHttp
OkHttp is used to make API call using custom class named WebService.
The response is saved in Result object.

## In Memory Cache
ViewModel is used as memory cache. At home page, after getting the top 20 topics, the list will be checked whether it contains current user's topic, if there is any, the topic will be saved to the ViewModel.

The same flow goes when user upvote or downvote the topic, if the voted topic is posted by current user, it will be updated in the ViewModel as well.

At My Recent Post Page, the topics inside ViewModel is displayed. If cache not found, network call will be make to retrive the current user's topics, the retrieved topics is then saved in the ViewModel as cache.

## Instrumented Test
Espresso is used to test empty input in the Home Page.
