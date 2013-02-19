Muni
====
Provides town management for Bukkit Minecraft servers

http://dev.bukkit.org/server-mods/muni

Development Roadmap
===================
First Alpha ( Pre-Alpha 0.02 Released 4 Feb 2013 )( Pre-Alpha 0.04 Released 19 Feb 2013 ) 
===========
1) Command executors responds with messages (Done a few days ago - 27 Jan) 

2) Town class stores and changes the town data and can read/write into a sql flat file ( Class created, needs to read / write to database - 27 Jan ) 
( Working pretty well with SQLite - 4 Feb ) (SQLite working solid except for occasional on disable saves where DB locks.  MySQL not yet tested but believe may work - 18 Feb ) 

3) Proof of working with economy (items taken directly from player's inventory) ( Working and somewhat tested - 27 Jan )
( Tested more extensively - 4 Feb ) (Future: expand the DB for rankup item bank account and add to town class - 18 Feb )

4) Reads and writes global variables to config.yml (Done but needs testing - 27 Jan)
( Satisfied with operation - 4 Feb ) 

5) Command Executors work for everything but world guard region functionality
( This is the current stage of development - 4 Feb ) 
(Commands are using Maps for quick access to town data, currently debugging.  Comments added to each command and made help output more verbose. 18 Feb ) 

6) Town Bank ( Should be working, testing to come with command implementation - 4 Feb ) 
(Seems to be working with recent changes - 18 Feb )

7) Taxes working in a basic fashion (Working well - 18 Feb ) 

8) Transactions reporting (Working very well 18 Feb) 
*_needs to have in game lookup commands added_*


Second Alpha
============

Add World Guard Region functionality

1) Define a 20x20 (configurable) region centered at command location

2) Flag the region for no-PVP and (maybe) no MobSpawn

3) Define 5x5 sub-regions with Restaurant and Hospital flags

4) Create border push mechanism

5) Cost mechanism applied to all these changes


First Beta
==========
1) Voting system

2) Democracy vs. Dictatorship 

3) Taxes

4) Rank Up system

5) Full permissions implementation (and testing)

Possibilities
=============
1) Dynmap Marker integration

2) Giant Shop Location set as a special region

3) Mail system and Addresses