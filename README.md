Muni
====
A town management plugin for Bukkit Minecraft servers

http://dev.bukkit.org/server-mods/muni

Development Roadmap
===================
First Alpha ( Pre-Alpha 0.2 Released 4 Feb 2013 )
===========
1) Command executors responds with messages (Done a few days ago - 27 Jan) 

2) Town class stores and changes the town data and can read/write into a sql flat file ( Class created, needs to read / write to database - 27 Jan ) 
( Working pretty well with SQLite - 4 Feb ) 

3) Proof of working with economy (items taken directly from player's inventory) ( Working and somewhat tested - 27 Jan )
( Tested more extensively - 4 Feb ) 

4) Reads and writes global variables to config.yml (Done but needs testing - 27 Jan)
( Satisfied with operation - 4 Feb ) 

5) Command Executors work for everything but world guard region functionality
( This is the current stage of development - 4 Feb ) 

6) Town Bank ( Should be working, testing to come with command implementation - 4 Feb )

7) Taxes working in a basic fashion


Second Alpha
============

Add World Guard Region functionality

1) Define a 20x20 (configurable) region centered at command location

2) Flag the region for no-PVP and no MobSpawn

3) Define 5x5 sub-regions with Restaurant and Hospital flags

4) Create border push mechanism

5) Cost mechanism applied to all these changes


First Beta
==========
1) Voting system

2) Democracy vs. Dictatorship 

3) Taxes

4) Rank Up system

Possibilities
=============
1) Dynmap Marker integration

2) Giant Shop Location set as a special region

3) Mail system and Addresses