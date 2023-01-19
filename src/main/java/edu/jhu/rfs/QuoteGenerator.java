/* Copyright 2021
 * Student in Fall 2021 EN605.681.82
 * All rights reserved.
 */
package edu.jhu.rfs;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import edu.jhu.en605681.BookingDay;
import edu.jhu.en605681.HikeType;
import edu.jhu.en605681.Rates;

/**
 * Class to handle all of the logic related of validating input and generating 
 * the quote for the user.
 */
public class QuoteGenerator {
	
	/**
	 * Processes the data from the form input, validating and either returning 
	 * a quote or an error message
	 * @param hikeIdStr the hike ID from the input form
	 * @param startDateStr the start date from the input form
	 * @param durationStr the duration from the input form
	 * @param partySizeStr the party size from the input form
	 * @return the processed quote or error in HTML output
	 */
	public String processData(String hikeIdStr, String startDateStr, 
			String durationStr, String partySizeStr) {
		HikeType selectedHike = null;
		Rates rates = null;
		int duration = -1;
		int partySize = -1;
		LocalDate startDate = null;
		String errorMessage = "";
		String quoteMessage = "";
		
		// Get the selected hike
		for (HikeType h : HikeType.values()) {
			if (h.toString().equals(hikeIdStr)) { 
				selectedHike = h;
				break;
			}
		}
		if (selectedHike == null) 
			errorMessage = "Invalid input. Must choose a valid hike from the dropdown.";
		else
			rates = new Rates(selectedHike);
		
		// Get the duration
		try {			
			duration = Integer.parseInt(durationStr);
			if(rates != null) {
				List<Integer> validDurations = Arrays
	                    .stream(rates.getDurations()).boxed()
	                    .collect(Collectors.toList());
				if(!validDurations.contains(duration)) {
					errorMessage = "Duration is invalid for the selected hike. "
							+ "Options are: " + String.join(", ", 
									validDurations.stream()
									.map(v -> v.toString())
									.collect(Collectors.toList()));
				}
			}
		} catch (RuntimeException re) {
			errorMessage = "Invalid input. Duration is required and must be a number.";
		}
		
		try {
			partySize = Integer.parseInt(partySizeStr);
			if(partySize > (new Rates(HikeType.values()[0]).getMaxHikers()) || partySize < 1) {
				errorMessage = "Party Size must be between 1 and 10 persons.";
			}
		} catch (RuntimeException re) {
			errorMessage = "Invalid input. Party Size is required and must be between 1 and 10 persons.";
		}


		// Get the start date
		try {
			try {
				startDate = LocalDate.parse(startDateStr);
			} catch (DateTimeParseException dtpe) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
				startDate = LocalDate.parse(startDateStr, formatter);
			}
		} catch (RuntimeException re) {
			errorMessage = "Invalid input. Starting date is required and must be in the format MM/DD/YYYY";
		}
		
		// No errors so far?
		if(errorMessage.isEmpty()) {
			// Is the date in the future?
			if (!startDate.isAfter(LocalDate.now())) {
				errorMessage = "Sorry, you must choose a starting date after today for a quote.";
			} else {
				BookingDay bookingDay = new BookingDay(startDate.getYear(),
						startDate.getMonthValue(), 
						startDate.getDayOfMonth());

	            // Is the start date valid for hiking?
	            if(!bookingDay.isValidDate()) {
	                errorMessage = bookingDay.getValidation();
	            } else {
	                // Set up the Rate calculator
	                rates.setBeginDate(bookingDay);
	                rates.setDuration(duration);

	                // Are the hikes valid dates, and did the cost calculate?
	                if(rates.isValidDates() && rates.getCost() != -0.01d) {
	                    NumberFormat currency = NumberFormat
	                            .getCurrencyInstance(Locale.US);

	                    // Create message based on input
	                    String weekdayRate = String.format(
	                            "<br>%d weekday%s at %s/person each = %s",
	                            rates.getNormalDays(),
	                            (rates.getNormalDays() != 1 ? "s" : ""),
	                            currency.format(rates.getBaseRate()),
	                            currency.format(rates.getBaseRate()
	                                    * rates.getNormalDays() * partySize));
	                    String weekendRate = String.format(
	                            "<br>%d weekend day%s at %s/person each = %s",
	                            rates.getPremiumDays(),
	                            (rates.getPremiumDays() != 1 ? "s" : ""),
	                            currency.format(rates.getPremiumRate()),
	                            currency.format(rates.getPremiumDays()
	                                    * rates.getPremiumRate() * partySize));
	                    quoteMessage = String.format(
	                            "Your selected hike, %s for %d day%s, has a " +
	                                    "total cost of <b>%s</b><br>%s%s" +
	                                    "<br><br>Please call us to book!",
	                            selectedHike, duration, 
	                            (duration != 1 ? "s" : ""),
	                            currency.format(rates.getCost()),
	                            (rates.getNormalDays() > 0 ? weekdayRate : ""),
	                            (rates.getPremiumDays() > 0 ? weekendRate : ""));

	                    // Show user the quote
	                    return wrapInHTML(quoteMessage, false);
	                } else {
	                    // Show user the error message
	                    errorMessage = "Your quote could not be generated " +
	                                    "because: " + rates.getDetails();
	                }
	            }
			}
		}
		
		return wrapInHTML(errorMessage, true);
	}
	
	/**
	 * Wraps either the error message or quote in HTML
	 * @param message the message to output
	 * @param isError if the message was an error message
	 * @return HTML response to send via the API call
	 */
	private String wrapInHTML(String message, boolean isError) {
		StringBuilder sb = new StringBuilder();
		
		// Build HTML response
		sb.append("<!DOCTYPE html>\n");
		sb.append("<html lang=\"en\">\n");
		sb.append("    <head> \n");
		sb.append("        <meta charset=\"UTF-8\">\n");
		sb.append("        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n");
		sb.append("        <title>Bryce Canyon Hiking Company Quotes</title>\n");
		sb.append("        <link href=\"../styles.css\" rel=\"stylesheet\" type=\"text/css\" />\n");
		sb.append("    </head>\n");
		sb.append("    <body>\n");
		sb.append("        <header>\n");
		sb.append("            <img src=\"https://webdev.jhuep.com/~spiegel/en605681/Images/Beartooth002-01.jpg\" \n");
		sb.append("            alt=\"Mountains\" class=\"header-img\">\n");
		sb.append("        </header>\n");
		sb.append("        <main>\n");
		sb.append("            <h1>Bryce Canyon Hiking Company</h1>\n");
		sb.append("            <h2>Quote Form</h2>\n");
		
		// Error or success?
		if(isError) {
			sb.append("        <div class=\"alert error-alert\">Error: ");
		} else {
			sb.append("        <div class=\"alert success-alert\">");
		}
		sb.append(message);
		sb.append("</div>\n");
		sb.append("            <p><a href=\"javascript:history.back()\">Go Back</a></p>\n");
		sb.append("        </main>\n");
		sb.append("    </body>\n");
		sb.append("</html>");
		
		return sb.toString();
	}
}
