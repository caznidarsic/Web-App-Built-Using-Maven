/* Copyright 2021
 * Student in Fall 2021 EN605.681.82
 * All rights reserved.
 */
package edu.jhu.rfs;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Contains the endpoint for the quote form to satisfy Homework 12 requirements
 * 
 */
@Path("Form")
public class FormEndpoint {
	
	/**
	 * API Endpoint to receive submissions from a quote form and generate 
	 * either an error message or the quote cost
	 * @param hikeIdStr the hike ID from the form
	 * @param startDateStr the start date from the form
	 * @param durationStr the duration from the form
	 * @param partySizeStr the party size from the form
	 * @return the HTML response page with the quote results
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String formToQuote(@FormParam("hikeId") String hikeIdStr, 
			@FormParam("startDate") String startDateStr, 
			@FormParam("duration") String durationStr, 
			@FormParam("partySize") String partySizeStr) {
		QuoteGenerator generator = new QuoteGenerator();
		
		return generator.processData(hikeIdStr, startDateStr, durationStr, partySizeStr);
	}
}
