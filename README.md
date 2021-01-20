#MentionPack
Pack that helps you to manage local groups inside your chat and mention them

## Features
You can form groups out of users in your chat, for example you can form a 'dota2' group.

To add users to group use command _mgadd_:  
``/mgadd dota2 <someUsername1> <someUsername2> ...``  
Here for someUsername1... you should use telegram usernames like @theseems etc. 
  
Then when you want to play with them, simply write a message:  

``@dota2 let's go dota!``
  
Bot will mention all of them like that:  
```🔔 let's go dota``` (using invisible mentions)

## Commands
```
/mdel <mentionGroupName>
- Remove mention group

/mgadd <someUsername1> <someUsername2> ...
- Add user(s) to mention group

/mgat <groupName> <text> (or you can type @<groupName> <text>)
- Mention specified group with text message
```