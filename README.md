# TopTrackerReporter
Report creator for https://tracker.toptal.com. It will take a screenshot of current week report and send to a slack channel or rocket channel.
TO RUN for slack: 
 `mvn test -Dusername=<topTrackerUsername> 
           -Dpassword=<topTrackerPassword> 
           -Dchannel=<#slackChannel>
           -Dtoken=<slackUserToken>`
           

TO RUN for rocket chat: 
 `mvn test -Dusername=<topTrackerUsername> 
           -Dpassword=<topTrackerPassword> 
           -DchannelId=<rocketChannelId>
           -DuserId=<rocketuserId>
           -Dtoken=<rocketUserToken>`

To find out rocketChannelId use
`curl -H "X-Auth-Token: <rocketUserToken>" \
     -H "X-User-Id: <rocketuserId>" \
     http://<yourRocketChatURL>/api/v1/channels.info?roomName=<channelName>`
