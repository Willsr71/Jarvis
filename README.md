Jarvis
======
[![Build Status](https://ci.noxal.net/job/Jarvis/badge/icon)](https://ci.noxal.net/job/Jarvis/)
![Java JDK v1.8](https://img.shields.io/badge/Java%20JDK-v1.8-blue.svg)
![JDA v3.5.0](https://img.shields.io/badge/JDA-v3.5.0-green.svg)

A multipurpose Discord bot   
[Add this bot to your server](https://discordapp.com/oauth2/authorize?client_id=182630745108512768&scope=bot&permissions=268446806)

[Configuration & Usage](https://github.com/Willsr71/Jarvis/wiki/Configuration-&-Usage)

[Creating plugins](https://github.com/Willsr71/Jarvis/wiki/Plugins)

Included Modules
================
Jarvis
----
Always active

|Command|Permission|Description|
|---|---|---|
|help|None|Displays commands and custom commands|
|invite|None|Displays an invite link for the bot|
|modules|None|Displays available modules|
|moduleenable \<module\>|None|Enables the specified module|
|moduledisable \<module\>|None|Disables the specified module|
|moduleload \<module jar\>|Bot Owner|Load a module from a jar|
|moduleunload \<module\>|Bot Owner|Unload a module|
|restart \<bot mention\>|Bot Owner|Stops the bot|
|source|None|Displays a link to this GitHub repository|
|stats|None|Displays stats about the bot instance|

Admin Module ![default](https://img.shields.io/badge/default-yes-green.svg)
------------
Administrative commands such as ban and mute

|Command|Permission|Description|
|---|---|---|
|clear \[amount\]|Manage Messages|Deletes the specified number of messages from the current channel. Default is 10|
|ban \<user mention&#124;user id\> \[duration\]|Ban Members|Bans the specified member for the specified amount of time. Default time is infinite|
|banlist|None|Displays the currently banned users and remaining durations|
|bantime \<user mention&#124;user id\>|None|Displays the remaining duration of the mentioned user's ban|
|unban \<user mention&#124;user id\>|Ban Members|Unbans the specified member|
|mute \<user mention&#124;user id\> \[duration\]|Voice Mute Others|Mutes the specified member for the specified amount of time. Default time is infinite|
|mutelist|None|Displays the currently mutes users and remaining durations|
|mutetime \<user mention&#124;user id\>|None|Displays the remaining duration of the mentioned user's mute|
|unmute \<user mention&#124;user id\>|Voice Mute Others|Unmutes the specified member|

Assistance Module ![default](https://img.shields.io/badge/default-yes-green.svg)
-----------------
Basic assitance commands such as remindme, define, and google

|Command|Permission|Description|
|---|---|---|
|define \<word\>|None|Displays the first Urban Dictionary result of a word|
|google \<search string\>|None|Displays a lmgtfy.com link of the search string|
|remindme \<delay\> \<message\>|None|Reminds the user of the specified message after the specified time|

ChatBot Module ![not default](https://img.shields.io/badge/default-no-red.svg)
--------------
An interactive chatbot and commands

|Command|Permission|Description|
|---|---|---|
|botadd|Manage Messages|Adds a chat bot to the current channel|
|botremove|Manage Messages|Removes a chat bot from the current channel|

CustomCommands Module ![default](https://img.shields.io/badge/default-yes-green.svg)
---------------------
Custom commands and responses.

|Command|Permission|Description|
|---|---|---|
|commandadd \<name\> \<response\>|Manage Messages|Adds a custom command that responds with the response field. Guild specific|
|commandremove \<name\>|Manage Messages|Removes a custom command. Guild specific|
|commands|None|Displays all custom commands|

Flair Module ![not default](https://img.shields.io/badge/default-no-red.svg)
------------
Offers per-user flairs (roles) with the ability to color and name them freely

|Command|Permission|Description|
|---|---|---|
|flair|None|Displays a list of flair commands and their usage|
|flairgetcolor \[user mention\]|None|Displays the specified user's flair color|
|flairsetcolor \<color name&#124;hex code\>|None|Sets the color of the user's flair|
|flairsetname \<name\>|None|Sets the name of the user's flair|
|flairlist|None|Lists all flairs and the users associated with them|
|flairimport \[role exclude list\]|Administrator|Adds the user's roles to the flair system|

Levels Module ![not default](https://img.shields.io/badge/default-no-red.svg)
-------------
Levels plugin and commands

|Command|Permission|Description|
|---|---|---|
|levels|None|Displays the xp leaderboard for the current server|
|rank \[user mention&#124;user id\]|None|Displays xp, rank, and leaderboard position for the specified user|
|levelsignorechannel \[channel mention\]|Manage Channels|Ignores a channel for the purposes of gaining experience|
|levelsoptin|None|Opt into the leveling system|
|levelsoptout|None|Opt out of the leveling system|
|levelssilencechannel \[channel mention\]|Manage Channels|Silences or unsilences channels. Silencing prevents level up messages from appearing in specified channels|
|importmee6|Administrator|Imports level data from Mee6|

OhNo Module ![not default](https://img.shields.io/badge/default-no-red.svg)
-----------
Responds with an "oh no" image whenever a user says "oh no"

Overwatch Module ![not default](https://img.shields.io/badge/default-no-red.svg)
----------------
Overwatch related commands

|Command|Permission|Description|
|---|---|---|
|owstats \[user mention&#124;user id&#124;battletag\]|None|Shows the user's level, current competitive rank, and the top three heroes for quick play and competitive by playtime|
|owheroes \[user mention&#124;user id&#124;battletag\]|None|Shows the user's quickplay and current competitive heroes in order of playtime|
|battletagadd \<battletag\> \[user mention&#124;user id\]|None|Adds the battletag to the specified discord account to allow for mentioning|
|battletagremove|None|Removes the battletag from the sender's discord account|

Console Commands
================
|Command|Usage|
|---|---|
|stop|Stops the bot|
