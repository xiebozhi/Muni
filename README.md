Muni
====
Provides town management for Bukkit Minecraft servers

http://dev.bukkit.org/server-mods/muni

Development Roadmap
===================
First Alpha ( Alpha v0.11 Released 26 Feb 2013 )
===========
1) Command executors responds with messages 

2) Town class stores and changes the town data and can read/write into a sql flat file

3) Works with money and player inventory

4) Reads and writes global variables to config.yml 

5) Command Executors work for everything but world guard region functionality

6) Town Bank (/town bank, /mayor bank deposit, /mayor bank withdraw)
6a) Town Item Bank (/mayor itemBank)

7) Taxes working in a basic fashion (/town paytaxes, /mayor settax) 

8 ) Transactions reporting 
8a) Tax lookup (/deputy checkTaxes <playerName> ) 
9a) Bank transaction lookup (to be added) 


Second Alpha (*Alpha v0.20b released 5 March 2013*)
============

Add World Guard Region functionality

1) Define a 20x20 (maybe configurable) region centered at command location (done)

2) Flag the region for no-PVP and (maybe) no MobSpawn (maybe working) 

3) Define 5x5 sub-regions with Restaurant and Hospital flags (Mostly working)

4) Create border push mechanism (Mostly working)

5) Cost mechanism applied to all these changes (Mostly working but not displaying effectively)

6) MySQL functionality added and needs testing 


First Beta
==========
1) Voting system (Heart beat is started and hits every half hour)

2) Democracy vs. Dictatorship 

3) Taxes

4) Rank Up system

5) Full permissions implementation (and testing)

Possibilities
=============
1) Dynmap Marker integration

2) Giant Shop Location set as a special region

3) Mail system and Addresses