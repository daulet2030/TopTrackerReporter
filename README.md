# TopTrackerReporter
Report creator for https://tracker.toptal.com/. It will take a screenshot of current week report and send to a slack channel.
 TO RUN: 
 `mvn test -Dusername=<topTrackerUsername> 
           -Dpassword=<topTrackerPassword> 
           -Dchannel=<#slackChannel>
           -DchannelId<rocketChannelId>
           -DuserId<rocketuserId>
           -Dtoken=<slackUserToken>`
