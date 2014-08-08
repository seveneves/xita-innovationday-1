## _"AngularJS - Spray - Twitter Bootstrap"_ Project Template

A template to start a project using Scala, Spray, AngularJS and Twitter Bootstrap (v3).
The provided Bootstrapt template is 'Jumbotron-Narrow'. Start changing the GUI using AngularJS.
Implement your REST API in the Api trait.

# Testing JSON with Curl
*Note: This testing does not replace unit or integration testing!*
To test sending some JSON to your Rest API use:
    'curl -d '<some json>' -H "Content-Type: application/json" http://localhost:9000/<api-url>'

# Run
Use 'sbt run' to run the server locally. 

# Run on Heroku
Use sbt-start-script plugin to create a 'start' script in the target folder.
    Run 'sbt clean compile stage' to create a script script.

Add a Procfile in the project directory and try to run the project using 'foreman'.
If it works, the project can be uploaded to Heroku. See Heroku-Scala on Heroku site.
The 'system.properties' file tells Heroku which Java version to use.