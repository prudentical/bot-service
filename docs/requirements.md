# # Bot service 
## Functional requirements
- Create different bots (strategies)
- Each bot must connect to an account
- Configuration of a bot must be immutable
- Each account connect to a bot must have `API_KEY` for non-virtual/historic run
- Control life cycle of a bot `created`/`active`/`stop`(liquidate)/`delete`(logical)
- Bots that crash must be restored immediately
- Bot must be able to run in virtual mode (Paper trading)
- Bot must be able to run against historic data (show PNL,order count, etc)