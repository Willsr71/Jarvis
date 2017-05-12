Jarvis
======
[![Build Status](https://ci.noxal.net/job/Jarvis/badge/icon)](https://ci.noxal.net/job/Jarvis/)
![Java JDK v1.8][java]
![JDA v3.0.0][jda]

A multipurpose Discord bot  
[Add this bot to your server](https://discordapp.com/oauth2/authorize?client_id=182630745108512768&scope=bot&permissions=268446806)

Commands
=======
No module
---------
|Command|Permission|Description|
|---|---|---|
|clear \[amount\]|Manage Messages|Deletes the specified number of messages from the current channel. Default is 10|
|commandadd \<name\> \<response\>|Manage Messages|Adds a custom command that responds with the response field. Guild specific|
|commandremove \<name\>|Manage Messages|Removes a custom command. Guild specific|
|define \<word\>|None|Displays the first Urban Dictionary result of a word|
|google \<search string\>|None|Displays a google.com link of the search string|
|help|None|Displays commands and custom commands|
|invite|None|Displays an invite link for the bot|
|modules|None|Displays available modules|
|moduleenable \<module\>|None|Enables the specified module|
|moduledisable \<module\>|None|Disables the specified module|
|restart <\bot mention\>|Bot Owner|Stops the bot|
|source|None|Displays a link to this GitHub repository|
|stats|None|Displays stats about the bot instance|
|trump|None|Displays a random Trump quote|

Admin Module
------------
|Command|Permission|Description|
|---|---|---|
|ban \<user mention&#124;user id\> \[duration\]|Ban Members|Bans the specified member for the specified amount of time. Default time is infinite|
|banlist|None|Displays the currently banned users and remaining durations|
|bantime \<user mention\>|None|Displays the remaining duration of the mentioned user's ban|
|unban \<user mention\>|Ban Members|Unbans the specified member|
|mute \<user mention&#124;user id\> \[duration\]|Voice Mute Others|Mutes the specified member for the specified amount of time. Default time is infinite|
|mutelist|None|Displays the currently mutes users and remaining durations|
|mutetime \<user mention\>|None|Displays the remaining duration of the mentioned user's mute|
|unmute \<user mention\>|Voice Mute Others|Unmutes the specified member|

Overwatch Module
----------------
|Command|Permission|Description|
|---|---|---|
|owstats \[user mention&#124;battletag\]|None|Shows the user's level, current competitive rank, and the top three heroes for quick play and competitive by playtime|
|owheroes \[user mention&#124;battletag\]|None|Shows the user's quickplay and current competitive heroes in order of playtime|
|battletagadd \<battletag\> \[user mention\]|None|Adds the battletag to the specified discord account to allow for mentioning|
|battletagremove|None|Removes the battletag from the sender's discord account|

ChatBot Module
--------------
|Command|Permission|Description|
|---|---|---|
|botadd|Manage Messages|Adds a chat bot to the current channel|
|botremove|Manage Messages|Removes a chat bot from the current channel|

Usage
=====
1. Download the jar from [https://ci.noxal.net/job/Jarvis/]() or compile it
2. Create a `config` directory
3. Create `jarvis.json` in the config directory
4. Paste in the example config file and modify it appropriately
5. Run the command `java -jar jarvis.jar`

Config File
===========
```json
{
  "discord": {
    "token": "a98s7ng987wt98c3834fvn98.c2345c.987asfdfpo8yn9op978ty07bntj",
    "owners": [
      "112587845968912384"
    ],
    "statusMessageInterval": 60,
    "statusMessages": [
      "SyntaxError",
      "NullPointerException",
      "InterruptedException",
      "401 Unauthorized",
      "403 Forbidden",
      "404 Not Found",
      "406 Unacceptable",
      "410 Gone",
      "418 I'm a teapot",
      "503 Servers on Fire"
    ]
  },
  "sql": {
    "host": "db.example.com",
    "database": "database",
    "user": "user",
    "password": "password"
  }
}

```

|Field|Description|
|---|---|
|discord - token|Discord Auth Token. You can acquire this by going to [discordapp.com/developers/applications/me]()|
|discord - owners|Numerical ids of the owners of the bot|
|discord - statusMessageInterval|Interval in seconds to wait before picking a new status message|
|discord - statusMessages|Messages used as the "Playing" status of the bot|
|sql - host|Host of the MySQL database|
|sql - database|Database to use|
|sql - user|User to use|
|sql - password|Password to use|

[java]: https://img.shields.io/badge/Java%20JDK-v1.8-blue.svg "Java JDK 8"
[jda]: https://img.shields.io/badge/JDA-v3.0.0-green.svg "JDA 3.0"
