# JDG + EAP Lab 6 Guide
This explains the steps for lab 6, either follow them step-by-step or if you 
feel adventurous try to accomplish goals without the help of the step-by-step guide.

## Background 
TODO application is a success, but we don't know much about our users. The marketing department has expressed requirements for tracking if users are using Computers orTablets, which OS they are using and which browsers are more popular.

## Use-case
We will implement a solution to store user information. To minimize any impact to performance user information should be stored unstructured and instead use Map/Reduce pattern to structure the data. Initially the user information used will only be the User-Agent HTTP header that all browsers provide.

## These are the main tasks of lab 6

1. Extend the REST layer to store the request data unstructured in a local cache
2. Provide a BiSerivce (Business Intelligence Service) class that can structure the data and return data summarized views of the data.
3. Provide a BiEndpoint (REST Service) to enable UI to access the BiService

## Step-by-Step
