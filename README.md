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
(SQLite working solid except for occasional on disable saves where DB locks.  MySQL not yet tested but believe may work - 18 Feb ) 

3) Works with money and player inventory
(Future: expand the DB for rankup item bank account and add to town class - 18 Feb )

4) Reads and writes global variables to config.yml 

5) Command Executors work for everything but world guard region functionality
(There may still be some bugs in this area)

6) Town Bank (/town bank, /mayor bank deposit, /mayor bank withdraw)
6a) Town Item Bank (/mayor itemBank)

7) Taxes working in a basic fashion (/town paytaxes, /mayor settax) 

8) Transactions reporting 
8a) Tax lookup (/deputy checkTaxes <playerName> ) 
9a) Bank transaction lookup (to be added) 


Second Alpha (*Development has started*)
============

Add World Guard Region functionality

1) Define a 20x20 (maybe configurable) region centered at command location (DONE!)

2) Flag the region for no-PVP and (maybe) no MobSpawn (maybe working) 

3) Define 5x5 sub-regions with Restaurant and Hospital flags (Skeleton in place)

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