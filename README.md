# transpool-web
Carpool Management Web Application
 - Build the project using maven.
 
Explanation about the app:
--------------------------
   
- Login page: you choose role and name for your user.
- User page: you will see table of all uploaded maps – this table are clickable so by clicking on any map you are offered to go to this map page, and another table of all your actions.
- You can deposit money by enter any amount, and also upload map by using xml file(you have example maps under xml files folder).
- Map page: 
   - Visual of the specific map.
   - Table for all the requests trips in the system.
   - Table for all the offers trips in the system.
   - This tables are clickable – so by clicking twice on each row, you will see for the requests table, the information about the match if matched , and for offer’s table you will see the route ,and attached passengers.
   - You have a few buttons at this page:
       - Match- offers you to find a match for any request.
       - Rank- offers you to rank and feedback your drivers (you can rank any driver only once).
       - Show feedback- allows you to see all your feedbacks.
       - Add trip- allows you to add new trip to any map . for requesting trip you can choose flexibility ,which determines how many hours you are willing to wait from your original time, and option to split or not to split your trip to different drivers.
  - Alerts: alerts will prompt at any page when rank or match taking place.
