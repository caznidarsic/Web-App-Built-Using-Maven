<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="edu.jhu.en605681.HikeType" %>
<%@ page import="edu.jhu.en605681.Rates" %>    
<%@ page import="edu.jhu.en605681.BookingDay" %>    

<!DOCTYPE html>
<html lang="en">
    <head> 
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Bryce Canyon Hiking Company Quotes</title>
        <link href="styles.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
        <header>
            <img src="https://webdev.jhuep.com/~spiegel/en605681/Images/Beartooth002-01.jpg" 
            alt="Mountains" class="header-img">
        </header>
        <main>
            <h1>Beartooth Hiking Company</h1>
            <h2>Quote Form</h2>
            <p>Welcome to Bryce Canyon Hiking Company! We're so happy you've 
            decided to book a hike with us. Please use the form below to select 
            the hike you'd like to do, set a date to depart, pick the duration 
            of your hike, and set the number of persons in your party before 
            pressing "Get Quote" to see the cost.
            <br/><br/>
             We are currently booking hikes between <%= BookingDay.DEFAULT_MIN_YEAR %> and <%= BookingDay.DEFAULT_MAX_YEAR %>.  
            To put in your starting date, use the format MM/DD/YYYY.</p>
            <form action="api/Form" method="post">
                <p>Hike: 
                    <select name="hikeId" id="hikeId">
						<% for (String s : HikeType.getHikeNames()) {  %>
						   <option value="<%=s %>"><%=s %></option> 
						<% } %>	                    
                    </select>
                </p>
                <p>Starting date: 
                    <input type="date" name="startDate" id="startDate">
                </p>
                <p>Duration (days):
                	<%  
                		int minDur = Integer.MAX_VALUE;
                		int maxDur = Integer.MIN_VALUE;
                		for (HikeType h : HikeType.values()) {
                		  for (int d : (new Rates(h).getDurations())) { 
                			  if (d >= maxDur) maxDur = d;
                			  if (d < minDur) minDur = d;
                		  }
                		}
                	 %>
                    <input type="number" name="duration" id="duration" min="<%= minDur %>"
                    max="<%= maxDur %>" value="<%= minDur %>">
                </p>
                <p>Party Size (1-10 persons):                
                    <input type="number" name="partySize" id="partySize" 
                    min="1" max="<%= new Rates(HikeType.values()[0]).getMaxHikers() %>" value="1">
                </p>
                <p><button type="SUBMIT">Get Quote</button></p>
            </form>
        </main>
    </body>
</html>